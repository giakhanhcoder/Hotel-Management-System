package com.example.projectprmt5.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.Room;

public class RoomListAdapter extends ListAdapter<Room, RoomListAdapter.RoomViewHolder> {

    private OnItemClickListener listener;

    public RoomListAdapter(@NonNull DiffUtil.ItemCallback<Room> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_list_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room current = getItem(position);
        holder.bind(current);
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewRoomNumber;
        private final TextView textViewRoomType;
        private final TextView textViewRoomPrice;

        private RoomViewHolder(View itemView) {
            super(itemView);
            textViewRoomNumber = itemView.findViewById(R.id.textViewRoomNumber);
            textViewRoomType = itemView.findViewById(R.id.textViewRoomType);
            textViewRoomPrice = itemView.findViewById(R.id.textViewRoomPrice);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }

        public void bind(Room room) {
            textViewRoomNumber.setText("Phòng " + room.getRoomNumber());
            textViewRoomType.setText("Loại: " + room.getType());
            textViewRoomPrice.setText(String.format("%,.0f VND", room.getPrice()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Room room);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class RoomDiff extends DiffUtil.ItemCallback<Room> {
        @Override
        public boolean areItemsTheSame(@NonNull Room oldItem, @NonNull Room newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Room oldItem, @NonNull Room newItem) {
            return oldItem.equals(newItem);
        }
    }
}
