package com.example.chess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess.R;
import com.example.chess.gameids.GameInfoAdapter;
import com.example.chess.userinformation.User;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    ArrayList<User> users;
    public static ClickListener clickListener;

    public FriendAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.friend, parent, false);
        return new ViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDetails(position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        FriendAdapter.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface ClickListener {
        void onItemClick(String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView rank, name, rating, rate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        public void setDetails(int position) {
            User user = users.get(position);
            name.setText(user.getName() + "\n(" + user.getRating() + ")");
        }

        @Override
        public void onClick(View view) {
            User user = users.get(getAdapterPosition());
            clickListener.onItemClick(user.getId());
        }
    }
}
