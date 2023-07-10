package com.example.chess.gameids;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess.R;
import com.example.chess.userinformation.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GameInfoAdapter extends RecyclerView.Adapter<GameInfoAdapter.ViewHolder> {
    ArrayList<GameInfo> gameInfos;
    private static ClickListener clickListener;

    public GameInfoAdapter(ArrayList<GameInfo> gameInfos) {
        this.gameInfos = gameInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View gameInfoView = inflater.inflate(R.layout._game, parent, false);
        return new ViewHolder(gameInfoView);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        GameInfoAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameInfo gameInfo = gameInfos.get(position);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(gameInfo.getId1()).child("info");
            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    User user = task.getResult().getValue(User.class);
                    if (user.getName() != null) holder.playerName.setText(user.getName() + "(" + user.getRating() + ")");
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("info");
                    ref2.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            User user2 = task.getResult().getValue(User.class);
                            double rating1 = user.getRating(), rating2 = user2.getRating();
                            double r21 = rating2 - rating1;
                            double sigma = 100 / (1 + Math.pow(10, -r21 / 400));
                            holder.winChance.setText(String.format("%.2f", sigma)+"%");
                        }
                    });
                }
            });
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Games").child(gameInfo.getId()).child("color1");
            ref2.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    String color = task.getResult().getValue(String.class);
                    if (color.equals("0")) {
                        holder.color.setImageResource(R.drawable.pawn1);
                    } else holder.color.setImageResource(R.drawable.pawn0);
                }
            });
    }

    @Override
    public int getItemCount() {
        return gameInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView color;
        TextView playerName, winChance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            color = itemView.findViewById(R.id.color);
            playerName = itemView.findViewById(R.id.player);
            winChance = itemView.findViewById(R.id.win_chance);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemLongClick(getAdapterPosition(), view);
            return false;
        }
    }
}
