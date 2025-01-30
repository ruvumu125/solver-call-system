package com.solver.callbutton.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @PUT("/api/v1/update-fcm-token/{id}")
    Call<Void> updateFcmToken(@Path("id") Long id, @Body Map<String, String> body);


}
