package com.example.chess.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.gameids.GameInfo;
import com.example.chess.R;
import com.example.chess.userinformation.UserExtended;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Color extends AppCompatActivity implements View.OnClickListener {

    ImageButton white, black;
    String gameID, gameno;
    int color;
    int hostColor;

    DatabaseReference ref, gameRef0, gameRef1;
    FirebaseAuth mAuth;
    FirebaseUser user;

    GameInfo gameInfo;

    boolean read = false, updated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        white = findViewById(R.id.white);
        black = findViewById(R.id.black);

        white.setOnClickListener(this);
        black.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameID = bundle.getString("gameid");
        }
        ref = FirebaseDatabase.getInstance().getReference();
        gameRef0 = ref.child("Games").child(gameID);
    }

    @Override
    public void onClick(View view) {
            if (view.getId() == white.getId()) color = 0; else color = 1;
            Intent i = new Intent(Color.this, PlayGame.class);
            i.putExtra("gameid", gameID);
            i.putExtra("color", color);
                gameRef0.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (read == false && task.isSuccessful()) {
                            gameInfo = task.getResult().getValue(GameInfo.class);
                            gameInfo.setId1(user.getUid());
                            gameInfo.setStatus1("onGame");
                            gameInfo.setColor1(String.valueOf(color));
                            gameInfo.setStatus("started");
                            read = true;
                            gameRef0.setValue(gameInfo);
                        }
                    }
                });
                DatabaseReference temp = ref.child("Users").child(user.getUid());
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