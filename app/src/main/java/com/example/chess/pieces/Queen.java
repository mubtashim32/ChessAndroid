package com.example.chess.pieces;

import com.example.chess.R;

public class Queen extends Piece {
    public Queen(int x, int y, int color) {
        super(x, y, color);
        if (color == 0) {
            iconID = R.drawable.queen0;
        } else {
            iconID = R.drawable.queen1;
        }
    }

    public void showMoves(int check) {
        straightMoves(check, 8);
        diagonalMoves(check, 8);
    }

}
