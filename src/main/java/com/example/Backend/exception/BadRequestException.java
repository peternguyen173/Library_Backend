package com.example.Backend.exception;

public class BadRequestException extends BaseException{
    @Override
    public int getStatusCode() {
        return 400;
    }

    public BadRequestException(String message)
    {
        super(message);
    }
}

