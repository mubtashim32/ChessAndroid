package com.example.chess.userinformation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess.R;
import com.example.chess.gameids.GameInfoAdapter;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        ArrayList<User> users;
        public static ClickListener clickListener;

        public UserAdapter(ArrayList<User> users) {
                this.users = users;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Context context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                View userView = inflater.inflate(R.layout._leaderboard_player, parent, false);
                return new ViewHolder(userView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.setDetails(position);
        }

        public void setOnItemClickListener(ClickListener clickListener) {
                System.out.println("Hey1");
                UserAdapter.clickListener = clickListener;
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
                        rank = itemView.findViewById(R.id.rank);
                        name = itemView.findViewById(R.id.name);
                        rating = itemView.findViewById(R.id.rating);
                        rate = itemView.findViewById(R.id.win_rate);
                        itemView.setOnClickListener(this);
                }

                public void setDetails(int position) {
                        User user = users.get(position);
                        rank.setText("#" + (position + 1));
                        name.setText(user.getName());
                        rating.setText(String.valueOf(user.getRating()));
                        int total1 = user.getWin() + user.getDraw() + user.getLoss();
                        int win1 = user.getWin();
                        int rate1 = 100;
                        if (total1 != 0) rate1 = win1 * 100 / total1;
                        rate.setText(String.valueOf(rate1) + "%");
                }

                @Override
                public void onClick(View view) {
                        User user = users.get(getAdapterPosition());
                        clickListener.onItemClick(user.getId());
                }
        }
}
