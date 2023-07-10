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
import com.example.chess.activities.Menu;
import com.example.chess.activities.WatchProfile;
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

public class RecentGamesAdapter extends RecyclerView.Adapter<RecentGamesAdapter.ViewHolder> {
    ArrayList<GameInfo> gameInfos;
    private static ClickListener clickListener;

    public RecentGamesAdapter(ArrayList<GameInfo> gameInfos) {
        this.gameInfos = gameInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View gameInfoView = inflater.inflate(R.layout.recent_games, parent, false);
        return new ViewHolder(gameInfoView);
    }

    public static void setOnItemClickListener(ClickListener clickListener) {
        RecentGamesAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
    String checkid = "hey";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameInfo gameInfo = gameInfos.get(position);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (Menu.watchProfile) id = WatchProfile.watchID;
        String id1 = gameInfo.getId1(), id2 = gameInfo.getId2();
        if (id2 != null) {
            if (id.equals(id1)) checkid = id2; else checkid = id1;
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(checkid).child("info");
            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    User user = task.getResult().getValue(User.class);
                    if (user.getName() != null) holder.playerName.setText("vs    " + user.getName() + "(" + user.getRating() + ")");
                    String del = "0";
                    if (checkid.equals(id2)) del = gameInfo.getDel1(); else del = gameInfo.getDel2();
                    if (del != null && del.charAt(0) != '-') del = "+" + del;
                    holder.winChance.setText("Delta: " + del);
                }
            });
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Games").child(gameInfo.getId()).child("color1");
            ref2.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    String color = task.getResult().getValue(String.class);
                    if (color.equals("1")) {
                        holder.color.setImageResource(R.drawable.pawn0);
                    } else holder.color.setImageResource(R.drawable.pawn1);
                }
            });
        }
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
