package com.solver.callbutton.api;

import com.solver.callbutton.Model.HelpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HelpRequestService {

    @POST("api/v1/help-request") // Replace with your endpoint
    Call<Void> postHelpRequest(@Body HelpRequest request);
}
