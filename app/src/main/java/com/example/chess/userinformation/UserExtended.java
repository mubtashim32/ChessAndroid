package com.example.chess.userinformation;

import java.util.ArrayList;

public class UserExtended {
    User info;
    ArrayList<String> friends;
    ArrayList<String> games;

    public UserExtended() {
    }

    public UserExtended(User info, ArrayList<String> friends, ArrayList<String> games) {
        this.info = info;
        this.friends = friends;
        this.games = games;
    }

    public User getInfo() {
        return info;
    }

    public void setInfo(User info) {
        this.info = info;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void addFriend(String friendID) {
        friends.add(friendID);
    }

    public void initializeFriends() {
        friends = new ArrayList<>();
    }

    public void addGame(String gameID) {
        games.add(gameID);
    }

    public void initializeGames() {
        games = new ArrayList<>();
    }

    public ArrayList<String> getGames() {
        return games;
    }

    public void setGames(ArrayList<String> games) {
        this.games = games;
    }
}
