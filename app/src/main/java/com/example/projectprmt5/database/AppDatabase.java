package com.example.projectprmt5.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.projectprmt5.database.dao.BookingDao;
import com.example.projectprmt5.database.dao.FeedbackDao;
import com.example.projectprmt5.database.dao.InventoryDao;
import com.example.projectprmt5.database.dao.InventoryUsageDao;
import com.example.projectprmt5.database.dao.PaymentDao;
import com.example.projectprmt5.database.dao.RoomDao;
import com.example.projectprmt5.database.dao.UserDao;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Feedback;
import com.example.projectprmt5.database.entities.Inventory;
import com.example.projectprmt5.database.entities.InventoryUsage;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Room Database class for the Hotel Management System
 * Defines the database configuration and serves as the app's main access point to the persisted data
 */
@Database(
    entities = {
        User.class,
        Room.class,
        Booking.class,
        Payment.class,
        Inventory.class,
        InventoryUsage.class,
        Feedback.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String TAG = "AppDatabase";
    
    // Database name
    private static final String DATABASE_NAME = "hotel_management_db";
    
    // Singleton instance
    private static volatile AppDatabase INSTANCE;
    
    // Executor service for database operations
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = 
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    // Abstract methods to get DAOs
    public abstract UserDao userDao();
    public abstract RoomDao roomDao();
    public abstract BookingDao bookingDao();
    public abstract PaymentDao paymentDao();
    public abstract InventoryDao inventoryDao();
    public abstract InventoryUsageDao inventoryUsageDao();
    public abstract FeedbackDao feedbackDao();
    
    /**
     * Get singleton instance of the database
     * Uses double-checked locking pattern for thread safety
     */
    public static AppDatabase getInstance(Context context) {
        Log.d(TAG, "getInstance() called");
        if (INSTANCE == null) {
            Log.d(TAG, "INSTANCE is null, creating new database...");
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    Log.d(TAG, "Building database: " + DATABASE_NAME);
                    INSTANCE = androidx.room.Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    // Uncomment for debugging (allows main thread queries, not recommended for production)
                    // .allowMainThreadQueries()
                    
                    // Add callback for database creation
                    .addCallback(roomDatabaseCallback)
                    
                    // Migration strategy (use fallback for development)
                    .fallbackToDestructiveMigration()
                    
                    .build();
                    Log.d(TAG, "Database instance created successfully");
                }
            }
        } else {
            Log.d(TAG, "INSTANCE already exists, returning existing instance");
        }
        return INSTANCE;
    }
    
    /**
     * Callback for database initialization
     * Populates initial data when database is created
     */
    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(TAG, "========== DATABASE onCreate() CALLED ==========");
            Log.d(TAG, "Database is being created for the first time!");
            
            // Populate initial data in background thread
            databaseWriteExecutor.execute(() -> {
                Log.d(TAG, "Starting to populate initial data...");
                if (INSTANCE != null) {
                    populateInitialData(INSTANCE);
                    Log.d(TAG, "Initial data population completed!");
                } else {
                    Log.e(TAG, "ERROR: INSTANCE is null in onCreate callback!");
                }
            });
        }
        
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.d(TAG, "========== DATABASE onOpen() CALLED ==========");
            Log.d(TAG, "Database opened (already exists)");
        }
    };
    
    /**
     * Populate the database with initial data
     * This includes default admin user and sample rooms
     */
    private static void populateInitialData(AppDatabase database) {
        Log.d(TAG, "populateInitialData() started");
        UserDao userDao = database.userDao();
        RoomDao roomDao = database.roomDao();
        InventoryDao inventoryDao = database.inventoryDao();
        
        Log.d(TAG, "Creating default users...");
        
        // Create default admin/manager account
        User adminUser = new User(
            "admin@hotel.com",
            hashPassword("Admin123!"), // In production, use proper password hashing
            "System Administrator",
            User.Role.MANAGER
        );
        adminUser.setPhoneNumber("+84901234567");
        adminUser.setAddress("Hotel Main Office");
        long adminId = userDao.insert(adminUser);
        Log.d(TAG, "✅ Inserted admin user (ID: " + adminId + "): " + adminUser.getEmail());
        
        // Create sample receptionist account
        User receptionistUser = new User(
            "receptionist@hotel.com",
            hashPassword("Receptionist123!"),
            "Front Desk Staff",
            User.Role.RECEPTIONIST
        );
        receptionistUser.setPhoneNumber("+84909876543");
        receptionistUser.setAddress("Hotel Front Desk");
        long recepId = userDao.insert(receptionistUser);
        Log.d(TAG, "✅ Inserted receptionist user (ID: " + recepId + "): " + receptionistUser.getEmail());
        
        // Create sample guest account
        User guestUser = new User(
            "guest@example.com",
            hashPassword("Guest123!"),
            "John Doe",
            User.Role.GUEST
        );
        guestUser.setPhoneNumber("+84905555555");
        guestUser.setAddress("123 Guest Street, City");
        long guestId = userDao.insert(guestUser);
        Log.d(TAG, "✅ Inserted guest user (ID: " + guestId + "): " + guestUser.getEmail());
        
        // Create sample rooms
        Log.d(TAG, "Creating sample rooms...");
        createSampleRooms(roomDao);
        
        // Create sample inventory
        Log.d(TAG, "Creating sample inventory...");
        createSampleInventory(inventoryDao);
        
        Log.d(TAG, "========== POPULATE INITIAL DATA COMPLETED ==========");
    }
    
    /**
     * Create sample rooms for testing
     */
    private static void createSampleRooms(RoomDao roomDao) {
        // Single rooms
        for (int i = 101; i <= 105; i++) {
            Room room = new Room(
                String.valueOf(i),
                Room.RoomType.SINGLE,
                500000, // 500k VND per night
                1
            );
            room.setFloorNumber(1);
            room.setDescription("Cozy single room with essential amenities");
            roomDao.insert(room);
        }
        
        // Double rooms
        for (int i = 201; i <= 205; i++) {
            Room room = new Room(
                String.valueOf(i),
                Room.RoomType.DOUBLE,
                800000, // 800k VND per night
                2
            );
            room.setFloorNumber(2);
            room.setDescription("Comfortable double room perfect for couples");
            roomDao.insert(room);
        }
        
        // Suite rooms
        for (int i = 301; i <= 303; i++) {
            Room room = new Room(
                String.valueOf(i),
                Room.RoomType.SUITE,
                1500000, // 1.5M VND per night
                4
            );
            room.setFloorNumber(3);
            room.setDescription("Luxurious suite with separate living area");
            roomDao.insert(room);
        }
        
        // Deluxe rooms
        for (int i = 401; i <= 402; i++) {
            Room room = new Room(
                String.valueOf(i),
                Room.RoomType.DELUXE,
                2500000, // 2.5M VND per night
                4
            );
            room.setFloorNumber(4);
            room.setDescription("Premium deluxe room with panoramic views");
            roomDao.insert(room);
        }
    }
    
    /**
     * Create sample inventory items
     */
    private static void createSampleInventory(InventoryDao inventoryDao) {
        // Amenities
        Inventory towels = new Inventory("Towels", "AMN001", Inventory.Category.AMENITY, 100, 20);
        towels.setUnit("piece");
        towels.setUnitPrice(50000);
        towels.setDescription("Hotel bath towels");
        inventoryDao.insert(towels);
        
        Inventory shampoo = new Inventory("Shampoo Bottles", "AMN002", Inventory.Category.AMENITY, 50, 15);
        shampoo.setUnit("bottle");
        shampoo.setUnitPrice(25000);
        shampoo.setDescription("Guest shampoo bottles");
        inventoryDao.insert(shampoo);
        
        // Cleaning supplies
        Inventory detergent = new Inventory("Detergent", "CLN001", Inventory.Category.CLEANING, 30, 10);
        detergent.setUnit("bottle");
        detergent.setUnitPrice(80000);
        detergent.setDescription("Cleaning detergent");
        inventoryDao.insert(detergent);
        
        // Linen
        Inventory bedSheets = new Inventory("Bed Sheets", "LIN001", Inventory.Category.LINEN, 80, 20);
        bedSheets.setUnit("piece");
        bedSheets.setUnitPrice(150000);
        bedSheets.setDescription("Hotel bed sheets");
        inventoryDao.insert(bedSheets);
        
        // Beverages
        Inventory water = new Inventory("Mineral Water", "BEV001", Inventory.Category.BEVERAGE, 200, 50);
        water.setUnit("bottle");
        water.setUnitPrice(10000);
        water.setDescription("Complimentary mineral water");
        inventoryDao.insert(water);
    }
    
    /**
     * Simple password hashing (for demo purposes only)
     * In production, use BCrypt, Argon2, or similar secure hashing algorithms
     */
    private static String hashPassword(String password) {
        // This is a simplified hash for demonstration
        // In production, use proper password hashing like BCrypt
        return "HASH_" + password;
    }
    
    /**
     * Clear all data from the database
     * Use with caution!
     */
    public static void clearDatabase(Context context) {
        databaseWriteExecutor.execute(() -> {
            AppDatabase db = getInstance(context);
            db.clearAllTables();
        });
    }
    
    /**
     * Close the database connection
     */
    public static void closeDatabase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}

