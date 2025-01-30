package com.solver.callbutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.solver.callbutton.Model.HelpRequest;
import com.solver.callbutton.Model.User;
import com.solver.callbutton.Model.Video;
import com.solver.callbutton.Model.VideoApiResponse;
import com.solver.callbutton.api.HelpRequestService;
import com.solver.callbutton.api.Retrofit2Client;
import com.solver.callbutton.api.VideoService;
import com.solver.callbutton.dialog.ConfirmCallDialogFragment;
import com.solver.callbutton.dialog.ConfirmLogoutDialogFragment;
import com.solver.callbutton.dialog.SuccessDialogFragment;
import com.solver.callbutton.utils.LoginPreferencesManager;
import com.solver.callbutton.utils.SharedPreferencesManagement;
import com.solver.callbutton.utils.VideoSharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.PowerButtonReceiver;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private ImageView imageView1;
    private ImageView imageView2;
    private Retrofit2Client retrofit2Client;
    private HelpRequestService helpRequestService;
    private LoginPreferencesManager loginPreferencesManager;

    private DrawerLayout mDrawerLayout;
    private TextView navHeaderFullName, navHeaderUserRole, navHeaderUserCompany;
    private Button btnLogout, btnTryAgain;
    private ConstraintLayout changePassword;
    private ImageButton rightCornerText;

    private VideoService videoService;
    private RelativeLayout relativeLayoutLoading, relativeLayoutNetworkErrorLayout, noDataFoundLayout;
    private VideoSharedPreferences videoSharedPreferences;
    private SharedPreferencesManagement sharedPreferencesManagement;
    private PowerButtonReceiver powerButtonReceiver;
    private static final int REQUEST_CALL_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeViews();
        initializeDependencies();

        setUpNavigationDrawer();
        setUpSurfaceView();
        handleImageViewClick();
        handleCall();
        handleTryAgain();
        handleLogout();
        findActiveVideo();

    }

    private void makePhoneCall(String phoneNumber) {
        // Check if the CALL_PHONE permission is granted
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Start the call intent
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } else {
            // Request the permission
            requestCallPermission();
        }
    }

    private void requestCallPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
            Toast.makeText(this, "Call permission is needed to make a phone call", Toast.LENGTH_SHORT).show();
        }
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Try calling again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Cannot make the call.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            // If the MediaPlayer is already created and paused, start it again
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
        else{
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Pause playback
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(); // Release resources
    }

    private void initializeViews() {
        surfaceView = findViewById(R.id.surfaceView);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        rightCornerText = findViewById(R.id.rightCornerText);
        navHeaderFullName = findViewById(R.id.nav_header_name);
        navHeaderUserRole = findViewById(R.id.nav_header_user);
        navHeaderUserCompany = findViewById(R.id.nav_header_role);
        changePassword = findViewById(R.id.change_password);
        btnLogout = findViewById(R.id.btn_logout);
        mDrawerLayout = findViewById(R.id.drawer_main_activity);
        relativeLayoutLoading = findViewById(R.id.rlLoading);
        relativeLayoutNetworkErrorLayout = findViewById(R.id.network_error_layout);
        noDataFoundLayout = findViewById(R.id.no_result_found_layout);
        btnTryAgain = findViewById(R.id.btn_try_again);
    }

    private void initializeDependencies() {
        loginPreferencesManager = new LoginPreferencesManager(this);
        sharedPreferencesManagement = new SharedPreferencesManagement(getApplicationContext());
        videoSharedPreferences = new VideoSharedPreferences(getApplicationContext());

        User user = loginPreferencesManager.getUser();
        navHeaderFullName.setText(user.getFirst_name() + " " + user.getLast_name());
        navHeaderUserRole.setText(user.getPhone_number());
        navHeaderUserCompany.setText(user.getUser_type());

        retrofit2Client = new Retrofit2Client(getApplicationContext());
        videoService = retrofit2Client.createService(VideoService.class);
        helpRequestService = retrofit2Client.createService(HelpRequestService.class);

        mediaPlayer = new MediaPlayer();
    }

    private void setUpNavigationDrawer() {
        rightCornerText.setOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.END));
        changePassword.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(GravityCompat.END);
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });
    }

    private void setUpSurfaceView() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                playVideo(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseMediaPlayer();
            }
        });
    }


    private void playVideo(SurfaceHolder holder) {
        new Thread(() -> {
            try {
                // Prepare the video URL
                String videoUrl = "https://office.solverexpress.com/storage/" +
                        videoSharedPreferences.getVideo("video_shared_preferences_solver_office").getFile_data();

                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(videoUrl));
                mediaPlayer.setDisplay(holder);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepareAsync(); // This will not block the UI thread

                mediaPlayer.setOnPreparedListener(mp -> {
                    // When the video is ready to play, start the video
                    runOnUiThread(() -> {

                        mediaPlayer.start();
                        surfaceView.setVisibility(View.VISIBLE);
                        Log.d("MainActivity", "Video playback started");
                        relativeLayoutLoading.setVisibility(View.GONE); // Hide loading spinner
                    });
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e("MainActivity", "Error playing video");
                    runOnUiThread(() -> {
                        // Handle error and ensure UI is still interactive
                        surfaceView.setVisibility(View.GONE);
                        relativeLayoutLoading.setVisibility(View.GONE);
                        relativeLayoutNetworkErrorLayout.setVisibility(View.VISIBLE); // Show error layout
                        Toast.makeText(MainActivity.this, "Failed to play video", Toast.LENGTH_SHORT).show();
                    });
                    return true;
                });

            } catch (Exception e) {
                Log.e("MainActivity", "Error starting video playback", e);
                runOnUiThread(() -> {
                    // Show network error or fallback UI if the video can't be loaded
                    surfaceView.setVisibility(View.GONE);
                    relativeLayoutLoading.setVisibility(View.GONE);
                    relativeLayoutNetworkErrorLayout.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("MainActivity", "MediaPlayer released");
            // Ensure surfaceView and other UI elements are visible after releasing the media player
            runOnUiThread(() -> surfaceView.setVisibility(View.GONE));
        }
    }


    private void handleImageViewClick() {
        Glide.with(this).load(R.drawable.pngwing).into(imageView1);
        imageView1.setOnClickListener(v -> {
            addHelpRequest(loginPreferencesManager.getUser().getId());
        });
    }
    private void handleCall(){
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConfirmCallDialogFragment dialogFragment = new ConfirmCallDialogFragment(() -> {

                    User user = loginPreferencesManager.getUser();
                    String agent_phone_number=user.getAgent_phone_number();
                    makePhoneCall(agent_phone_number);
                });
                dialogFragment.show(getSupportFragmentManager(), "dialog_call_agent");


            }
        });
    }
    private void handleTryAgain(){
        btnTryAgain.setOnClickListener(v -> {
            recreate();
        });
    }

    private void handleLogout() {
        btnLogout.setOnClickListener(v -> {
            mDrawerLayout.closeDrawer(GravityCompat.END);
            ConfirmLogoutDialogFragment dialogFragment = new ConfirmLogoutDialogFragment(() -> {
                loginPreferencesManager.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
            dialogFragment.show(getSupportFragmentManager(), "dialog_logout");
        });
    }

    private void findActiveVideo() {

        relativeLayoutLoading.setVisibility(View.VISIBLE);

        videoService.findActiveVideo().enqueue(new Callback<VideoApiResponse>() {
            @Override
            public void onResponse(Call<VideoApiResponse> call, Response<VideoApiResponse> response) {
                if (response.isSuccessful()) {
                    handleVideoResponse(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VideoApiResponse> call, Throwable t) {
                showErrorLayout();
                Log.e("MainActivity", "Request failed: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Request failed:", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addHelpRequest(Long user_id) {
        Glide.with(this)
                .load(R.drawable.gifsolv)
                .into(imageView1);

        HelpRequest helpRequest = new HelpRequest(user_id);

        helpRequestService.postHelpRequest(helpRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.pngwing)
                            .into(imageView1);

                    SuccessDialogFragment successDialogFragment = new SuccessDialogFragment(() -> {
                        // Handle OK click
                    });
                    Bundle args = new Bundle();
                    args.putString("success_message", "L'agent arrive dans un instant");
                    successDialogFragment.setArguments(args);
                    successDialogFragment.show(getSupportFragmentManager(), "SuccessDialog");
                } else {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.pngwing)
                            .into(imageView1);
                    Toast.makeText(getApplicationContext(), "Request failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Glide.with(getApplicationContext())
                        .load(R.drawable.pngwing)
                        .into(imageView1);
                Log.e("MainActivity", "Help request failed: " + t.getMessage(), t);
            }
        });
    }

    private void handleVideoResponse(VideoApiResponse apiResponse) {
        if (apiResponse != null && apiResponse.isSuccess()) {
            Video video = apiResponse.getData();
            if (video == null) {
                showNoDataLayout();
            } else {
                showVideoLayout(video);
            }
        }
    }

    private void showErrorLayout() {
        relativeLayoutLoading.setVisibility(View.VISIBLE);
        relativeLayoutNetworkErrorLayout.setVisibility(View.GONE);
        surfaceView.setVisibility(View.GONE);
    }

    private void showNoDataLayout() {
        relativeLayoutNetworkErrorLayout.setVisibility(View.GONE);
        noDataFoundLayout.setVisibility(View.GONE);
        relativeLayoutLoading.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.GONE);
    }

    private void showVideoLayout(Video video) {
        relativeLayoutNetworkErrorLayout.setVisibility(View.GONE);
        noDataFoundLayout.setVisibility(View.GONE);
        relativeLayoutLoading.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
        videoSharedPreferences.saveListInLocal(video, "video_shared_preferences_solver_office");
    }


}