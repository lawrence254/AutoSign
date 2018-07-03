package com.lawrence254.autosign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lawrence254.autosign.client.ClientActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if() {
                    savedItems = sharedpreferences.getString(MyPREFERENCES.toString(), "");
                    selectedItems.addAll(Arrays.asList(savedItems.split(",")));

                }

                Toast.makeText(this, "Welcome to StockTrack "+user.getDisplayName()+". Your news sources: "+savedItems, Toast.LENGTH_LONG).show();
                if(sharedpreferences.contains(MyPREFERENCES)){

                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this,ClientActivity.class);
                    startActivity(intent);
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                response.getError().getErrorCode();
            }
        }
    }
}
