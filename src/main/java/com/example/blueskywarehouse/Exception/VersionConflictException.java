package com.example.blueskywarehouse.Exception;

public class VersionConflictException extends RuntimeException {
    public VersionConflictException(String message) {
        super(message);
    }
}
