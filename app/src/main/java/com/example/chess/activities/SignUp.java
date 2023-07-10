package com.example.chess.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.R;
import com.example.chess.userinformation.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUp extends AppCompatActivity {

    EditText name, email, password;
    Button register;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void initialize() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");
    }
    private void createUser() {
        String namex = name.getText().toString();
        String emailx = email.getText().toString();
        String passwordx = password.getText().toString();
        mAuth.createUserWithEmailAndPassword(emailx, passwordx).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fuser = mAuth.getCurrentUser();
                    User user = new User(fuser.getUid(), namex, emailx, 0, 0, 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
                    String currentDate = sdf.format(new Date());
                    user.setJoinDate(currentDate);
                    ref.child(fuser.getUid()).child("info").setValue(user);
                    Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUp.this, Menu.class));
                } else {
                    Toast.makeText(SignUp.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}