package com.example.chess.pieces;

import com.example.chess.R;
import com.example.chess.activities.PlayGame;

public class Knight extends Piece {
    public Knight(int x, int y, int color) {
        super(x, y, color);
        if (color == 0) {
            iconID = R.drawable.knight0;
        } else {
            iconID = R.drawable.knight1;
        }
    }

    public void showMoves(int check) {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (Math.abs(i) + Math.abs(j) == 3) {
                    int nextX = x + i, nextY = y + j;
                    if (moveCondition(nextX, nextY)) {
                        int nextId = PlayGame.getID(nextX, nextY);
                        if (check == PlayGame.TRUE || kingNotChecked(id, nextId)) doShowMoves(nextX, nextY, check);
                    }
                }
            }
        }
    }
}
