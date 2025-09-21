package com.example.icebreaker.dto;

public class LoginResponse {
    private String message;
    private String token;
    private UserDto user;

    public LoginResponse() {}

    public LoginResponse(String message, UserDto user) {
        this.message = message;
        this.user = user;
        this.token = null;
    }


    public LoginResponse(String message, String token, UserDto user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}
