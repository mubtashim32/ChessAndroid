package com.example.chess.pieces;

import com.example.chess.R;

public class King extends Piece {
    public King(int x, int y, int color) {
        super(x, y, color);
        if (color == 0) {
            iconID = R.drawable.king0;
        } else {
            iconID = R.drawable.king1;
        }
    }

    public void showMoves(int check) {
        diagonalMoves(check, 2);
        straightMoves(check, 2);
    }
}
