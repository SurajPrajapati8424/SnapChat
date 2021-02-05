package com.dr.psycho.snapchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class RegistrationActivity extends AppCompatActivity {

    Button mRegistration;
    EditText mName,mEmail,mPassword1,mPassword2;
    ProgressBar mLoading;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(getApplication(), RegistrationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();

        mLoading = findViewById(R.id.LogLoading);
        mRegistration = findViewById(R.id.btnLogin);

        mName = findViewById(R.id.etUsername);
        mEmail = findViewById(R.id.etUsername);
        mPassword1 = findViewById(R.id.etRegPassword1);
        mPassword2 =findViewById(R.id.etRegPassword2);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final  String password = mPassword1.getText().toString();
                final String password2 = mPassword2.getText().toString();

                if (password.equals(password2)){
                    mAuth.createUserWithEmailAndPassword(email,password2).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(getApplication(),"Sign in Error",Toast.LENGTH_SHORT).show();
                            }
                        };
                    });
                }else{
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUSerDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                    Map userInfo = new HashMap<>();
                    userInfo.put("email",email);
                    userInfo.put("name",mName);
                    userInfo.put("profileImageUrl","default");

                    currentUSerDb.updateChildren(userInfo);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}