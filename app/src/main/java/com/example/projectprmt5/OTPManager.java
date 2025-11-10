package com.example.projectprmt5;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Random;

/**
 * Quản lý OTP (One-Time Password) cho reset password
 * OTP Manager for password reset
 */
public class OTPManager {

    private static final String TAG = "OTPManager";
    private static final String PREF_NAME = "OTPPrefs";
    private static final String KEY_OTP = "otp_";
    private static final String KEY_OTP_TIMESTAMP = "otp_timestamp_";
    private static final String KEY_OTP_EMAIL = "otp_email_";
    
    // OTP expires in 5 minutes (300 seconds)
    private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds
    
    // OTP length
    private static final int OTP_LENGTH = 6;

    private SharedPreferences sharedPreferences;

    public OTPManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Generate OTP (6-digit random number)
     */
    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit number (100000-999999)
        return String.valueOf(otp);
    }

    /**
     * Save OTP for email
     */
    public void saveOTP(String email, String otp) {
        String emailKey = email.toLowerCase().replace(".", "_");
        String otpKey = KEY_OTP + emailKey;
        String timestampKey = KEY_OTP_TIMESTAMP + emailKey;
        String emailKeyPref = KEY_OTP_EMAIL + emailKey;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(otpKey, otp);
        editor.putLong(timestampKey, System.currentTimeMillis());
        editor.putString(emailKeyPref, email);
        editor.apply();

        Log.d(TAG, "OTP saved for email: " + email + ", OTP: " + otp);
    }

    /**
     * Verify OTP
     */
    public boolean verifyOTP(String email, String inputOTP) {
        String emailKey = email.toLowerCase().replace(".", "_");
        String otpKey = KEY_OTP + emailKey;
        String timestampKey = KEY_OTP_TIMESTAMP + emailKey;

        String savedOTP = sharedPreferences.getString(otpKey, null);
        long timestamp = sharedPreferences.getLong(timestampKey, 0);

        if (savedOTP == null || timestamp == 0) {
            Log.d(TAG, "No OTP found for email: " + email);
            return false;
        }

        // Check if OTP expired
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - timestamp;

        if (elapsedTime > OTP_EXPIRY_TIME) {
            Log.d(TAG, "OTP expired for email: " + email + ", elapsed: " + elapsedTime + "ms");
            // Clear expired OTP
            clearOTP(email);
            return false;
        }

        // Verify OTP
        boolean isValid = savedOTP.equals(inputOTP.trim());
        
        Log.d(TAG, "OTP verification for email: " + email + 
                ", Input: " + inputOTP + 
                ", Saved: " + savedOTP + 
                ", Valid: " + isValid);

        if (isValid) {
            // Clear OTP after successful verification
            clearOTP(email);
        }

        return isValid;
    }

    /**
     * Clear OTP for email
     */
    public void clearOTP(String email) {
        String emailKey = email.toLowerCase().replace(".", "_");
        String otpKey = KEY_OTP + emailKey;
        String timestampKey = KEY_OTP_TIMESTAMP + emailKey;
        String emailKeyPref = KEY_OTP_EMAIL + emailKey;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(otpKey);
        editor.remove(timestampKey);
        editor.remove(emailKeyPref);
        editor.apply();

        Log.d(TAG, "OTP cleared for email: " + email);
    }

    /**
     * Get saved email for OTP (if exists)
     */
    public String getSavedEmail(String email) {
        String emailKey = email.toLowerCase().replace(".", "_");
        String emailKeyPref = KEY_OTP_EMAIL + emailKey;
        return sharedPreferences.getString(emailKeyPref, null);
    }

    /**
     * Check if OTP exists and is valid (not expired)
     */
    public boolean hasValidOTP(String email) {
        String emailKey = email.toLowerCase().replace(".", "_");
        String otpKey = KEY_OTP + emailKey;
        String timestampKey = KEY_OTP_TIMESTAMP + emailKey;

        String savedOTP = sharedPreferences.getString(otpKey, null);
        long timestamp = sharedPreferences.getLong(timestampKey, 0);

        if (savedOTP == null || timestamp == 0) {
            return false;
        }

        // Check if expired
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - timestamp;

        return elapsedTime <= OTP_EXPIRY_TIME;
    }

    /**
     * Get remaining time in seconds
     */
    public long getRemainingTime(String email) {
        String emailKey = email.toLowerCase().replace(".", "_");
        String timestampKey = KEY_OTP_TIMESTAMP + emailKey;

        long timestamp = sharedPreferences.getLong(timestampKey, 0);
        if (timestamp == 0) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - timestamp;
        long remainingTime = OTP_EXPIRY_TIME - elapsedTime;

        return remainingTime > 0 ? remainingTime / 1000 : 0; // Return in seconds
    }
}











