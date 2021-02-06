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

public class RegistrationActivity extends AppCompatActivity {

    //Step 01
    public Button rRegistration;
    private EditText rName,rEmail,rPassword1,rPassword2;
    public ProgressBar rLoading;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    //step 02
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
        //Step 03  Copy from LoginActivity.
        rName = findViewById(R.id.etRegName);
        rEmail = findViewById(R.id.etRegEmail);
        rPassword1 = findViewById(R.id.etRegPassword1);
        rPassword2 =findViewById(R.id.etRegPassword2);
        rRegistration = findViewById(R.id.btnRegRegistration);
        rLoading = findViewById(R.id.RegLoading);

        mAuth = FirebaseAuth.getInstance();

        //Step 04 Copied and Added name variable
        rRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rLoading.setVisibility(View.VISIBLE);
                final  String name = rName.getText().toString();
                final String email = rEmail.getText().toString();
                final  String password = rPassword1.getText().toString();
                final String password2 = rPassword2.getText().toString();


                    //Step 05   signInWithEmailAndPassword replaced by createWithEmailAndPassword
                mAuth.createUserWithEmailAndPassword(email,password2).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            rLoading.setVisibility(View.GONE);
                            Toast.makeText(getApplication(),"Sign in Error",Toast.LENGTH_SHORT).show();
                        }
                        //Step 06   User will add in signIn Not database, Here I get UserId and create it in database.
                        else{
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                            Map userInfo= new HashMap<>();
                            userInfo.put("email",email);
                            userInfo.put("name",name);
                            userInfo.put("psk",password2);
                            userInfo.put("profileImageUrl","default");

                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
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