package de.griot_app.griot.startactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

import de.griot_app.griot.R;
import de.griot_app.griot.mainactivities.MainOverviewActivity;

/**
 * This activity allways launches, if Griot-app got started new. It shows a splash-Screen with Griot logo and checks the authentication state.
 * After a time out, it starts either MainOverviewActivity, if a user is already signed in, or LoginActivity, if no user is signed in.
 */
public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Setup the AuthStateListener, which checks the authentication state, when added
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Intent intent;
                if (user != null) {
                    //If a user is signed in, start MainOverviewActivity
                    Log.d(TAG, "onAuthStateChanged: signed in: " + user.getUid());
                    intent = new Intent(StartActivity.this, MainOverviewActivity.class);
                } else {
                    //If no user is signed in, start LoginActivity
                    Log.d(TAG, "onAuthStateChanged: signed out: ");
                    intent = new Intent(StartActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        };
    }

    //start a Timer for display duration of StartActivity. After time out the AuthStateListener from above is added
    @Override
    protected void onStart() {
        super.onStart();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mAuth.addAuthStateListener(mAuthListener);
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
