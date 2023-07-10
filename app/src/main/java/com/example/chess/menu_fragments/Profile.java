package com.example.chess.menu_fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chess.FriendAdapter;
import com.example.chess.R;
import com.example.chess.RecentGamesAdapter;
import com.example.chess.activities.Login;
import com.example.chess.activities.Menu;
import com.example.chess.activities.PlayGame;
import com.example.chess.activities.WatchProfile;
import com.example.chess.gameids.GameInfo;
import com.example.chess.userinformation.User;
import com.example.chess.userinformation.UserExtended;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

public class Profile extends Fragment {
    ImageView profilePhotoImg, countryPhotoImg;
    TextView logout;
    TextView usernameTxt, ratingTxt, countryNameTxt, joinDateTxt, winTxt, drawTxt, loseTxt, leagueTxt, totalTxt, rateTxt, streakTxt, maxTxt, minTxt;
    TextView friendsCnt;
    TextView gameCount;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String currentUserID;
    User currentUser;
    ImageButton addFriend;
    RecyclerView playedGames;
    ArrayList<GameInfo> gameInfos;
    RecentGamesAdapter adapter;

    ArrayList<User> friends;
    FriendAdapter friendAdapter;
    RecyclerView friendsList;
    TreeSet<String> friendIDs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout._profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logout = view.findViewById(R.id.logout);

        friendsCnt = view.findViewById(R.id.friends_count);
        profilePhotoImg = view.findViewById(R.id.profile_photo);
        countryPhotoImg = view.findViewById(R.id.country_photo);
        usernameTxt = view.findViewById(R.id.user_name);
        ratingTxt = view.findViewById(R.id.rating);
        countryNameTxt = view.findViewById(R.id.country_name);
        joinDateTxt = view.findViewById(R.id.join_date);

        addFriend = view.findViewById(R.id.addFriend);
        if (!Menu.watchProfile) addFriend.setVisibility(ImageButton.GONE);

        winTxt = view.findViewById(R.id.win);
        drawTxt = view.findViewById(R.id.draw);
        loseTxt = view.findViewById(R.id.lose);

        leagueTxt = view.findViewById(R.id.league);
        totalTxt = view.findViewById(R.id.games_count);
        rateTxt = view.findViewById(R.id.win_rate);
        streakTxt = view.findViewById(R.id.streak);
        maxTxt = view.findViewById(R.id.max_rating);
        minTxt = view.findViewById(R.id.min_rating);

        initializeEverything();

        playedGames = view.findViewById(R.id.rv_recent_games);
        gameCount = view.findViewById(R.id.gamesCount);

        gameInfos = new ArrayList<>();
        adapter = new RecentGamesAdapter(gameInfos);
        playedGames.setAdapter(adapter);
        playedGames.setLayoutManager(new LinearLayoutManager(getContext()));



        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends);
        friendAdapter.setOnItemClickListener(new FriendAdapter.ClickListener() {
            @Override
            public void onItemClick(String id) {
                Intent i = new Intent(getActivity(), WatchProfile.class);
                i.putExtra("id", id);
                Menu.watchProfile = true;
                startActivity(i);
            }
        });
        friendsList = view.findViewById(R.id.rv_friends);
        friendsList.setAdapter(friendAdapter);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        RecentGamesAdapter.setOnItemClickListener(new RecentGamesAdapter.ClickListener() {
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


        populate();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Login.class));
            }
        });
    }
    boolean done, done2;
    TreeSet<String> gameIDs;
    public void populate() {
        done = false;
        done2 = false;
        gameIDs = new TreeSet<>();
        friendIDs = new TreeSet<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
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
                                if (done2 == false) {
                                    gameInfos.clear();
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        GameInfo game = data.getValue(GameInfo.class);
                                        if (game.getId2() != null && gameIDs.contains(data.getKey())) {
                                            gameInfos.add(game);
                                        }
                                    }
                                    gameCount.setText(String.valueOf(gameInfos.size()));
                                    adapter.notifyDataSetChanged();
                                    done2 = true;
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        done = true;
                    }
                    // new
                    if (user.getFriends() != null) {
                        for (String id : user.getFriends()) {
                            friendIDs.add(id);
                            System.out.println(id);
                        }
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    if (friendIDs.contains(data.getKey())) {
                                        UserExtended userExtended = data.getValue(UserExtended.class);
                                        User user = userExtended.getInfo();
                                        friends.add(user);
                                    }
                                }
                                friendsCnt.setText(String.valueOf(friends.size()));
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void initializeEverything() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserID = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(String.valueOf(currentUserID)).child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                setViews();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    void setViews() {
        int win = currentUser.getWin(), draw = currentUser.getDraw(), lose = currentUser.getLoss();
        int total = win + draw + lose;
        int rate = 100;
        if (total != 0) rate = win * 100 / total;

        profilePhotoImg.setImageResource(R.drawable.warrior);
        countryPhotoImg.setImageResource(R.drawable.bishop1);
        usernameTxt.setText(currentUser.getName());
        ratingTxt.setText("("+currentUser.getRating()+")");
        countryNameTxt.setText("Bangladesh");
        joinDateTxt.setText("Joined " + currentUser.getJoinDate());

        winTxt.setText(String.valueOf(win));
        drawTxt.setText(String.valueOf(draw));
        loseTxt.setText(String.valueOf(lose));

        leagueTxt.setText(currentUser.getLeague());
        totalTxt.setText(String.valueOf(total));
        rateTxt.setText(String.valueOf(rate) + "%");
        streakTxt.setText(String.valueOf(currentUser.getCons_win()));
        maxTxt.setText(String.valueOf(currentUser.getMaxRating()));
        minTxt.setText(String.valueOf(currentUser.getMinRating()));
    }

}