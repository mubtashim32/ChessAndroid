package com.example.chess.pieces;

import com.example.chess.R;
import com.example.chess.activities.PlayGame;

public class Pawn extends Piece {
    int sign;

    public Pawn(int x, int y, int color) {
        super(x, y, color);
        if (color == 0) {
            sign = 1;
            iconID = R.drawable.pawn0;
        } else {
            sign = 1;
            iconID = R.drawable.pawn1;
        }

    }

    public void showMoves(int check) {
        int nextX, nextY;
        nextX = x + sign;
        nextY = y;
        if (moveCondition(nextX, nextY) && checkFree(nextX, nextY)) {
            doShowMoves(nextX, nextY, check);
            nextX = x + 2 * sign;
            nextY = y;
            if (moved == PlayGame.FALSE && moveCondition(nextX, nextY) && checkFree(nextX, nextY)) {
                doShowMoves(nextX, nextY, check);
            }
        }
        nextX = x + sign;
        nextY = y - 1;
        if (moveCondition(nextX, nextY) && checkOpponent(nextX, nextY)) {
            doShowMoves(nextX, nextY, check);
        }
        nextX = x + sign;
        nextY = y + 1;
        if (moveCondition(nextX, nextY) && checkOpponent(nextX, nextY)) {
            doShowMoves(nextX, nextY, check);
        }
    }
}
