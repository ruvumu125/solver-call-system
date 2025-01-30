package com.solver.callbutton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.solver.callbutton.Model.ApiErrorResponse;
import com.solver.callbutton.Model.LoginRequest;
import com.solver.callbutton.Model.LoginResponse;
import com.solver.callbutton.Model.User;
import com.solver.callbutton.api.ApiService;
import com.solver.callbutton.api.LoginService;
import com.solver.callbutton.api.Retrofit2Client;
import com.solver.callbutton.dialog.ErrorDialogFragment;
import com.solver.callbutton.utils.LoginPreferencesManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginPreferencesManager loginPreferencesManager;
    private TextInputEditText et_email,et_password;

    private ConstraintLayout btn_next;
    private CardView btn_sign_in_with_google;
    private ConstraintLayout loadingCartGif;
    private TextView tvContinue;
    private Boolean valid = true;
    private Retrofit2Client retrofit2Client;
    private LoginService loginService;
    private ApiService apiService;
    private Button btn_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the preferences manager
        loginPreferencesManager = new LoginPreferencesManager(this);
        // Check if the user is logged in
        if (loginPreferencesManager.isLoggedIn()) {
            // If logged in, redirect to MainActivity
            redirectToMainActivity();
            return; // Stop further execution of onCreate
        }

        setContentView(R.layout.activity_login);

        retrofit2Client=new Retrofit2Client(getApplicationContext());
        loginService= retrofit2Client.createService(LoginService.class);
        apiService = retrofit2Client.createService(ApiService.class);
        loginPreferencesManager= new LoginPreferencesManager(this);

        et_email= findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        btn_forgot_password=findViewById(R.id.btn_forgot_password);

        btn_next = (ConstraintLayout) findViewById(R.id.btn_next);
        btn_sign_in_with_google= (CardView) findViewById(R.id.btn_sign_in_with_google);
        loadingCartGif = (ConstraintLayout) findViewById(R.id.loadingCartGif);
        tvContinue = (TextView) findViewById(R.id.continueBtn);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingCartGif.setVisibility(View.VISIBLE);
                btn_next.setEnabled(false);
                tvContinue.setText("");

                if (TextUtils.isEmpty(et_email.getText().toString())) {
                    valid = false;

                    loadingCartGif.setVisibility(View.GONE);
                    btn_next.setEnabled(true);
                    tvContinue.setText("Continuer");

                    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                    dialog.setTitle("Info !");
                    dialog.setMessage("Veuillez saisir l'email ");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = dialog.create();
                    alert.show();

                } else {

                    valid = true;
                    if (TextUtils.isEmpty(et_password.getText().toString())){
                        valid = false;

                        loadingCartGif.setVisibility(View.GONE);
                        btn_next.setEnabled(true);
                        tvContinue.setText("Continuer");

                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                        dialog.setTitle("Info !");
                        dialog.setMessage("Veuillez saisir le mot de passe ");
                        dialog.setCancelable(true);

                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert = dialog.create();
                        alert.show();

                    }
                }

                if(valid){

                    String username = et_email.getText().toString();
                    String password = et_password.getText().toString();
                    login(username, password);
                }

            }
        });

        btn_sign_in_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), RegisterActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(view.getContext(), YoutubeActivity.class);
//                view.getContext().startActivity(intent);

                Intent intent = new Intent(view.getContext(), PasswordResetActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish LoginActivity to prevent going back to it
    }

    private void login(String email, String password) {
        // Create login request
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Call the login API
        loginService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse.Data data = response.body().getData();
                    String token = data.getToken();
                    User user = data.getUser();

                    if (!user.getUser_type().equals("Occupier")){
                        displayErrorMessage("Vous n'êtes pas autorisé à utiliser ce service");
                    }

                    // Obtain the FCM token
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("TAG", "Fetching FCM token failed", task.getException());
                                        return;
                                    }

                                    // Get the FCM token
                                    String fcmToken  = task.getResult();
                                    Log.d("FFFF", "FCM Token: " + fcmToken);

                                    user.setFcm_token(fcmToken);

                                    // Send the token to your server
                                    sendTokenToServer(user.getId(),fcmToken);

                                    loginPreferencesManager.saveToken(token);
                                    loginPreferencesManager.saveUser(user);

                                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();


                                }
                            });



                } else {

                    loadingCartGif.setVisibility(View.GONE);
                    btn_next.setEnabled(true);
                    tvContinue.setText("Continuer");

                    // Parse the error response
                    Gson gson = new Gson();
                    ApiErrorResponse apiErrorResponse = gson.fromJson(response.errorBody().charStream(), ApiErrorResponse.class);

                    // Display the error message if it's not null
                    if (apiErrorResponse != null) {
                        displayErrorMessage(apiErrorResponse.getData().getError());
                    }




                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingCartGif.setVisibility(View.GONE);
                btn_next.setEnabled(true);
                tvContinue.setText("Continuer");
            }
        });
    }

    private void sendTokenToServer(Long id, String fcmToken) {

        // Prepare the FCM token data
        Map<String, String> fcmTokenData = new HashMap<>();
        fcmTokenData.put("fcm_token",fcmToken); // Replace "aq" with your actual FCM token

        Call<Void> call = apiService.updateFcmToken(id, fcmTokenData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    System.out.println("FCM token updated successfully.");
                } else {
                    System.out.println("Failed to update FCM token. HTTP status: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log and display the failure message
                String failureMessage = "Error sending FCM token: " + t.getMessage();
            }
        });
    }

    public void displayErrorMessage(String message) {

        ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment(new ErrorDialogFragment.CallBackListener() {
            @Override
            public void onOkClick() {

            }
        });

        Bundle args = new Bundle();
        args.putString("ERROR_MESSAGE", message);
        errorDialogFragment.setArguments(args);

        errorDialogFragment.show(getSupportFragmentManager(), "ErrorDialog");
    }



}