package com.example.chess.pieces;

import com.example.chess.R;

public class Rook extends Piece {
    public Rook(int x, int y, int color) {
        super(x, y, color);
        if (color == 0) {
            iconID = R.drawable.rook0;
        } else {
            iconID = R.drawable.rook1;
        }
    }

    public void showMoves(int check) {
        straightMoves(check, 8);
    }
}
