package org.ayfaar.app.spring.handler;

public class ErrorResponse {
    public String status = "error";
    public BusinessError error;

    public ErrorResponse(BusinessError error) {
        this.error = error;
    }
}
