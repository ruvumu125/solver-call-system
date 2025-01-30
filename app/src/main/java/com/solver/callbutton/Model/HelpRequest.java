package com.solver.callbutton.Model;

public class HelpRequest {
    private Long user_id;

    public HelpRequest(Long user_id) {
        this.user_id = user_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}
