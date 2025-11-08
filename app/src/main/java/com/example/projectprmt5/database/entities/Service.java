package com.example.projectprmt5.database.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "services")
public class Service implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int serviceId;

    private String name;
    private String description;
    private double price;

    public Service(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // Parcelable implementation
    protected Service(Parcel in) {
        serviceId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(serviceId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
    }

    // Equals and HashCode for Set operations
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return serviceId == service.serviceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }
}
