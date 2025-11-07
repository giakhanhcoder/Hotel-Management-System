package com.example.projectprmt5.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.AppDatabase;
import com.example.projectprmt5.database.dao.UserDao;
import com.example.projectprmt5.database.entities.User;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Repository class for User entity
 * Manages data operations and provides clean API for data access
 */
public class UserRepository {
    
    private final UserDao userDao;
    
    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
    }
    
    // Insert operations
    public Future<Long> insert(User user) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.insert(user));
    }
    
    public Future<List<Long>> insertAll(List<User> users) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.insertAll(users));
    }
    
    // Update operations
    public Future<Integer> update(User user) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.update(user));
    }
    
    public Future<Integer> updateLastLogin(int userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            userDao.updateLastLogin(userId, new Date().getTime())
        );
    }
    
    public Future<Integer> updateUserStatus(int userId, boolean isActive) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            userDao.updateUserStatus(userId, isActive)
        );
    }
    
    // Delete operations
    public Future<Integer> delete(User user) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.delete(user));
    }
    
    public Future<Integer> deleteById(int userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.deleteById(userId));
    }
    
    // Query operations
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }
    
    public Future<User> getUserByIdSync(int userId) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.getUserByIdSync(userId));
    }
    
    public Future<User> getUserByEmail(String email) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.getUserByEmail(email));
    }
    
    public Future<User> login(String email, String passwordHash) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            User user = userDao.login(email, passwordHash);
            if (user != null) {
                userDao.updateLastLogin(user.getUserId(), new Date().getTime());
            }
            return user;
        });
    }
    
    public LiveData<List<User>> getUsersByRole(String role) {
        return userDao.getUsersByRole(role);
    }
    
    public Future<List<User>> getUsersByRoleSync(String role) {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.getUsersByRoleSync(role));
    }
    
    public LiveData<List<User>> getAllActiveUsers() {
        return userDao.getAllActiveUsers();
    }
    
    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }
    
    public Future<List<User>> getAllUsersSync() {
        return AppDatabase.databaseWriteExecutor.submit(() -> userDao.getAllUsersSync());
    }
    
    public Future<Boolean> checkEmailExists(String email) {
        return AppDatabase.databaseWriteExecutor.submit(() -> 
            userDao.checkEmailExists(email) > 0
        );
    }
    
    public LiveData<List<User>> searchUsers(String searchQuery) {
        return userDao.searchUsers(searchQuery);
    }
    
    /**
     * Register a new user
     */
    public Future<Long> registerUser(String email, String password, String fullName, 
                                     String role, String phoneNumber) {
        return AppDatabase.databaseWriteExecutor.submit(() -> {
            // Check if email already exists
            if (userDao.checkEmailExists(email) > 0) {
                throw new Exception("Email already exists");
            }
            
            // Create new user
            User user = new User(email, hashPassword(password), fullName, role);
            user.setPhoneNumber(phoneNumber);
            
            return userDao.insert(user);
        });
    }
    
    /**
     * Simple password hashing (for demo purposes)
     * In production, use BCrypt or similar secure hashing
     */
    private String hashPassword(String password) {
        return "HASH_" + password;
    }
}


