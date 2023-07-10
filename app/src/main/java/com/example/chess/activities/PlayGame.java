package com.example.chess.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.R;
import com.example.chess.gameids.GameInfo;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeSet;
import java.util.Vector;

public class
PlayGame extends AppCompatActivity implements View.OnClickListener {
    BottomNavigationView bottomNavigationView;
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
    boolean moveShown = false;
    DatabaseReference ref, userRef0, userRef1, gameRef0;
    boolean dataReceived = true;
    String gameID;
    String id1, id2;
    User pp1, pp2;
    TextView p1NameTxt, p2NameTxt, p1RatingTxt, p2RatingTxt, p1TimeTxt, p2TimeTxt;
    ImageView photo1, photo2;
    String lastMove0 = "lastMove", lastMove1;
    long time1, time2;
    CountDownTimer timer1, timer2;
    boolean t1running = false, t2running = false;
    Dialog dialog, dialog2;
    TextView result;
    TextView how;
    TextView rt;
    ImageButton close;
    boolean start = false;
    int ir1 = -1, ir2 = -1;

    public static int getID(int x, int y) {
        return square[x][y].getId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._play_game);
        bottomNavigationView = findViewById(R.id.check);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.forward:
                        movePlus();
                        return true;
                    case R.id.backward:
                        moveMinus();
                        return true;
                }
                return false;
            }
        });
        bottomNavigationView.setVisibility(View.GONE);
        initializeClasses();
        initializeButtons();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            curColor = bundle.getInt("color");
            gameID = bundle.getString("gameid");
        }
        if (curColor == WHITE) placePiecesWhite(); else placePiecesBlack();
        initialQuery();
        if (Menu.watchGameRunning) {
            watchGame();
        } else if (Menu.watchGameFinished) {
            reviewGame();
        } else {
            playGame();
        }
        dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout._result_dialog2);
        winner = dialog2.findViewById(R.id.winner);
        ok = dialog2.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.cancel();
            }
        });
    }
    ArrayList<Move> moves;
    int idx = 0;
    String status1;
    void movePlus() {
        if (idx < moves.size()) {
            Move move = moves.get(idx++);
            curType = type.get(getID(move.getX1(), move.getY1()));
            assert curType != null;
            curType.piece.performMoves(move.getX2(), move.getY2());
        } else {
            dialog2.show();
            if (status1.equals("Win")) {
                winner.setText(pp1.getName() + " Won");
            } else {
                winner.setText(pp2.getName() + " Won");
            }
        }
    }

    void moveMinus() {
        if (idx > 0) {
            Move move = moves.get(--idx);
            curType = type.get(getID(move.getX2(), move.getY2()));
            assert curType != null;
            curType.piece.performMoves(move.getX1(), move.getY1());
        }
    }
    TextView winner;
    Button ok;
    void reviewGame() {
        moves = new ArrayList<>();
        gameRef0.child("Moves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (start == false) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Move move = data.getValue(Move.class);
                        moves.add(move);
                    }
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    start = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        gameRef0.child("status1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                status1 = snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    Button newGame;
    Button reviewGame;
    void playGame() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout._result_dialog);
        result = dialog.findViewById(R.id.rrr);
        how = dialog.findViewById(R.id.how);
        rt = dialog.findViewById(R.id.rr);
        close = dialog.findViewById(R.id.close);
        newGame = dialog.findViewById(R.id.newgame);
        reviewGame = dialog.findViewById(R.id.review);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlayGame.this, Menu.class));
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        reviewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PlayGame.this, PlayGame.class);
                Menu.watchGameFinished = true;
                i.putExtra("gameid", gameID);
                startActivity(i);
            }
        });
        time1 = Play.time;
        time2 = Play.time;
        database();
        if (Play.host) {

        } else if (!Play.host) {
            if (curColor == BLACK) {
                startTimer2();
                dataReceived = false;
            } else if (curColor == WHITE) {
                startTimer1();
            }
        }
    }
    void watchGame() {
        updateQuery();
    }
    void initialQuery() {
        ref = FirebaseDatabase.getInstance().getReference();
        gameRef0 = ref.child("Games").child(gameID);
        gameRef0.child("id1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id1 = (String) snapshot.getValue();
                ref.child("Users").child(id1).child("info").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pp1 = snapshot.getValue(User.class);
                        int rating;
                        if (Play.host) {
                            p1NameTxt.setText(pp1.getName());
                            rating = pp1.getRating();
                            String ratingTxt = "(" + rating + ")";
                            p1RatingTxt.setText(ratingTxt);
                        } else {
                            p2NameTxt.setText(pp1.getName());
                            rating = pp1.getRating();
                            String ratingTxt = "(" + rating + ")";
                            p2RatingTxt.setText(ratingTxt);
                        }
                        if (ir1 == -1) ir1 = rating;
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
                            int rating;
                            if (Play.host) {
                                p2NameTxt.setText(pp2.getName());
                                rating = pp2.getRating();
                                String ratingTxt = "(" + rating + ")";
                                p2RatingTxt.setText(ratingTxt);
                            } else {
                                p1NameTxt.setText(pp2.getName());
                                rating = pp2.getRating();
                                String ratingTxt = "(" + rating + ")";
                                p1RatingTxt.setText(ratingTxt);
                            }
                            if (ir2 == -1) ir2 = rating;

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
    }
    void updateQuery() {
        gameRef0.child("Moves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (start == false) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Move move = data.getValue(Move.class);
                        System.out.println(move.getX1() + " " + move.getY1());
                        curType = type.get(getID(move.getX1(), move.getY1()));
                        assert curType != null;
                        curType.piece.performMoves(move.getX2(), move.getY2());
                        lastMove0 = lastMove1;
                        curColor = 1 - curColor;
                    }
                    start = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        gameRef0.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println(snapshot);
                System.out.println("GameID:" + gameID + " " + snapshot.child("lastMove").getKey() + " " + snapshot.child("lastMove").getValue(String.class));
                if (snapshot.child("lastMove").exists()) {
                    lastMove1 = (String) snapshot.child("lastMove").getValue();
                    if (snapshot.child("Moves").child(lastMove1).exists()) {
                        Move move = snapshot.child("Moves").child(lastMove1).getValue(Move.class);
                        if (lastMove0 != lastMove1 && move.getColor() != -1 && move.getColor() == curColor) {
                            curType = type.get(getID(move.getX1(), move.getY1()));
                            assert curType != null;
                            curType.piece.performMoves(move.getX2(), move.getY2());
                            lastMove0 = lastMove1;
                            curColor = 1 - curColor;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        gameRef0.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //GameInfo gameInfo = snapshot.getValue(GameInfo.class);
                String status = snapshot.child("status").getValue(String.class);
                //System.out.println(status);
                if (status.equals("gameOver")) {
                    String status1 = (String) snapshot.child("status1").getValue();
                    dialog2.show();
                    if (status1.equals("Win")) {
                        winner.setText(pp1.getName() + " Won");
                    } else {
                        winner.setText(pp2.getName() + " Won");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void database() {
        gameRef0.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameInfo gameInfo = snapshot.getValue(GameInfo.class);
                if (gameInfo.getStatus().equals("gameOver")) {
                    if (com.example.chess.menu_fragments.Play.host) {
                        String status1 = (String) snapshot.child("status1").getValue();
                        result.setText("You " + status1);
                        how.setText("By Checkmate");
                        String rts = "New Rating: " + (int)ir1;
                        if (gameInfo.getDel1().charAt(0) == '-') rts +=  " - " + gameInfo.getDel1().substring(1);
                        else rts +=  " + " + gameInfo.getDel1();
                        rt.setText(rts);
                        System.out.println("Hey: " + rts);
                        dialog.show();
                    } else {
                        String status2 = (String) snapshot.child("status2").getValue();
                        result.setText("You " + status2);
                        how.setText("By Checkmate");
                        String rts = "New Rating: " + (int)ir2;
                        if (gameInfo.getDel2().charAt(0) == '-') rts +=  " - " + gameInfo.getDel2().substring(1);
                        else rts +=  " + " + gameInfo.getDel2();
                        rt.setText(rts);
                        System.out.println("Hey: " + rts);
                        dialog.show();
                    }
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
                    if (curColor == WHITE && status.equals("running") && time1 == Play.time) startTimer1();
                    else if (curColor == BLACK && status.equals("running") && time2 == Play.time) startTimer2();
                }
                if (snapshot.child("lastMove").exists()) {
                    lastMove1 = (String) snapshot.child("lastMove").getValue();
                    if (snapshot.child("Moves").child(lastMove1).exists()) {
                        Move move = snapshot.child("Moves").child(lastMove1).getValue(Move.class);
                        if (lastMove0 != lastMove1 && move.getColor() != -1 && move.getColor() != curColor) {
                            if (curColor == BLACK) {
                                move.setX1(7 - move.getX1());
                                move.setX2(7 - move.getX2());
                            }
                            curType = type.get(getID(move.getX1(), move.getY1()));
                            assert curType != null;
                            curType.piece.performMoves(move.getX2(), move.getY2());
                            dataReceived = true;

                                stopTimer2();
                                startTimer1();

                            if (kingTrapped()) {
                                loss();
                            } else {
                            }
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
    int prevRating1, prevRating2, added1, added2;
    void loss() {
        startTimer1();
        stopTimer1();
        startTimer2();
        stopTimer2();
        double rating1 = pp1.getRating();
        prevRating1 = (int)rating1;
        double rating2 = pp2.getRating();
        prevRating2 = (int)rating2;
        double r1 = Math.pow(10, rating1 / 400);
        double r2 = Math.pow(10, rating2 / 400);
        double e1 = r1 / (r1 + r2);
        double e2 = r2 / (r1 + r2);
        int s1 = 0, s2 = 0;
        if (Play.host) {
            s1 = 0;
            s2 = 1;
        } else {
            s1 = 1;
            s2 = 0;
        }
        double r1prime = 32 * (s1 - e1);
        added1 = (int)r1prime;
        r1prime += rating1;
        double r2prime = 32 * (s2 - e2);
        added2 = (int)r2prime;
        r2prime += rating2;
        System.out.println(added1 + " " + added2);
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
        gameRef0.child("del1").setValue(String.valueOf(added1));
        gameRef0.child("del2").setValue(String.valueOf(added2));
        userRef0.child("info").setValue(pp1);
        userRef1.child("info").setValue(pp2);
        gameRef0.child("status").setValue("gameOver");
    }

    public boolean kingTrapped() {
        int cnt = 0;

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                int id = getID(i, j);
                Type t = type.get(id);
                assert t != null;
                if (t.piece.color == curColor) {
                    t.piece.showMoves(FALSE);
                }
                cnt += nextXY.size();
                nextXY.clear();
            }
        }
        return cnt == 0;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (moveShown) {
            for (int i = 0; i < nextXY.size(); ++i) {
                int x = nextXY.get(i).x, y = nextXY.get(i).y;
                if (getID(x, y) == id) {
                    int x1 = curType.piece.x, y1 = curType.piece.y, x2 = x, y2 = y;
                    Move move;
                    if (curColor == BLACK) {
                        x1 = 7 - x1;
                        x2 = 7 - x2;
                    }
                    move = new Move(curColor, x1, y1, x2, y2);
                    String key = gameRef0.child("Moves").push().getKey();
                    gameRef0.child("lastMove").setValue(key);
                    gameRef0.child("Moves").child(key).setValue(move);
                    curType.piece.performMoves(x, y);
                    dataReceived = false;
                    break;
                }
            }
            for (int i = 0; i < nextXY.size(); ++i) {
                int x = nextXY.get(i).x, y = nextXY.get(i).y;
                square[x][y].setBackgroundColor(0);
            }
            nextXY.clear();
            moveShown = false;
            stopTimer1();
            startTimer2();
        }
        if (dataReceived && curColor == Objects.requireNonNull(type.get(id)).color) {
            curType = type.get(id);
            assert curType != null;
            curType.piece.showMoves(FALSE);
            for (int i = 0; i < nextXY.size(); ++i) {
                int x = nextXY.get(i).x, y = nextXY.get(i).y;
                square[x][y].setBackgroundResource(R.drawable.circle);
            }
            moveShown = true;
        }
    }

    public boolean kingNotChecked(int id1, int id2) {
        Type t1 = type.get(id1), t2 = type.get(id2);
        Piece p1 = Objects.requireNonNull(t1).piece, p2 = Objects.requireNonNull(t2).piece;

        int x1 = p1.x, y1 = p1.y, id11 = p1.id, color1 = p1.color, ind1 = t1.index;
        int x2 = p2.x, y2 = p2.y, id22 = p2.id, color2 = p2.color, ind2 = t2.index;


        p1.x = x2;
        p1.y = y2;
        p1.id = id22;
        p1.color = -1;
        p2.x = x1;
        p2.y = y1;
        p2.id = id11;
        p2.color = color1;
        t1.index = -1;
        t1.color = -1;
        t2.index = ind1;
        t2.color = color1;

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                int id = getID(i, j);
                Type t = type.get(id);
                assert t != null;
                if (t.piece.color == 1 - curColor) {
                    t.piece.showMoves(TRUE);
                }
            }
        }
        boolean checked = false;
        for (Coordinates c : tempXY) {
            if (c.x == king[curColor][0].x && c.y == king[curColor][0].y) {
                checked = true;
                break;
            }
        }
        tempXY.clear();
        p1.x = x1;
        p1.y = y1;
        p1.id = id11;
        p1.color = color1;
        p2.x = x2;
        p2.y = y2;
        p2.id = id22;
        p2.color = color2;
        t1.index = ind1;
        t1.color = color1;
        t2.index = ind2;
        t2.color = color2;
        return !checked;
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
        photo1.setImageResource(R.drawable.warrior);
        photo2.setImageResource(R.drawable.warrior);
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
                if (Menu.watchGameFinished == false && Menu.watchGameRunning == false) square[i][j].setOnClickListener(PlayGame.this);
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