package com.example.projectprmt5.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectprmt5.R;
import com.example.projectprmt5.database.entities.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
        void onUserLongClick(User user);
    }

    public UserAdapter(List<User> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvEmail;
        private TextView tvPhone;
        private TextView tvRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvRole = itemView.findViewById(R.id.tvRole);
        }

        public void bind(User user, OnUserClickListener listener) {
            tvName.setText(user.getFullName() != null ? user.getFullName() : "N/A");
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");

            // Display role in Vietnamese
            String roleText = "";
            if ("MANAGER".equals(user.getRole())) {
                roleText = "Quản lý";
            } else if ("RECEPTIONIST".equals(user.getRole())) {
                roleText = "Lễ tân";
            } else if ("GUEST".equals(user.getRole())) {
                roleText = "Khách hàng";
            }
            tvRole.setText(roleText);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onUserLongClick(user);
                }
                return true;
            });
        }
    }
}
