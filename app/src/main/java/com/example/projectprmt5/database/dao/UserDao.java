package com.example.projectprmt5.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectprmt5.database.entities.User;

import java.util.List;

/**
 * Data Access Object for User entity
 */
@Dao
public interface UserDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<User> users);
    
    // Update operations
    @Update
    int update(User user);
    
    // Delete operations
    @Delete
    int delete(User user);
    
    @Query("DELETE FROM users WHERE userId = :userId")
    int deleteById(int userId);
    
    // Query operations
    @Query("SELECT * FROM users WHERE userId = :userId")
    LiveData<User> getUserById(int userId);
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserByIdSync(int userId);
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
    
    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    User login(String email, String passwordHash);
    
    @Query("SELECT * FROM users WHERE role = :role AND isActive = 1")
    LiveData<List<User>> getUsersByRole(String role);
    
    @Query("SELECT * FROM users WHERE role = :role AND isActive = 1")
    List<User> getUsersByRoleSync(String role);
    
    @Query("SELECT * FROM users WHERE isActive = 1")
    LiveData<List<User>> getAllActiveUsers();
    
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();
    
    @Query("SELECT * FROM users")
    List<User> getAllUsersSync();
    
    @Query("UPDATE users SET lastLoginAt = :loginTime WHERE userId = :userId")
    int updateLastLogin(int userId, long loginTime);
    
    @Query("UPDATE users SET isActive = :isActive WHERE userId = :userId")
    int updateUserStatus(int userId, boolean isActive);
    
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);
    
    @Query("SELECT * FROM users WHERE fullName LIKE '%' || :searchQuery || '%' OR email LIKE '%' || :searchQuery || '%'")
    LiveData<List<User>> searchUsers(String searchQuery);
}


