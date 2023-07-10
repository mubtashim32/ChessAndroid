package com.example.chess.menu_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chess.R;
import com.example.chess.WatchGameAdapter;
import com.example.chess.activities.Menu;
import com.example.chess.activities.PlayGame;
import com.example.chess.gameids.GameInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Watch extends Fragment {

    RecyclerView gameList;
    ArrayList<GameInfo> gameInfos;
    WatchGameAdapter adapter;

    DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout._watch_games, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameList = view.findViewById(R.id.gamelist);
        gameInfos = new ArrayList<>();
        adapter = new WatchGameAdapter(gameInfos);


        adapter.setOnItemClickListener(new WatchGameAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent i = new Intent(getActivity(), PlayGame.class);
                GameInfo gameInfo = gameInfos.get(position);
                i.putExtra("gameid", gameInfo.getId());
                i.putExtra("color", gameInfo.getColor1());
                if (gameInfo.getStatus().equals("running")) Menu.watchGameRunning = true; else Menu.watchGameFinished = true;
                startActivity(i);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Toast.makeText(getContext(), "Long Clicked", Toast.LENGTH_LONG).show();
            }
        });

        gameList.setAdapter(adapter);
        gameList.setLayoutManager(new LinearLayoutManager(getContext()));
        populate();
    }
    public void populate() {
        ref = FirebaseDatabase.getInstance().getReference().child("Games");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameInfos.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    GameInfo gameInfo = data.getValue(GameInfo.class);
                    if (gameInfo.getStatus() != null) {
                        if (gameInfo.getStatus().equals("running") == true || gameInfo.getStatus().equals("gameOver")) gameInfos.add(gameInfo);
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