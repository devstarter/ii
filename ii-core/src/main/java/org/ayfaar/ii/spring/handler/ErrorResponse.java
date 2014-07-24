package org.ayfaar.ii.spring.handler;

public class ErrorResponse {
    public String status = "error";
    public BusinessError error;

    public ErrorResponse(BusinessError error) {
        this.error = error;
    }
}
