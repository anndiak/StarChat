package com.starchat.model;

public enum UserRoles {
    ADMIN(0),
    USER(1);

    private final int value;

    UserRoles(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRoles getByValue(int value) {
        for (UserRoles role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
}

