package com.solver.callbutton.api;

import com.solver.callbutton.Model.LoginRequest;
import com.solver.callbutton.Model.LoginResponse;
import com.solver.callbutton.Model.PasswordChangeApiResponse;
import com.solver.callbutton.Model.PasswordResetApiResponse;
import com.solver.callbutton.Model.PasswordResetRequest;
import com.solver.callbutton.Model.PasswordUpdateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    @POST("api/v1/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/v1/update-password")
    Call<PasswordChangeApiResponse> updatePassword(@Body PasswordUpdateRequest request);

    @POST("api/v1/forget-password")
    Call<PasswordResetApiResponse> resetPassword(@Body PasswordResetRequest request);
}
