package com.example.chess.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chess.gameids.GameInfo;
import com.example.chess.R;
import com.example.chess.gameids.GameInfoAdapter;
import com.example.chess.userinformation.UserExtended;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class CurrentGames extends AppCompatActivity {

    RecyclerView gameList;
    ArrayList<GameInfo> gameInfos;
    GameInfoAdapter adapter;

    DatabaseReference ref;
    String id;
    boolean updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._current_games);

        gameList = findViewById(R.id.gamelist);
        gameInfos = new ArrayList<>();
        adapter = new GameInfoAdapter(gameInfos);


        adapter.setOnItemClickListener(new GameInfoAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent i = new Intent(CurrentGames.this, PlayGame.class);
                GameInfo gameInfo = gameInfos.get(position);
                String gameID = gameInfo.getId();
                i.putExtra("gameid", gameID);
                String color1 = gameInfo.getColor1();
                int color2;
                if (color1.equals("0")) color2 = 1; else color2 = 0;
                i.putExtra("color", color2);
                System.out.println(color2);
                id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                gameInfo.setId2(id);
                gameInfo.setStatus2("onGame");
                gameInfo.setColor2(String.valueOf(color2));
                gameInfo.setStatus("running");
                ref = FirebaseDatabase.getInstance().getReference().child("Games").child(gameID);
                ref.setValue(gameInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference temp = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                            temp.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!updated) {
                                        UserExtended user = snapshot.getValue(UserExtended.class);
                                        if (user.getGames() == null) user.initializeGames();
                                        user.addGame(gameID);
                                        temp.setValue(user);
                                        updated = true;
                                        startActivity(i);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });


            }

            @Override
            public void onItemLongClick(int position, View v) {
                Toast.makeText(CurrentGames.this, "Long Clicked", Toast.LENGTH_LONG).show();
            }
        });

        gameList.setAdapter(adapter);
        gameList.setLayoutManager(new LinearLayoutManager(this));
        populate();
    }

    public void populate() {
        System.out.println("Populating");
        ref = FirebaseDatabase.getInstance().getReference().child("Games");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameInfos.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    GameInfo gameInfo = data.getValue(GameInfo.class);
                    System.out.println(gameInfo.getId());
                    if (gameInfo.getStatus()!= null && gameInfo.getStatus().equals("started") == true) {
                        gameInfos.add(gameInfo);
                    }
                }
                Collections.reverse(gameInfos);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}