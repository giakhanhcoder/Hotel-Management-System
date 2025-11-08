package com.example.projectprmt5.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
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
import com.example.projectprmt5.database.dao.RoomTypeDao;
import com.example.projectprmt5.database.dao.UserDao;
import com.example.projectprmt5.database.dao.ServiceDao;
import com.example.projectprmt5.database.entities.Booking;
import com.example.projectprmt5.database.entities.BookingServiceCrossRef;
import com.example.projectprmt5.database.entities.Inventory;
import com.example.projectprmt5.database.entities.InventoryUsage;
import com.example.projectprmt5.database.entities.Payment;
import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.database.entities.RoomType;
import com.example.projectprmt5.database.entities.Service;
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
        InventoryUsage.class,
        RoomType.class,
        Service.class,
        BookingServiceCrossRef.class
    },
    version = 6, // Incremented version for schema change
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
    public abstract RoomTypeDao roomTypeDao();
    public abstract ServiceDao serviceDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
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
                    // Populate data if tables are empty
                    populateUsers(INSTANCE.userDao());
                    populateRooms(INSTANCE.roomDao());
                    populateRoomTypes(INSTANCE.roomTypeDao());
                    populateServices(INSTANCE.serviceDao());
                }
            });
        }
    };
    
    private static void populateUsers(UserDao userDao) {
            userDao.insert(new User("admin@hotel.com", hashPassword("Admin123!"), "Admin", User.Role.MANAGER));
        userDao.insert(new User("receptionist@hotel.com", hashPassword("Receptionist123!"), "Receptionist", User.Role.RECEPTIONIST));
        userDao.insert(new User(" ", hashPassword("Guest123!"), "John Doe", User.Role.GUEST));
        userDao.insert(new User("giakhanh@gmail.com", hashPassword("giakhanh123"), "Gia Khanh", User.Role.GUEST));
        Log.d(TAG, "Users populated.");
    }

    private static void populateRooms(RoomDao roomDao) {
        roomDao.insert(new Room("101", "SINGLE", 50.0));
        roomDao.insert(new Room("102", "DOUBLE", 80.0));
        roomDao.insert(new Room("201", "SUITE", 150.0));
        roomDao.insert(new Room("202", "SINGLE", 55.0));
        roomDao.insert(new Room("301", "PENTHOUSE", 300.0));
        Log.d(TAG, "Rooms populated.");
    }

    private static void populateRoomTypes(RoomTypeDao roomTypeDao) {
            Log.d(TAG, "Populating room types...");
            roomTypeDao.insert(new RoomType("SINGLE"));
            roomTypeDao.insert(new RoomType("DOUBLE"));
            roomTypeDao.insert(new RoomType("SUITE"));
    }

    private static void populateServices(ServiceDao serviceDao) {
        if (serviceDao.getAnyService() == null) {
            Log.d(TAG, "Populating services...");
            serviceDao.insert(new Service("Breakfast", "Delicious breakfast buffet.", 15.0));
            serviceDao.insert(new Service("Lunch", "A la carte lunch menu.", 25.0));
            serviceDao.insert(new Service("Dinner", "Exquisite dinner experience.", 40.0));
            serviceDao.insert(new Service("Gym Access", "Full access to our modern gym.", 10.0));
            serviceDao.insert(new Service("Pool Access", "Relax by our stunning swimming pool.", 10.0));
        }
    }

    private static String hashPassword(String password) {
        return "HASH_" + password;
    }
}
