package com.example.chess.pieces;

import com.example.chess.R;

public class Bishop extends Piece {
    public Bishop(int x, int y, int color) {
        super(x, y, color);
        if (color == 0) {
            iconID = R.drawable.bishop0;
        } else {
            iconID = R.drawable.bishop1;
        }
    }

    public void showMoves(int check) {
        diagonalMoves(check, 8);
    }
}
