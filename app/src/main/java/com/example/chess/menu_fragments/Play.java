package com.example.chess.menu_fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chess.activities.CurrentGames;
import com.example.chess.activities.Menu;
import com.example.chess.gameids.GameInfo;
import com.example.chess.R;
import com.example.chess.activities.Color;
import com.example.chess.activities.Login;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Play extends Fragment {
    ImageButton bstart, bjoin;
    Slider slider;
    TextView timeTxt;
    DatabaseReference ref, gameRef0;
    long count = -1;
    FirebaseAuth mAuth;
    FirebaseUser user;
    public static boolean host = false;
    public static int time = 5 * 60 * 1000;
    Boolean updated = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Menu.watchGameRunning = false;
        Menu.watchGameFinished = false;
        bstart = view.findViewById(R.id.start);
        bjoin = view.findViewById(R.id.join);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        timeTxt = view.findViewById(R.id.time);
        timeTxt.setText("5 min");
        if (user == null) {
            startActivity(new Intent(getActivity(), Login.class));
        }
        ref = FirebaseDatabase.getInstance().getReference();
        gameRef0 = ref.child("Games");
        bstart.setEnabled(false);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                bstart.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gameID = gameRef0.push().getKey();
                System.out.println("-----" + gameID);
                GameInfo gameInfo = new GameInfo();
                gameInfo.setId(gameID);
                gameRef0.child(gameID).setValue(gameInfo);
                host = true;
                Intent i = new Intent(getActivity(), Color.class);
                i.putExtra("gameid", gameID);
                startActivity(i);
            }
        });
        bjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), CurrentGames.class));
            }
        });

        slider = view.findViewById(R.id.slider);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                time = (int)value * 60 * 1000;
                timeTxt.setText((int)value + " min");
            }
        });
    }
}