package com.example.projectprmt5.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
// import androidx.room.Room; // THIS LINE IS THE CAUSE OF THE ERROR AND IS NOW REMOVED.
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.projectprmt5.database.converters.DateConverter;
import com.example.projectprmt5.database.converters.ListConverter;
import com.example.projectprmt5.database.dao.BookingDao;
import com.example.projectprmt5.database.dao.InventoryDao;
import com.example.projectprmt5.database.dao.InventoryUsageDao;
import com.example.projectprmt5.database.dao.PaymentDao;
import com.example.projectprmt5.database.dao.RoomDao;
import com.example.projectprmt5.database.dao.UserDao;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.Inventory;
import com.example.projectprmt5.database.entities.InventoryUsage;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {
        User.class,
        Room.class,
        Booking.class,
        Payment.class,
        Inventory.class,
        InventoryUsage.class
    },
    version = 2,
    exportSchema = false
)
@TypeConverters({DateConverter.class, ListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String TAG = "AppDatabase";
    private static final String DATABASE_NAME = "hotel_management_db";
    private static volatile AppDatabase INSTANCE;
    
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = 
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public abstract UserDao userDao();
    public abstract RoomDao roomDao();
    public abstract BookingDao bookingDao();
    public abstract PaymentDao paymentDao();
    public abstract InventoryDao inventoryDao();
    public abstract InventoryUsageDao inventoryUsageDao();
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Use the fully qualified name here to avoid conflicts
                    INSTANCE = androidx.room.Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .addCallback(roomDatabaseCallback)
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                if (INSTANCE != null) {
                    UserDao dao = INSTANCE.userDao();
                    if (dao.getAnyUser() == null) {
                        populateInitialData(INSTANCE);
                    }
                }
            });
        }
    };
    
    private static void populateInitialData(AppDatabase database) {
        Log.d(TAG, "Populating initial data...");
        UserDao userDao = database.userDao();
        RoomDao roomDao = database.roomDao();
        
        // Create default users
        userDao.insert(new User("admin@hotel.com", hashPassword("Admin123!"), "Admin", User.Role.MANAGER));
        userDao.insert(new User("receptionist@hotel.com", hashPassword("Receptionist123!"), "Receptionist", User.Role.RECEPTIONIST));
        userDao.insert(new User("guest@example.com", hashPassword("Guest123!"), "John Doe", User.Role.GUEST));
        
        // Create sample rooms
        for (int i = 101; i <= 105; i++) roomDao.insert(new Room(String.valueOf(i), Room.RoomType.SINGLE, 500000, 1));
        for (int i = 201; i <= 205; i++) roomDao.insert(new Room(String.valueOf(i), Room.RoomType.DOUBLE, 800000, 2));
        Log.d(TAG, "Initial data populated.");
    }
    
    private static String hashPassword(String password) {
        return "HASH_" + password;
    }
}
