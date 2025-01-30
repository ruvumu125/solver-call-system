package com.solver.callbutton.api;

import com.solver.callbutton.Model.ApiResponse;
import com.solver.callbutton.Model.VideoApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VideoService {

    @GET("api/v1/video/active")
    Call<VideoApiResponse> findActiveVideo();
}
