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

import com.example.chess.R;
import com.example.chess.activities.Menu;
import com.example.chess.userinformation.UserExtended;
import com.example.chess.activities.WatchProfile;
import com.example.chess.userinformation.User;
import com.example.chess.userinformation.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Ranks extends Fragment {
    ArrayList<User> users;
    UserAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<String> ids;

    DatabaseReference ref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout._leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        users = new ArrayList<>();
        ids = new ArrayList<>();
        adapter = new UserAdapter(users);
        adapter.setOnItemClickListener(new UserAdapter.ClickListener() {
            @Override
            public void onItemClick(String id) {
                Intent i = new Intent(getActivity(), WatchProfile.class);
                i.putExtra("id", id);
                Menu.watchProfile = true;
                startActivity(i);
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        populate();
    }
    public void populate() {
        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ids.add(data.getKey());
                    UserExtended userE = data.getValue(UserExtended.class);
                    User user = userE.getInfo();
                    users.add(user);
                }
                Collections.sort(users, new Comparator<User>() {
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
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}