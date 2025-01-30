package com.solver.callbutton.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.solver.callbutton.constant.Constant.BASE_URL;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}