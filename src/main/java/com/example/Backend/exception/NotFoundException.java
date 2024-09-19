package com.example.Backend.exception;

public class NotFoundException extends BaseException {
    @Override
    public int getStatusCode() {
        return 404;
    }

    public NotFoundException(String message)
    {
        super(message);
    }
}
