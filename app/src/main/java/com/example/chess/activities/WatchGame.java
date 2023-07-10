package com.example.chess.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.R;
import com.example.chess.menu_fragments.Play;
import com.example.chess.pieces.Bishop;
import com.example.chess.pieces.King;
import com.example.chess.pieces.Knight;
import com.example.chess.pieces.Move;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Piece;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;
import com.example.chess.userinformation.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;
import java.util.Vector;

public class WatchGame extends AppCompatActivity {
    public static final int FALSE = 0;
    public static final int TRUE = 1;
    static final int WHITE = 0, BLACK = 1;
    public static ImageButton[][] square;
    public static Piece[][] pawn;
    public static Piece[][] rook;
    public static Piece[][] knight;
    public static Piece[][] bishop;
    public static Piece[][] queen;
    public static Piece[][] king;
    public static Vector<Coordinates> nextXY;
    public static TreeSet<Coordinates> tempXY;
    public static TreeSet<Coordinates> finalCheck;
    public static HashMap<Integer, Type> type;
    public static Type curType;
    public static int curColor = WHITE;
    static boolean gameStarted = false;
    boolean moveShown = false;
    DatabaseReference ref, userRef0, userRef1, gameRef0;
    boolean dataReceived = true;
    String gameID, gameno;
    String id1, id2;
    User pp1, pp2;
    TextView p1NameTxt, p2NameTxt, p1RatingTxt, p2RatingTxt, p1TimeTxt, p2TimeTxt;
    ImageView photo1, photo2;
    String lastMove0 = "lastMove", lastMove1;
    long time1, time2;
    CountDownTimer timer1, timer2;
    boolean t1running = false, t2running = false;

