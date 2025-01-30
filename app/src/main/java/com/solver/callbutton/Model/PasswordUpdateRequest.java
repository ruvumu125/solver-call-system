package com.solver.callbutton.Model;

public class PasswordUpdateRequest {
    private String current_password;
    private String new_password;
    private String new_password_confirmation;

    public PasswordUpdateRequest(String current_password, String new_password, String new_password_confirmation) {
        this.current_password = current_password;
        this.new_password = new_password;
        this.new_password_confirmation = new_password_confirmation;
    }
}

