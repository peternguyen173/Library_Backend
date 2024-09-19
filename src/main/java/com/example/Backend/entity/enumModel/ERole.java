package com.example.Backend.entity.enumModel;

public enum ERole {
    ROLE_USER,
    ROLE_ADMIN;
    @Override
    public String toString(){
        return switch (this.ordinal()) {
            case 0 -> "ROLE_USER";
            case 1 -> "ROLE_ADMIN";
            default -> null;
        };
    }
}
