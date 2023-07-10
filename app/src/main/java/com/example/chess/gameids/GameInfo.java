package com.example.chess.gameids;

import com.example.chess.pieces.Move;

import java.util.HashMap;

public class GameInfo {
    String status, status1, status2;
    String color1, color2;
    String id, id1, id2;
    HashMap<String, Move> moves;
    String lastMove;
    String del1, del2;

    public GameInfo() {
    }

//    public GameInfo(String status, String status1, String status2, String color1, String color2, String id, String id1, String id2, HashMap<String, Move> moves) {
//        this.status = status;
//        this.status1 = status1;
//        this.status2 = status2;
//        this.color1 = color1;
//        this.color2 = color2;
//        this.id = id;
//        this.id1 = id1;
//        this.id2 = id2;
//        this.moves = moves;
//    }


    public String getDel1() {
        return del1;
    }

    public void setDel1(String del1) {
        this.del1 = del1;
    }

    public String getDel2() {
        return del2;
    }

    public void setDel2(String del2) {
        this.del2 = del2;
    }

    public GameInfo(String status, String status1, String status2, String color1, String color2, String id, String id1, String id2, HashMap<String, Move> moves, String lastMove, String del1, String del2) {
        this.status = status;
        this.status1 = status1;
        this.status2 = status2;
        this.color1 = color1;
        this.color2 = color2;
        this.id = id;
        this.id1 = id1;
        this.id2 = id2;
        this.moves = moves;
        this.del1 = del1;
        this.del2 = del2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus1() {
        return status1;
    }

    public void setStatus1(String status1) {
        this.status1 = status1;
    }

    public String getStatus2() {
        return status2;
    }

    public void setStatus2(String status2) {
        this.status2 = status2;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public HashMap<String, Move> getMoves() {
        return moves;
    }

    public void setMoves(HashMap<String, Move> moves) {
        this.moves = moves;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLastMove() {
        return lastMove;
    }
    public void setLastMove(String lastMove) {
        this.lastMove = lastMove;
    }
}
