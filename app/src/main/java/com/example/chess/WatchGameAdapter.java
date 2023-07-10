package com.example.chess;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chess.R;
import com.example.chess.gameids.GameInfo;
import com.example.chess.userinformation.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class WatchGameAdapter extends RecyclerView.Adapter<WatchGameAdapter.ViewHolder> {
    ArrayList<GameInfo> gameInfos;
    private static ClickListener clickListener;

    public WatchGameAdapter(ArrayList<GameInfo> gameInfos) {
        this.gameInfos = gameInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View gameInfoView = inflater.inflate(R.layout.watch_game, parent, false);
        return new ViewHolder(gameInfoView);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        WatchGameAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
    String checkid = "hey";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameInfo gameInfo = gameInfos.get(position);
        if (gameInfo.getColor1().equals("0")) { holder.color1.setImageResource(R.drawable.pawn0); holder.color2.setImageResource(R.drawable.pawn1); }
        else if (gameInfo.getColor1().equals("1")) { holder.color1.setImageResource(R.drawable.pawn1); holder.color2.setImageResource(R.drawable.pawn0); }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(gameInfo.getId1()).child("info");
        if (gameInfo.getStatus().equals("running")) holder.status.setText("Running");
        else {
            if (gameInfo.getStatus1().equals("Win")) holder.color1.setBackgroundResource(R.drawable._leaderboard0);
            else holder.color2.setBackgroundResource(R.drawable._leaderboard0);
            holder.status.setText("Finished");
        }
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);
                holder.player1.setText(user.getName());
            }
        });
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(gameInfo.getId2()).child("info");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);
                holder.player2.setText(user.getName());
            }
        });
//        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Games").child(gameInfo.getId()).child("color1");
//        ref2.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                String color = task.getResult().getValue(String.class);
//                if (color.equals("0")) {
//                    holder.color.setImageResource(R.drawable.pawn0);
//                } else holder.color.setImageResource(R.drawable.pawn1);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return gameInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView color1, color2;
        TextView player1, player2, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            color1 = itemView.findViewById(R.id.color1);
            color2 = itemView.findViewById(R.id.color2);
            player1 = itemView.findViewById(R.id.player1);
            player2 = itemView.findViewById(R.id.player2);
            status = itemView.findViewById(R.id.status);
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
