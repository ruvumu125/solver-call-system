package com.solver.callbutton.api;

import com.solver.callbutton.Model.ApiResponse;
import com.solver.callbutton.Model.Company;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CompanyService {

    @GET("api/v1/companies/all")
    Call<ApiResponse> findAll();
}
