package com.solver.callbutton.api;

import com.solver.callbutton.Model.Occupier;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OccupierService {

    @POST("api/v1/addOccupier")
    Call<Void> register(@Body Occupier occupier);
}
