package com.relieftrack.config;

import com.relieftrack.enums.Role;
import com.relieftrack.model.User;

public final class SessionManager {

    private static User currentUser;

    private SessionManager() {
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentDisplayName() {
        if (currentUser == null) {
            return "Guest";
        }
        if (currentUser.getFullName() != null && !currentUser.getFullName().isBlank()) {
            return currentUser.getFullName();
        }
        return currentUser.getUsername();
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }
}
