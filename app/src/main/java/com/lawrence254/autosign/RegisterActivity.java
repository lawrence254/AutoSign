package com.lawrence254.autosign;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.uEmail)EditText mEmail;
    @BindView(R.id.uPass)EditText mPass;
    @BindView(R.id.uregister)Button mRegister;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            Intent intent =  new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }

        mRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v==mRegister){
            String email = mEmail.getText().toString().trim();
            String pass = mPass.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(pass)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                insertData(user);
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void insertData(FirebaseUser user) {
        if (user != null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Map<String, String> map = new HashMap<>();
            map.put("email",user.getEmail());
            map.put("class","JAVA-MC9");

            Toast.makeText(this, "Reg: "+map, Toast.LENGTH_SHORT).show();
            ref.child("students").child(user.getUid()).setValue(map);
            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}

