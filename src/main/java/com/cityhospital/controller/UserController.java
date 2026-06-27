package com.cityhospital.controller;

import com.cityhospital.model.User;
import com.cityhospital.service.FileManager;
import com.cityhospital.util.AlertUtil;
import com.cityhospital.util.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ROLE-BASED ACCESS CONTROL: Only ADMIN can manage user accounts.
 */
public class UserController {

    public boolean addReceptionist(String username, String password) {
        if (username == null || username.trim().isEmpty()) { AlertUtil.showError("Error", "Username required."); return false; }
        if (password == null || password.trim().isEmpty()) { AlertUtil.showError("Error", "Password required."); return false; }
        if (password.length() < 6) { AlertUtil.showError("Error", "Password must be 6+ characters."); return false; }

        List<User> users = FileManager.loadUsers();
        if (users.stream().anyMatch(u -> u.getUsername().equals(username.trim()))) {
            AlertUtil.showError("Error", "Username already exists."); return false;
        }

        String userId = IDGenerator.generateUserId();
        users.add(new User(userId, username.trim(), password.trim(), User.ROLE_RECEPTIONIST));
        FileManager.saveUsers(users);
        AlertUtil.showInfo("Success", "Receptionist added.\nID: " + userId);
        return true;
    }

    public boolean updateUser(User user, String username, String password) {
        if (user == null) { AlertUtil.showError("Error", "No user selected."); return false; }
        List<User> users = FileManager.loadUsers();
        for (User u : users) {
            if (u.getUserId().equals(user.getUserId())) {
                u.setUsername(username.trim()); u.setPassword(password.trim());
                FileManager.saveUsers(users);
                AlertUtil.showInfo("Success", "User updated.");
                return true;
            }
        }
        AlertUtil.showError("Error", "User not found.");
        return false;
    }

    public boolean deleteUser(String userId) {
        List<User> users = FileManager.loadUsers();
        if (users.removeIf(u -> u.getUserId().equals(userId))) {
            FileManager.saveUsers(users);
            AlertUtil.showInfo("Success", "User deleted.");
            return true;
        }
        AlertUtil.showError("Error", "User not found.");
        return false;
    }

    public List<User> getAllUsers() { return FileManager.loadUsers(); }

    public List<User> getReceptionists() {
        return FileManager.loadUsers().stream()
                .filter(u -> User.ROLE_RECEPTIONIST.equals(u.getRole()))
                .collect(Collectors.toList());
    }
}
