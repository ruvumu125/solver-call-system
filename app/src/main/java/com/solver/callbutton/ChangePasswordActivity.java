package com.solver.callbutton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.solver.callbutton.Model.ApiErrorResponse;
import com.solver.callbutton.Model.LoginRequest;
import com.solver.callbutton.Model.LoginResponse;
import com.solver.callbutton.Model.PasswordChangeApiResponse;
import com.solver.callbutton.Model.PasswordUpdateRequest;
import com.solver.callbutton.Model.User;
import com.solver.callbutton.api.ApiService;
import com.solver.callbutton.api.LoginService;
import com.solver.callbutton.api.Retrofit2Client;
import com.solver.callbutton.dialog.ErrorDialogFragment;
import com.solver.callbutton.dialog.SuccessDialogFragment;
import com.solver.callbutton.utils.LoginPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private LoginPreferencesManager loginPreferencesManager;
    private TextInputEditText et_old_password,et_new_password,et_confirm_new_password;

    private ConstraintLayout btn_next;
    private ConstraintLayout loadingCartGif;
    private TextView tvContinue;
    private Boolean valid = true;
    private Retrofit2Client retrofit2Client;
    private LoginService loginService;
    private ImageButton btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        retrofit2Client=new Retrofit2Client(getApplicationContext());
        loginService= retrofit2Client.createService(LoginService.class);
        loginPreferencesManager= new LoginPreferencesManager(this);

        et_old_password= findViewById(R.id.et_old_password);
        et_new_password= findViewById(R.id.et_new_password);
        et_confirm_new_password= findViewById(R.id.et_confirm_new_password);

        btn_next = (ConstraintLayout) findViewById(R.id.btn_next);
        loadingCartGif = (ConstraintLayout) findViewById(R.id.loadingCartGif);
        tvContinue = (TextView) findViewById(R.id.continueBtn);
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingCartGif.setVisibility(View.VISIBLE);
                btn_next.setEnabled(false);
                tvContinue.setText("");

                if (TextUtils.isEmpty(et_old_password.getText().toString())) {
                    valid = false;

                    loadingCartGif.setVisibility(View.GONE);
                    btn_next.setEnabled(true);
                    tvContinue.setText("Sauvegarder");

                    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                    dialog.setTitle("Info !");
                    dialog.setMessage("Veuillez saisir votre mot de passe actuel ");
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
                    if (TextUtils.isEmpty(et_new_password.getText().toString())){
                        valid = false;

                        loadingCartGif.setVisibility(View.GONE);
                        btn_next.setEnabled(true);
                        tvContinue.setText("Sauvegarder");

                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                        dialog.setTitle("Info !");
                        dialog.setMessage("Veuillez saisir le nouveau mot de passe ");
                        dialog.setCancelable(true);

                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert = dialog.create();
                        alert.show();

                    }
                    else{

                        valid=true;

                        if (TextUtils.isEmpty(et_confirm_new_password.getText().toString())){
                            valid = false;

                            loadingCartGif.setVisibility(View.GONE);
                            btn_next.setEnabled(true);
                            tvContinue.setText("Sauvegarder");

                            AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                            dialog.setTitle("Info !");
                            dialog.setMessage("Veuillez confirmer le nouveau mot de passe ");
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
                }

                if(valid){

                    String current_password = et_old_password.getText().toString();
                    String new_password = et_new_password.getText().toString();
                    String new_password_confirmation= et_confirm_new_password.getText().toString();
                    changePassword(current_password, new_password, new_password_confirmation);
                }

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void changePassword(String current_password, String new_password, String new_password_confirmation) {
        // Create login request
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(current_password,new_password, new_password_confirmation);

        // Call the login API
        loginService.updatePassword(passwordUpdateRequest).enqueue(new Callback<PasswordChangeApiResponse>() {
            @Override
            public void onResponse(Call<PasswordChangeApiResponse> call, Response<PasswordChangeApiResponse> response) {

                if (response.isSuccessful()) {

                    SuccessDialogFragment successDialogFragment = new SuccessDialogFragment(new SuccessDialogFragment.CallBackListener() {
                        @Override
                        public void onOkClick() {

                            loginPreferencesManager.logout();

                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    // Create a Bundle to pass the argument
                    Bundle args = new Bundle();
                    args.putString("success_message", "Votre mot de passe à été changé avec succès");
                    successDialogFragment.setArguments(args);
                    successDialogFragment.show(getSupportFragmentManager(), "SuccessDialog");



                } else {

                    loadingCartGif.setVisibility(View.GONE);
                    btn_next.setEnabled(true);
                    tvContinue.setText("Sauvegarder");

                    Gson gson = new Gson();
                    ApiErrorResponse apiErrorResponse = gson.fromJson(response.errorBody().charStream(), ApiErrorResponse.class);

                    // Handle the validation error or unauthorized error
                    if (apiErrorResponse.getData().getNew_password() != null) {
                        // Handle validation error (e.g., password confirmation mismatch)
                        String errorMessage = apiErrorResponse.getData().getNew_password().get(0);
                        displayErrorMessage(errorMessage);

                    } else if (apiErrorResponse.getData().getError() != null) {
                        // Handle authorization error (e.g., incorrect current password)
                        String errorMessage = apiErrorResponse.getData().getError();
                        displayErrorMessage(errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<PasswordChangeApiResponse> call, Throwable t) {

                Log.d("nnn",t.getMessage());

                loadingCartGif.setVisibility(View.GONE);
                btn_next.setEnabled(true);
                tvContinue.setText("Sauvegarder");
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