    public static int getID(int x, int y) {
        return square[x][y].getId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._watch_game);
        initializeClasses();
        initializeButtons();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameID = bundle.getString("gameid");
        }
        System.out.println(gameID);
        placePiecesWhite();
        database();
    }

    void database() {
        ref = FirebaseDatabase.getInstance().getReference();
        gameRef0 = ref.child("Games").child(gameID);
        gameRef0.child("id1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id1 = (String) snapshot.getValue();
                System.out.println("---" + id1);
                ref.child("Users").child(id1).child("info").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("Hey: " + snapshot);
                        pp1 = snapshot.getValue(User.class);
                        p1NameTxt.setText(pp1.getName());
                        int rating = pp1.getRating();
                        String ratingTxt = "(" + rating + ")";
                        p1RatingTxt.setText(ratingTxt);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                userRef0 = ref.child("Users").child(id1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        gameRef0.child("id2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    id2 = (String) snapshot.getValue();
                    ref.child("Users").child(id2).child("info").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            pp2 = snapshot.getValue(User.class);
                            p2NameTxt.setText(pp2.getName());
                            int rating = pp2.getRating();
                            String ratingTxt = "(" + rating + ")";
                            p2RatingTxt.setText(ratingTxt);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    userRef1 = ref.child("Users").child(id2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        gameRef0.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("status").exists()) {
                    String status = (String) snapshot.child("status").getValue();
                    System.out.println("Status: " + status);
                }
                if (snapshot.child("lastMove").exists()) {
                    lastMove1 = (String) snapshot.child("lastMove").getValue();
                    if (snapshot.child("Moves").child(lastMove1).exists()) {
                        Move move = snapshot.child("Moves").child(lastMove1).getValue(Move.class);
                        if (lastMove0 != lastMove1 && move.getColor() != -1) {
                            curType = type.get(getID(move.getX1(), move.getY1()));
                            assert curType != null;
                            curType.piece.performMoves(move.getX2(), move.getY2());
                            dataReceived = true;
                            System.out.println("Data changed");
                            lastMove0 = lastMove1;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loss() {
        startTimer1();
        stopTimer1();
        startTimer2();
        stopTimer2();
        gameRef0.child("status").setValue("gameOver");
        double rating1 = pp1.getRating();
        double rating2 = pp2.getRating();
        double r1 = Math.pow(10, rating1 / 400);
        double r2 = Math.pow(10, rating2 / 400);
        double e1 = r1 / (r1 + r2);
        double e2 = r2 / (r1 + r2);
        int s1 = 0;
        int s2 = 1;
        double r1prime = rating1 + 32 * (s1 - e1);
        double r2prime = rating2 + 32 * (s2 - e2);
        if (com.example.chess.menu_fragments.Play.host) {
            pp1.setLoss(pp1.getLoss() + 1);
            pp1.setRating((int) r1prime);
            pp2.setWin(pp2.getWin() + 1);
            pp2.setRating((int) r2prime);
            gameRef0.child("status1").setValue("Loss");
            gameRef0.child("status2").setValue("Win");
        } else {
            pp2.setLoss(pp2.getLoss() + 1);
            pp1.setWin(pp1.getWin() + 1);
            gameRef0.child("status1").setValue("Win");
            gameRef0.child("status2").setValue("Loss");
        }
        userRef0.child("Info").setValue(pp1);
        userRef1.child("Info").setValue(pp2);
    }


    void initializeClasses() {
        square = new ImageButton[8][8];
        pawn = new Pawn[2][8];
        rook = new Rook[2][2];
        knight = new Knight[2][2];
        bishop = new Bishop[2][2];
        queen = new Queen[2][1];
        king = new King[2][1];
        type = new HashMap<>();
        nextXY = new Vector<>();
        tempXY = new TreeSet<>();
        finalCheck = new TreeSet<>();
    }

    void startTimer1() {
        timer1 = new CountDownTimer(time1, 1000) {
            @Override
            public void onTick(long l) {
                t1running = true;
                time1 = l;
                int min = (int) (time1 / 1000) / 60;
                int sec = (int) (time1 / 1000) % 60;
                String time = String.format("%02d:%02d", min, sec);
                System.out.println(time);
                p1TimeTxt.setText(time);
                if (min == 0 && sec == 0) loss();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    void stopTimer1() {
        timer1.cancel();
        t1running = false;
    }

    void startTimer2() {
        timer2 = new CountDownTimer(time2, 1000) {
            @Override
            public void onTick(long l) {
                time2 = l;
                int min = (int) (time2 / 1000) / 60;
                int sec = (int) (time2 / 1000) % 60;
                String time = String.format("%02d:%02d", min, sec);
                System.out.println(time);
                p2TimeTxt.setText(time);
                t2running = true;
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    void stopTimer2() {
        timer2.cancel();
        t2running = false;
    }

    void initializeButtons() {
        p1NameTxt = findViewById(R.id.p1UserName);
        p2NameTxt = findViewById(R.id.p2UserName);
        p1RatingTxt = findViewById(R.id.p1Rating);
        p2RatingTxt = findViewById(R.id.p2Rating);
        p1TimeTxt = findViewById(R.id.time1);
        p2TimeTxt = findViewById(R.id.time2);
        photo1 = findViewById(R.id.p1Photo);
        photo2 = findViewById(R.id.p2Photo);
        photo1.setImageResource(R.drawable.bishop0);
        photo2.setImageResource(R.drawable.bishop1);
        ImageButton k = findViewById(R.id.imageButton00);
        square[0][0] = findViewById(R.id.imageButton00);
        square[0][1] = findViewById(R.id.imageButton01);
        square[0][2] = findViewById(R.id.imageButton02);
        square[0][3] = findViewById(R.id.imageButton03);
        square[0][4] = findViewById(R.id.imageButton04);
        square[0][5] = findViewById(R.id.imageButton05);
        square[0][6] = findViewById(R.id.imageButton06);
        square[0][7] = findViewById(R.id.imageButton07);
        square[1][0] = findViewById(R.id.imageButton10);
        square[1][1] = findViewById(R.id.imageButton11);
        square[1][2] = findViewById(R.id.imageButton12);
        square[1][3] = findViewById(R.id.imageButton13);
        square[1][4] = findViewById(R.id.imageButton14);
        square[1][5] = findViewById(R.id.imageButton15);
        square[1][6] = findViewById(R.id.imageButton16);
        square[1][7] = findViewById(R.id.imageButton17);
        square[2][0] = findViewById(R.id.imageButton20);
        square[2][1] = findViewById(R.id.imageButton21);
        square[2][2] = findViewById(R.id.imageButton22);
        square[2][3] = findViewById(R.id.imageButton23);
        square[2][4] = findViewById(R.id.imageButton24);
        square[2][5] = findViewById(R.id.imageButton25);
        square[2][6] = findViewById(R.id.imageButton26);
        square[2][7] = findViewById(R.id.imageButton27);
        square[3][0] = findViewById(R.id.imageButton30);
        square[3][1] = findViewById(R.id.imageButton31);
        square[3][2] = findViewById(R.id.imageButton32);
        square[3][3] = findViewById(R.id.imageButton33);
        square[3][4] = findViewById(R.id.imageButton34);
        square[3][5] = findViewById(R.id.imageButton35);
        square[3][6] = findViewById(R.id.imageButton36);
        square[3][7] = findViewById(R.id.imageButton37);
        square[4][0] = findViewById(R.id.imageButton40);
        square[4][1] = findViewById(R.id.imageButton41);
        square[4][2] = findViewById(R.id.imageButton42);
        square[4][3] = findViewById(R.id.imageButton43);
        square[4][4] = findViewById(R.id.imageButton44);
        square[4][5] = findViewById(R.id.imageButton45);
        square[4][6] = findViewById(R.id.imageButton46);
        square[4][7] = findViewById(R.id.imageButton47);
        square[5][0] = findViewById(R.id.imageButton50);
        square[5][1] = findViewById(R.id.imageButton51);
        square[5][2] = findViewById(R.id.imageButton52);
        square[5][3] = findViewById(R.id.imageButton53);
        square[5][4] = findViewById(R.id.imageButton54);
        square[5][5] = findViewById(R.id.imageButton55);
        square[5][6] = findViewById(R.id.imageButton56);
        square[5][7] = findViewById(R.id.imageButton57);
        square[6][0] = findViewById(R.id.imageButton60);
        square[6][1] = findViewById(R.id.imageButton61);
        square[6][2] = findViewById(R.id.imageButton62);
        square[6][3] = findViewById(R.id.imageButton63);
        square[6][4] = findViewById(R.id.imageButton64);
        square[6][5] = findViewById(R.id.imageButton65);
        square[6][6] = findViewById(R.id.imageButton66);
        square[6][7] = findViewById(R.id.imageButton67);
        square[7][0] = findViewById(R.id.imageButton70);
        square[7][1] = findViewById(R.id.imageButton71);
        square[7][2] = findViewById(R.id.imageButton72);
        square[7][3] = findViewById(R.id.imageButton73);
        square[7][4] = findViewById(R.id.imageButton74);
        square[7][5] = findViewById(R.id.imageButton75);
        square[7][6] = findViewById(R.id.imageButton76);
        square[7][7] = findViewById(R.id.imageButton77);
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                type.put(square[i][j].getId(), new Type(new Piece(i, j, -1), -1, -1));
            }
        }
    }

    void placePiecesWhite() {
        for (int j = 0; j < 8; ++j) {
            square[1][j].setImageResource(R.drawable.pawn0);
            square[6][j].setImageResource(R.drawable.pawn1);
            pawn[WHITE][j] = new Pawn(1, j, WHITE);
            pawn[BLACK][j] = new Pawn(6, j, BLACK);
            type.put(square[1][j].getId(), new Type(pawn[WHITE][j], WHITE, j));
            type.put(square[6][j].getId(), new Type(pawn[BLACK][j], BLACK, j));
        }
        square[0][0].setImageResource(R.drawable.rook0);
        square[0][7].setImageResource(R.drawable.rook0);
        square[7][0].setImageResource(R.drawable.rook1);
        square[7][7].setImageResource(R.drawable.rook1);
        rook[WHITE][0] = new Rook(0, 0, WHITE);
        rook[WHITE][1] = new Rook(0, 7, WHITE);
        rook[BLACK][0] = new Rook(7, 0, BLACK);
        rook[BLACK][1] = new Rook(7, 7, BLACK);
        type.put(square[0][0].getId(), new Type(rook[WHITE][0], WHITE, 0));
        type.put(square[0][7].getId(), new Type(rook[WHITE][1], WHITE, 1));
        type.put(square[7][0].getId(), new Type(rook[BLACK][0], BLACK, 0));
        type.put(square[7][7].getId(), new Type(rook[BLACK][1], BLACK, 1));


        square[0][1].setImageResource(R.drawable.knight0);
        square[0][6].setImageResource(R.drawable.knight0);
        square[7][1].setImageResource(R.drawable.knight1);
        square[7][6].setImageResource(R.drawable.knight1);
        knight[WHITE][0] = new Knight(0, 1, WHITE);
        knight[WHITE][1] = new Knight(0, 6, WHITE);
        knight[BLACK][0] = new Knight(7, 1, BLACK);
        knight[BLACK][1] = new Knight(7, 6, BLACK);
        type.put(square[0][1].getId(), new Type(knight[WHITE][0], WHITE, 0));
        type.put(square[0][6].getId(), new Type(knight[WHITE][1], WHITE, 1));
        type.put(square[7][1].getId(), new Type(knight[BLACK][0], BLACK, 0));
        type.put(square[7][6].getId(), new Type(knight[BLACK][1], BLACK, 1));

        square[0][2].setImageResource(R.drawable.bishop0);
        square[0][5].setImageResource(R.drawable.bishop0);
        square[7][2].setImageResource(R.drawable.bishop1);
        square[7][5].setImageResource(R.drawable.bishop1);
        bishop[WHITE][0] = new Bishop(0, 2, WHITE);
        bishop[WHITE][1] = new Bishop(0, 5, WHITE);
        bishop[BLACK][0] = new Bishop(7, 2, BLACK);
        bishop[BLACK][1] = new Bishop(7, 5, BLACK);
        type.put(square[0][2].getId(), new Type(bishop[WHITE][0], WHITE, 0));
        type.put(square[0][5].getId(), new Type(bishop[WHITE][1], WHITE, 1));
        type.put(square[7][2].getId(), new Type(bishop[BLACK][0], BLACK, 0));
        type.put(square[7][5].getId(), new Type(bishop[BLACK][1], BLACK, 1));

        square[0][3].setImageResource(R.drawable.queen0);
        square[7][3].setImageResource(R.drawable.queen1);
        queen[WHITE][0] = new Queen(0, 3, WHITE);
        queen[BLACK][0] = new Queen(7, 3, BLACK);
        type.put(square[0][3].getId(), new Type(queen[WHITE][0], WHITE, 0));
        type.put(square[7][3].getId(), new Type(queen[BLACK][0], BLACK, 0));

        square[0][4].setImageResource(R.drawable.king0);
        square[7][4].setImageResource(R.drawable.king1);
        king[WHITE][0] = new King(0, 4, WHITE);
        king[BLACK][0] = new King(7, 4, BLACK);
        type.put(square[0][4].getId(), new Type(king[WHITE][0], WHITE, 0));
        type.put(square[7][4].getId(), new Type(king[BLACK][0], BLACK, 0));
    }

    void placePiecesBlack() {
        for (int j = 0; j < 8; ++j) {
            square[6][j].setImageResource(R.drawable.pawn0);
            square[1][j].setImageResource(R.drawable.pawn1);
            pawn[WHITE][j] = new Pawn(6, j, WHITE);
            pawn[BLACK][j] = new Pawn(1, j, BLACK);
            type.put(square[6][j].getId(), new Type(pawn[WHITE][j], WHITE, j));
            type.put(square[1][j].getId(), new Type(pawn[BLACK][j], BLACK, j));
        }
        square[7][0].setImageResource(R.drawable.rook0);
        square[7][7].setImageResource(R.drawable.rook0);
        square[0][0].setImageResource(R.drawable.rook1);
        square[0][7].setImageResource(R.drawable.rook1);
        rook[WHITE][0] = new Rook(7, 0, WHITE);
        rook[WHITE][1] = new Rook(7, 7, WHITE);
        rook[BLACK][0] = new Rook(0, 0, BLACK);
        rook[BLACK][1] = new Rook(0, 7, BLACK);
        type.put(square[7][0].getId(), new Type(rook[WHITE][0], WHITE, 0));
        type.put(square[7][7].getId(), new Type(rook[WHITE][1], WHITE, 1));
        type.put(square[0][0].getId(), new Type(rook[BLACK][0], BLACK, 0));
        type.put(square[0][7].getId(), new Type(rook[BLACK][1], BLACK, 1));


        square[7][1].setImageResource(R.drawable.knight0);
        square[7][6].setImageResource(R.drawable.knight0);
        square[0][1].setImageResource(R.drawable.knight1);
        square[0][6].setImageResource(R.drawable.knight1);
        knight[WHITE][0] = new Knight(7, 1, WHITE);
        knight[WHITE][1] = new Knight(7, 6, WHITE);
        knight[BLACK][0] = new Knight(0, 1, BLACK);
        knight[BLACK][1] = new Knight(0, 6, BLACK);
        type.put(square[7][1].getId(), new Type(knight[WHITE][0], WHITE, 0));
        type.put(square[7][6].getId(), new Type(knight[WHITE][1], WHITE, 1));
        type.put(square[0][1].getId(), new Type(knight[BLACK][0], BLACK, 0));
        type.put(square[0][6].getId(), new Type(knight[BLACK][1], BLACK, 1));

        square[7][2].setImageResource(R.drawable.bishop0);
        square[7][5].setImageResource(R.drawable.bishop0);
        square[0][2].setImageResource(R.drawable.bishop1);
        square[0][5].setImageResource(R.drawable.bishop1);
        bishop[WHITE][0] = new Bishop(7, 2, WHITE);
        bishop[WHITE][1] = new Bishop(7, 5, WHITE);
        bishop[BLACK][0] = new Bishop(0, 2, BLACK);
        bishop[BLACK][1] = new Bishop(0, 5, BLACK);
        type.put(square[7][2].getId(), new Type(bishop[WHITE][0], WHITE, 0));
        type.put(square[7][5].getId(), new Type(bishop[WHITE][1], WHITE, 1));
        type.put(square[0][2].getId(), new Type(bishop[BLACK][0], BLACK, 0));
        type.put(square[0][5].getId(), new Type(bishop[BLACK][1], BLACK, 1));

        square[7][3].setImageResource(R.drawable.queen0);
        square[0][3].setImageResource(R.drawable.queen1);
        queen[WHITE][0] = new Queen(7, 3, WHITE);
        queen[BLACK][0] = new Queen(0, 3, BLACK);
        type.put(square[7][3].getId(), new Type(queen[WHITE][0], WHITE, 0));
        type.put(square[0][3].getId(), new Type(queen[BLACK][0], BLACK, 0));

        square[7][4].setImageResource(R.drawable.king0);
        square[0][4].setImageResource(R.drawable.king1);
        king[WHITE][0] = new King(7, 4, WHITE);
        king[BLACK][0] = new King(0, 4, BLACK);
        type.put(square[7][4].getId(), new Type(king[WHITE][0], WHITE, 0));
        type.put(square[0][4].getId(), new Type(king[BLACK][0], BLACK, 0));
    }

    public static class Type {
        public Piece piece;
        public int color;
        int index;

        public Type(Piece piece, int color, int index) {
            this.piece = piece;
            this.color = color;
            this.index = index;
        }
    }

    public static class Coordinates implements Comparable<Coordinates> {
        int x, y;

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Coordinates o) {
            if (x == o.x && y == o.y) return 0;
            else if (x == o.x) {
                if (y < o.y) return 1;
                else return -1;
            } else {
                if (x < o.x) return 1;
                else return -1;
            }
        }
    }

}