package com.example.blueskywarehouse.Response;


public class IfSuccessResponse {
    private int status;
    private String message;

    public IfSuccessResponse() {

    }
    public IfSuccessResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
// getters and setters
}
