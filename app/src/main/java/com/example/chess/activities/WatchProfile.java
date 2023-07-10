package com.example.chess.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chess.FriendAdapter;
import com.example.chess.R;
import com.example.chess.RecentGamesAdapter;
import com.example.chess.gameids.GameInfo;
import com.example.chess.userinformation.User;
import com.example.chess.userinformation.UserExtended;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

public class WatchProfile extends AppCompatActivity {
    public static String watchID;
    ImageButton addFriend;
    TextView logout;
    ImageView profilePhotoImg, countryPhotoImg;
    TextView usernameTxt, ratingTxt, countryNameTxt, joinDateTxt, winTxt, drawTxt, loseTxt, leagueTxt, totalTxt, rateTxt, streakTxt, maxTxt, minTxt;
    DatabaseReference databaseReference;
    String playerID;
    User player;
    Boolean added = false;
    TextView gameCount;
    RecyclerView playedGames;
    ArrayList<GameInfo> gameInfos;
    RecentGamesAdapter adapter;


    ArrayList<User> friends;
    FriendAdapter friendAdapter;
    RecyclerView friendsList;
    TreeSet<String> friendIDs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._profile);
        initializeEverything();
    }
    void initializeEverything() {
        logout = findViewById(R.id.logout);
        if (Menu.watchProfile) logout.setVisibility(View.GONE);
        profilePhotoImg = findViewById(R.id.profile_photo);
        countryPhotoImg = findViewById(R.id.country_photo);
        usernameTxt = findViewById(R.id.user_name);
        ratingTxt = findViewById(R.id.rating);
        countryNameTxt = findViewById(R.id.country_name);
        joinDateTxt = findViewById(R.id.join_date);

        addFriend = findViewById(R.id.addFriend);

        winTxt = findViewById(R.id.win);
        drawTxt = findViewById(R.id.draw);
        loseTxt = findViewById(R.id.lose);

        leagueTxt = findViewById(R.id.league);
        totalTxt = findViewById(R.id.games_count);
        rateTxt = findViewById(R.id.win_rate);
        streakTxt = findViewById(R.id.streak);
        maxTxt = findViewById(R.id.max_rating);
        minTxt = findViewById(R.id.min_rating);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playerID = bundle.getString("id");
            watchID = playerID;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(String.valueOf(playerID)).child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                player = snapshot.getValue(User.class);
                setViews();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!added) {
                            UserExtended user = snapshot.getValue(UserExtended.class);
                            if (user.getFriends() == null) user.initializeFriends();
                            user.addFriend(playerID);
                            databaseReference.child(userID).setValue(user);
                            added = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        playedGames = findViewById(R.id.rv_recent_games);
        gameCount = findViewById(R.id.gamesCount);

        gameInfos = new ArrayList<>();
        adapter = new RecentGamesAdapter(gameInfos);
        playedGames.setAdapter(adapter);
        playedGames.setLayoutManager(new LinearLayoutManager(this));

        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends);
        friendAdapter.setOnItemClickListener(new FriendAdapter.ClickListener() {
            @Override
            public void onItemClick(String id) {
                Intent i = new Intent(WatchProfile.this, WatchProfile.class);
                i.putExtra("id", id);
                Menu.watchProfile = true;
                startActivity(i);
            }
        });
        friendsList = findViewById(R.id.rv_friends);
        friendsList.setAdapter(friendAdapter);
        friendsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        populate();


    }
    boolean done;
    TreeSet<String> gameIDs;
    public void populate() {
        done = false;
        gameIDs = new TreeSet<>();
        friendIDs = new TreeSet<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(playerID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (done == false) {
                    UserExtended user = snapshot.getValue(UserExtended.class);
                    if (user.getGames() != null) {
                        for (String id : user.getGames()) {
                            gameIDs.add(id);
                        }
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Games");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    GameInfo game = data.getValue(GameInfo.class);
                                    if (game.getId2() != null && gameIDs.contains(data.getKey())) {
                                        gameInfos.add(game);
                                    }
                                }
                                gameCount.setText(String.valueOf(gameInfos.size()));
                                adapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (user.getFriends() != null) {
                            for (String id : user.getFriends()) {
                                friendIDs.add(id);
                                System.out.println(id);
                            }
                            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("Users");
                            ref3.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        if (friendIDs.contains(data.getKey())) {
                                            UserExtended userExtended = data.getValue(UserExtended.class);
                                            User user = userExtended.getInfo();
                                            friends.add(user);
                                        }
                                    }
                                    Collections.sort(friends, new Comparator<User>() {
                                        @Override
                                        public int compare(User user1, User user2) {
                                            if (user1.getRating() == user2.getRating()) {
                                                int total1 = user1.getWin() + user1.getDraw() + user1.getLoss();
                                                int total2 = user2.getWin() + user2.getDraw() + user2.getLoss();
                                                int win1 = user1.getWin(), win2 = user2.getWin();
                                                int rate1 = 100, rate2 = 100;
                                                if (total1 != 0) rate1 = win1 * 100 / total1;
                                                if (total2 != 0) rate2 = win2 * 100 / total2;
                                                return rate2 - rate1;
                                            } else {
                                                return user2.getRating() - user1.getRating();
                                            }
                                        }
                                    });
                                    friendAdapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        done = true;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void setViews() {
        int win = player.getWin(), draw = player.getDraw(), lose = player.getLoss();
        int total = win + draw + lose;
        int rate = 100;
        if (total != 0) rate = win * 100 / total;

        profilePhotoImg.setImageResource(R.drawable.bishop0);
        countryPhotoImg.setImageResource(R.drawable.bishop1);
        usernameTxt.setText(player.getName());
        ratingTxt.setText("("+ player.getRating()+")");
        countryNameTxt.setText("Bangladesh");
        joinDateTxt.setText("Joined " + player.getJoinDate());

        winTxt.setText(String.valueOf(win));
        drawTxt.setText(String.valueOf(draw));
        loseTxt.setText(String.valueOf(lose));

        leagueTxt.setText(player.getLeague());
        totalTxt.setText(String.valueOf(total));
        rateTxt.setText(String.valueOf(rate) + "%");
        streakTxt.setText(String.valueOf(player.getCons_win()));
        maxTxt.setText(String.valueOf(player.getMaxRating()));
        minTxt.setText(String.valueOf(player.getMinRating()));
    }
}