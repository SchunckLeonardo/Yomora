package com.yomora.identity.application;

public record RegisterCommand(String name, String username, String email, String password) {
}
