package com.example.chess.pieces;

import androidx.annotation.NonNull;

import com.example.chess.activities.Menu;
import com.example.chess.activities.PlayGame;
import com.example.chess.activities.WatchGame;

import java.util.Objects;

public class Piece extends PlayGame {
    public int x;
    public int y;
    public int id;
    public int color;
    int alive;
    int moved;
    int iconID;

    public Piece(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.id = getID(x, y);
        this.color = color;
        alive = TRUE;
        moved = FALSE;
    }

    boolean doShowMoves(int nextX, int nextY, int check) {
        int nextId = getID(nextX, nextY);
        if (checkFree(nextX, nextY)) {
            if ((check == TRUE || kingNotChecked(this.id, nextId))) fillXY(nextX, nextY, check);
        } else if (checkOpponent(nextX, nextY)) {
            if ((check == TRUE || kingNotChecked(this.id, nextId))) fillXY(nextX, nextY, check);
            return false;
        } else return false;
        return true;
    }

    boolean moveCondition(int nextX, int nextY) {
        return checkBounds(nextX, nextY);
    }

    void fillXY(int nextX, int nextY, int check) {
        if (check == TRUE) tempXY.add(new Coordinates(nextX, nextY));
        else nextXY.add(new Coordinates(nextX, nextY));
    }

    boolean checkFree(int nextX, int nextY) {
        int nextId = getID(nextX, nextY);
        return Objects.requireNonNull(type.get(nextId)).color == -1;
    }

    boolean checkOpponent(int nextX, int nextY) {
        int nextId = getID(nextX, nextY);
        return Objects.requireNonNull(type.get(nextId)).color == 1 - this.color;
    }

    public void showMoves(int check) {
    }

    boolean checkBounds(int x, int y) {
        return (x >= 0 && x < 8 && y >= 0 && y < 8);
    }

    @NonNull
    @Override
    public String toString() {
        return x + " " + y + " " + id + " " + color;
    }

    public void performMoves(int newX, int newY) {
        System.out.println(type.get(id).piece);
        Piece p1 = Objects.requireNonNull(type.get(id)).piece, p2 = Objects.requireNonNull(type.get(getID(newX, newY))).piece;
        square[x][y].setImageResource(0);
        square[newX][newY].setImageResource(iconID);
        type.put(id, new Type(new Piece(x, y, -1), -1, -1));
        int tx = x, ty = y, tid = id;
        curType.piece.x = newX;
        curType.piece.y = newY;
        curType.piece.id = getID(newX, newY);
        curType.piece.moved = TRUE;
        p2.x = tx;
        p2.y = ty;
        p2.id = tid;
        p2.moved = FALSE;
        type.put(getID(newX, newY), curType);
    }

    void straightMoves(int check, int N) {
        int nextX, nextY;
        boolean left = true, right = true, top = true, bottom = true;
        for (int i = 1; i < N; ++i) {
            nextX = x - i;
            nextY = y;
            if (moveCondition(nextX, nextY)) {
                if (top) top = doShowMoves(nextX, nextY, check);
            }
            nextX = x + i;
            nextY = y;
            if (moveCondition(nextX, nextY)) {
                if (bottom) bottom = doShowMoves(nextX, nextY, check);
            }
            nextX = x;
            nextY = y - i;
            if (moveCondition(nextX, nextY)) {
                if (left) left = doShowMoves(nextX, nextY, check);
            }
            nextX = x;
            nextY = y + i;
            if (moveCondition(nextX, nextY)) {
                if (right) right = doShowMoves(nextX, nextY, check);
            }
        }

    }

    void diagonalMoves(int check, int N) {
        int nextX, nextY;
        boolean topLeft = true, topRight = true, bottomLeft = true, bottomRight = true;
        for (int i = 1; i < N; ++i) {
            nextX = x + i;
            nextY = y + i;
            if (moveCondition(nextX, nextY)) {
                if (topRight) topRight = doShowMoves(nextX, nextY, check);
            }
            nextX = x + i;
            nextY = y - i;
            if (moveCondition(nextX, nextY)) {
                if (topLeft) topLeft = doShowMoves(nextX, nextY, check);
            }
            nextX = x - i;
            nextY = y - i;
            if (moveCondition(nextX, nextY)) {
                if (bottomLeft) bottomLeft = doShowMoves(nextX, nextY, check);
            }
            nextX = x - i;
            nextY = y + i;
            if (moveCondition(nextX, nextY)) {
                if (bottomRight) bottomRight = doShowMoves(nextX, nextY, check);
            }
        }
    }
}
