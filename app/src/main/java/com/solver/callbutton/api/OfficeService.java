package com.solver.callbutton.api;

import com.solver.callbutton.Model.ApiOfficeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OfficeService {

    @GET("api/v1/company/offices/{id}")
    Call<ApiOfficeResponse> getOfficeById(@Path("id") Long id);
}
