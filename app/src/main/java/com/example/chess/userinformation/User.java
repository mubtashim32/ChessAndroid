package com.example.chess.userinformation;

import java.util.ArrayList;

public class User {
    String id;
    String name, email;
    int win, draw, loss;
    String countryName, joinDate;
    int rating;
    int cons_win;
    int maxRating, minRating;
    String league;

    public User() {

    }

    public int getCons_win() {
        return cons_win;
    }

    public void setCons_win(int cons_win) {
        this.cons_win = cons_win;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(int maxRating) {
        this.maxRating = maxRating;
    }

    public int getMinRating() {
        return minRating;
    }

    public void setMinRating(int minRating) {
        this.minRating = minRating;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User(String id, String name, String email, int win, int draw, int loss) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.win = win;
        this.draw = draw;
        this.loss = loss;
        rating = 800;
        cons_win = 0;
        maxRating = 800;
        minRating = 800;
        league = "Silver";
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getWin() {
        return win;
    }

    public int getDraw() {
        return draw;
    }

    public int getLoss() {
        return loss;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setWin(int win) {
        this.win = win;
        cons_win = this.win;
    }

    public void setDraw(int draw) {
        cons_win = 0;
        this.draw = draw;
    }

    public void setLoss(int loss) {
        cons_win = 0;
        this.loss = loss;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        maxRating = Math.max(maxRating, rating);
        minRating = Math.min(minRating, rating);
        if (rating >= 5000) league = "Legend";
        else if (rating >= 4100) league = "Titan";
        else if (rating >= 3200) league = "Champion";
        else if (rating >= 2600) league = "Master";
        else if (rating >= 2000) league = "Crystal";
        else if (rating >= 1400) league = "Gold";
        else if (rating >= 800) league = "Silver";
        else if (rating >= 400) league = "Bronze";
        else league = "TopG";
    }
}
