package com.example.pocketcoach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView  textViewLogInStatus;     // to show the Log in Status of the user

    private FirebaseAuth firebaseAuth; // to check if the user is logged in



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLogInStatus = (TextView) findViewById(R.id.textViewLogInStatus); // initialization fot the Text View

        firebaseAuth = FirebaseAuth.getInstance(); // gets the log in status of the user

        if(firebaseAuth.getCurrentUser() == null) {
            textViewLogInStatus.setText(getString(R.string.not_logged_in));

            //forward user to login
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);

        }else {
            textViewLogInStatus.setText(getString(R.string.logged_in));
        }
    }

    /***********************************************************************************************
     * ON CREATE OPTIONS MENU
     * Includes the drop down menu
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logoutmenu, menu);
        return true;
    }

    /***********************************************************************************************
     * ON OPTION ITEM SELECTED
     * gives a function to the menu
     **********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.log_out_info_negativ),
                    Toast.LENGTH_SHORT).show();
            //forward user to login
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(),
                    getString(R.string.log_out_info_positiv),
                    Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            //forward user to login
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}