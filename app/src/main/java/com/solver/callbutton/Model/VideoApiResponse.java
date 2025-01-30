package com.solver.callbutton.Model;

import java.util.List;

public class VideoApiResponse {

    private boolean success;
    private Video data;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Video getData() {
        return data;
    }

    public void setData(Video data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
