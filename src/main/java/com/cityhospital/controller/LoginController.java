package com.cityhospital.controller;

import com.cityhospital.model.User;
import com.cityhospital.service.AuthenticationService;
import com.cityhospital.util.AlertUtil;

/**
 * LoginController handles the authentication logic.
 * It coordinates between the LoginView and the AuthenticationService.
 * 
 * This class demonstrates separation of concerns - the controller
 * manages the flow, while the service handles the actual authentication.
 */
public class LoginController {

    private User loggedInUser;

    /**
     * Attempts to authenticate a user with the given credentials and role.
     * 
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @param role     The expected role (ADMIN, DOCTOR, RECEPTIONIST, PATIENT)
     * @return true if authentication succeeds, false otherwise
     */
    public boolean login(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            AlertUtil.showError("Login Error", "Please enter your username.");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            AlertUtil.showError("Login Error", "Please enter your password.");
            return false;
        }
        if (role == null || role.trim().isEmpty()) {
            AlertUtil.showError("Login Error", "Please select your role.");
            return false;
        }

        User user = AuthenticationService.authenticate(username.trim(), password.trim());
        if (user != null) {
            if (!user.getRole().equals(role)) {
                AlertUtil.showError("Login Failed", "Incorrect role selected for this account.");
                return false;
            }
            loggedInUser = user;
            return true;
        } else {
            AlertUtil.showError("Login Failed", "Invalid username or password.");
            return false;
        }
    }

    /** Overload for backward compatibility (no role check). */
    public boolean login(String username, String password) {
        return login(username, password, null);
    }

    /**
     * Gets the currently logged-in user.
     * 
     * @return The authenticated User object
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Gets the role of the currently logged-in user.
     * 
     * @return The role string (ADMIN, DOCTOR, RECEPTIONIST, PATIENT)
     */
    public String getLoggedInRole() {
        return loggedInUser != null ? loggedInUser.getRole() : null;
    }
}
