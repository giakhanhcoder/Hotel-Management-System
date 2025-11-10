package com.example.projectprmt5.examples;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.projectprmt5.database.entities.Room;
import com.example.projectprmt5.repository.BookingRepository;
import com.example.projectprmt5.repository.InventoryRepository;
import com.example.projectprmt5.repository.PaymentRepository;
import com.example.projectprmt5.repository.RoomRepository;
import com.example.projectprmt5.repository.UserRepository;

import java.util.List;

/**
 * Example class demonstrating how to use the Room Database
 * This is for reference only - not meant to be run as-is.
 * NOTE: Some examples have been commented out after code refactoring.
 */
public class DatabaseUsageExample extends AppCompatActivity {
    
    // Repositories
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;
    private PaymentRepository paymentRepository;
    private InventoryRepository inventoryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize repositories
        initializeRepositories();
        
        // Example usages
        exampleRoomOperations();
        // The methods below have been commented out as they contained
        // legacy code and caused build errors.
        // exampleUserOperations();
        // exampleBookingFlow();
        // exampleInventoryManagement();
        // exampleReports();
    }
    
    private void initializeRepositories() {
        userRepository = new UserRepository(getApplication());
        roomRepository = new RoomRepository(getApplication());
        bookingRepository = new BookingRepository(getApplication());
        paymentRepository = new PaymentRepository(getApplication());
        inventoryRepository = new InventoryRepository(getApplication());
    }
    
    // ==================== ROOM OPERATIONS (REWRITTEN) ====================
    
    private void exampleRoomOperations() {
        // 1. Get all rooms
        LiveData<List<Room>> allRoomsLiveData = roomRepository.getAllRooms();
        allRoomsLiveData.observe(this, rooms -> {
            System.out.println("Total rooms in database: " + rooms.size());
        });
        
        // 2. Add a new room
        Room newRoom = new Room("701", "SUITE", 2500000);
        roomRepository.insert(newRoom);
        
        // 3. Update a room (Example)
        // In a real app, you would get a room from LiveData first.
        // Let's assume the room with ID 1 exists.
        LiveData<Room> roomToUpdateLiveData = roomRepository.getRoomById(1);
        roomToUpdateLiveData.observe(this, room -> {
            if (room != null) {
                // We only want to update once, so remove the observer
                roomToUpdateLiveData.removeObservers(this);

                // Change the price and update
                room.setPrice(999999);
                roomRepository.update(room);
            }
        });

        // 4. Update room status
        // Let's assume the room with ID 2 exists and we want to mark it as occupied.
//        roomRepository.updateRoomStatus(2, Room.RoomStatus.OCCUPIED);

        // 5. Delete a room
        // In a real app, you get the room object to delete. For this example,
        // we'll create a placeholder object with a specific ID to illustrate the call.
        Room roomToDelete = new Room("TBD", "TBD", 0);
        roomToDelete.setId(5); // Assuming room with ID 5 exists.
        roomRepository.delete(roomToDelete);
    }
    
    // =========================================================================
    // THE METHODS BELOW HAVE BEEN COMMENTED OUT DUE TO MAJOR REFACTORING
    // THEY CONTAINED LEGACY CODE AND CAUSED BUILD ERRORS.
    // =========================================================================

    /*
    private void exampleUserOperations() {
        // ... code removed ...
    }
    */

    /*
    private void exampleBookingFlow() {
        // ... code removed ...
    }
    */

    /*
    private void exampleCheckInCheckOut() {
        // ... code removed ...
    }
    */

    /*
    private void exampleInventoryManagement() {
        // ... code removed ...
    }
    */

    /*
    private void exampleReports() {
        // ... code removed ...
    }
    */
}








