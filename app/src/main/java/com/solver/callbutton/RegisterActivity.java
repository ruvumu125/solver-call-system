package com.solver.callbutton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.solver.callbutton.Model.ApiErrorResponse;
import com.solver.callbutton.Model.Occupier;
import com.solver.callbutton.Model.ValidationErrorResponse;
import com.solver.callbutton.api.ApiService;
import com.solver.callbutton.api.LoginService;
import com.solver.callbutton.api.OccupierService;
import com.solver.callbutton.api.Retrofit2Client;
import com.solver.callbutton.dialog.SuccessDialogFragment;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextView nom_company,nom_office;
    private LinearLayout ll_address_add_company,ll_add_office;
    private final int OPEN_NEW_ACTIVITY_COMPANY=1;
    private final int OPEN_NEW_ACTIVITY_OFFICE=2;
    private String ID_COMPANY,NOM_COMPANY,ID_OFFICE,NOM_OFFICE;
    private TextInputEditText et_first_name,et_last_name,et_mail,et_phone_number,et_password,et_confirm_password;

    private ConstraintLayout btn_next;
    private CardView btn_sign_in_with_google;
    private ConstraintLayout loadingCartGif;
    private TextView tvContinue;

    private Boolean valid = true;
    private Retrofit2Client retrofit2Client;
    private OccupierService occupierService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_first_name=findViewById(R.id.et_first_name);
        et_last_name=findViewById(R.id.et_last_name);
        et_mail=findViewById(R.id.et_mail);
        et_phone_number=findViewById(R.id.et_phone_number);
        et_password=findViewById(R.id.et_password);
        et_confirm_password=findViewById(R.id.et_confirm_password);


        ll_address_add_company=findViewById(R.id.ll_add_company);
        ll_add_office=findViewById(R.id.ll_add_office);
        nom_company=findViewById(R.id.tv_add_company);
        nom_office=findViewById(R.id.tv_add_office);

        retrofit2Client=new Retrofit2Client(getApplicationContext());
        occupierService=retrofit2Client.createService(OccupierService.class);

        btn_next = (ConstraintLayout) findViewById(R.id.btn_next);
        loadingCartGif = (ConstraintLayout) findViewById(R.id.loadingCartGif);
        tvContinue = (TextView) findViewById(R.id.continueBtn);

        ll_address_add_company.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 selectCompany();
             }
        });

        ll_add_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               selectOffice();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingCartGif.setVisibility(View.VISIBLE);
                btn_next.setEnabled(false);
                tvContinue.setText("");

                if (TextUtils.isEmpty(et_first_name.getText().toString())) {
                    valid = false;

                    loadingCartGif.setVisibility(View.GONE);
                    btn_next.setEnabled(true);
                    tvContinue.setText("Continuer");

                    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                    dialog.setTitle("Info !");
                    dialog.setMessage("Veuillez saisir votre nom ");
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
                    if (TextUtils.isEmpty(et_last_name.getText().toString())){
                        valid = false;

                        loadingCartGif.setVisibility(View.GONE);
                        btn_next.setEnabled(true);
                        tvContinue.setText("Continuer");

                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                        dialog.setTitle("Info !");
                        dialog.setMessage("Veuillez saisir votre prénom ");
                        dialog.setCancelable(true);

                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert = dialog.create();
                        alert.show();

                    }
                    else {
                        valid=true;
                        if (TextUtils.isEmpty(et_mail.getText().toString())){

                            valid=false;

                            loadingCartGif.setVisibility(View.GONE);
                            btn_next.setEnabled(true);
                            tvContinue.setText("Continuer");

                            AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                            dialog.setTitle("Info !");
                            dialog.setMessage("Veuillez saisir votre email ");
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

                            if (TextUtils.isEmpty(et_phone_number.getText().toString())){

                                valid=false;

                                loadingCartGif.setVisibility(View.GONE);
                                btn_next.setEnabled(true);
                                tvContinue.setText("Continuer");

                                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                                dialog.setTitle("Info !");
                                dialog.setMessage("Veuillez saisir votre numéro de téléphone ");
                                dialog.setCancelable(true);

                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                                AlertDialog alert = dialog.create();
                                alert.show();



                            }
                            else {
                                valid=true;

                                if (TextUtils.isEmpty(et_password.getText().toString())){

                                    valid=false;

                                    loadingCartGif.setVisibility(View.GONE);
                                    btn_next.setEnabled(true);
                                    tvContinue.setText("Continuer");

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                                    dialog.setTitle("Info !");
                                    dialog.setMessage("Veuillez saisir votre mot de passe ");
                                    dialog.setCancelable(true);

                                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                                    AlertDialog alert = dialog.create();
                                    alert.show();



                                }
                                else {
                                    valid=true;

                                    if (TextUtils.isEmpty(et_confirm_password.getText().toString())){

                                        valid=false;

                                        loadingCartGif.setVisibility(View.GONE);
                                        btn_next.setEnabled(true);
                                        tvContinue.setText("Continuer");

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                                        dialog.setTitle("Info !");
                                        dialog.setMessage("Veuillez saisir confirmer votre mot de passe ");
                                        dialog.setCancelable(true);

                                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                        AlertDialog alert = dialog.create();
                                        alert.show();
                                    }
                                    else {

                                        valid =true;

                                        if (TextUtils.isEmpty(nom_company.getText().toString())){

                                            valid=false;

                                            loadingCartGif.setVisibility(View.GONE);
                                            btn_next.setEnabled(true);
                                            tvContinue.setText("Continuer");

                                            AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                                            dialog.setTitle("Info !");
                                            dialog.setMessage("Veuillez choisir une entreprise");
                                            dialog.setCancelable(true);

                                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                            AlertDialog alert = dialog.create();
                                            alert.show();
                                        }
                                        else {

                                            valid=true;

                                            if (TextUtils.isEmpty(nom_office.getText().toString())){

                                                valid=false;

                                                loadingCartGif.setVisibility(View.GONE);
                                                btn_next.setEnabled(true);
                                                tvContinue.setText("Continuer");

                                                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                                                dialog.setTitle("Info !");
                                                dialog.setMessage("Veuillez choisir un bureau");
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
                                }

                            }
                        }
                    }
                }

                if(valid){

                    String first_name = et_first_name.getText().toString();
                    String last_name = et_last_name.getText().toString();
                    String email=et_mail.getText().toString();
                    String phone_number=et_phone_number.getText().toString();
                    String password=et_password.getText().toString();
                    String confirm_password=et_confirm_password.getText().toString();
                    Long office_id=Long.parseLong(ID_OFFICE);

                    registrer(first_name,last_name,email,phone_number,password,confirm_password,office_id);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == OPEN_NEW_ACTIVITY_COMPANY){

            if(resultCode == Activity.RESULT_OK){

                String result_id_company=data.getStringExtra("ID_COMPANY");
                String result_nom_company=data.getStringExtra("NOM_COMPANY");

                nom_company.setText(result_nom_company);
                ID_COMPANY=result_id_company;
                NOM_COMPANY=result_nom_company;


            }
        }
        else if (requestCode == OPEN_NEW_ACTIVITY_OFFICE){

            String result_id_office=data.getStringExtra("ID_OFFICE");
            String result_nom_office=data.getStringExtra("NOM_OFFICE");

            nom_office.setText(result_nom_office);
            ID_OFFICE=result_id_office;
            NOM_OFFICE=result_nom_office;
        }

    }

    private void selectCompany(){
        Intent intentProvince= new Intent(getApplicationContext(), CompanyListActivity.class);
        startActivityForResult(intentProvince,OPEN_NEW_ACTIVITY_COMPANY);
    }

    private void selectOffice(){
        Intent intentDestination = new Intent(getApplicationContext(), OfficeListActivity.class);
        intentDestination.putExtra("ID_COMPANY",ID_COMPANY);
        startActivityForResult(intentDestination,OPEN_NEW_ACTIVITY_OFFICE);
    }

    public void registrer(String first_name,String last_name,String email,String phone_number,String password,String confirm_password,Long office_id) {

        Occupier occupier = new Occupier (first_name,last_name,email,phone_number,password,confirm_password,office_id);

        Call<Void> call = occupierService.register(occupier);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    loadingCartGif.setVisibility(View.GONE);
                    btn_next.setEnabled(true);
                    tvContinue.setText("Continuer");

                    SuccessDialogFragment successDialogFragment = new SuccessDialogFragment(new SuccessDialogFragment.CallBackListener() {
                        @Override
                        public void onOkClick() {

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    // Create a Bundle to pass the argument
                    Bundle args = new Bundle();
                    args.putString("success_message", "Compte crée avec succès");
                    successDialogFragment.setArguments(args);
                    successDialogFragment.show(getSupportFragmentManager(), "SuccessDialog");


                } else {
                    // Handle error response, response code is not 2xx
                    Log.e("API Error", "Response code: " + response.code());
                    Toast.makeText(RegisterActivity.this, "nmmmmmmmm", Toast.LENGTH_SHORT).show();

                    loadingCartGif.setVisibility(View.GONE);
                    btn_next.setEnabled(true);
                    tvContinue.setText("Continuer");

                    handleValidationError(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                Log.e("API Failure", t.getMessage());

                loadingCartGif.setVisibility(View.GONE);
                btn_next.setEnabled(true);
                tvContinue.setText("Continuer");

                Toast.makeText(RegisterActivity.this, "ngwahooo", Toast.LENGTH_SHORT).show();

                //displayErrorMessage(t.getMessage());
            }
        });

    }

    private void handleValidationError(Response<?> response) {
        try {
            String errorBody = response.errorBody().string(); // Raw error response
            Gson gson = new Gson();
            ValidationErrorResponse errorResponse = gson.fromJson(errorBody, ValidationErrorResponse.class);

            if (errorResponse != null && errorResponse.getData() != null) {
                // Build a dynamic error message
                StringBuilder errorMessages = new StringBuilder("Validation Errors:\n");

                for (Map.Entry<String, List<String>> entry : errorResponse.getData().entrySet()) {
                    String field = entry.getKey();
                    List<String> messages = entry.getValue();

                    errorMessages.append("- ").append(field).append(": ");
                    for (String message : messages) {
                        errorMessages.append(message).append(" ");
                    }
                    errorMessages.append("\n");
                }

                // Show the AlertDialog
                showErrorDialog(errorMessages.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("An unexpected error occurred.");
        }
    }


    private void showErrorDialog(String errorMessages) {
        new AlertDialog.Builder(this)
                .setTitle("Validation Errors")
                .setMessage(errorMessages)
                .setPositiveButton("OK", null)
                .show();
    }



}