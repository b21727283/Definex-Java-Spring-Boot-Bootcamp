package com.mgumussoy.advancedtaskmanagement.exceptions;

public class UserEntityNotFoundException extends RuntimeException {
    public UserEntityNotFoundException() {
        super("UserNotFoundException");
    }
}
