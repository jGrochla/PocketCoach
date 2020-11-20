package com.example.pocketcoach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    final static String TAG = "SignInActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    TextView textViewForgotPassword; // OnClickText to reset your password
    EditText editTextEMail;          // Edit Text to put in the Mail Address
    EditText editTextPassword;       // Edit Text to put in the Password
    Button   buttonLogIn;            // Button for the Log In
    Button   buttonRegister;         // Button to register

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance(); // gets the log in status of the user

        // initialize the Views from the Log In Activity
        textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);
        editTextEMail          = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextPassword       = (EditText) findViewById(R.id.editTextPassword);
        buttonLogIn            = (Button)   findViewById(R.id.buttonLogIn);
        buttonRegister         = (Button)   findViewById(R.id.buttonRegister);

        // add On Click Listener to the Buttons and the textView
        buttonLogIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);

        // impementation of the mAuthListener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // Query of the current user
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //Command to send the confirmation email
                    user.sendEmailVerification().
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                // Logout of the user and notification that
                                // the email has been sent.
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(getApplicationContext(),
                                        R.string.verify_mail,
                                        Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        };
    }

    /***********************************************************************************************
     * ON CLICK
     * reacts when a button or the TextView is clicked
     * this method is called through the OnClickListener
     **********************************************************************************************/
    @Override
    public void onClick(View view) {
        String email = "";
        String password = "";
        switch (view.getId()) {
            case R.id.buttonLogIn:
                email = editTextEMail.getText().toString();
                password = editTextPassword.getText().toString();
                signIn(email, password);
                break;
            case R.id.buttonRegister:
                email = editTextEMail.getText().toString();
                password = editTextPassword.getText().toString();
                registrate(email, password);
                break;
            case R.id.textViewForgotPassword:
                sendResetPasswordMail();
                break;
        }
    }
    /***********************************************************************************************
     * REGISTER
     * to register a new User
     **********************************************************************************************/
    private void registrate(String email, String password){
        Log.d(TAG, "createAcccount: " + email);
        if(!validateForm()) {
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mAuthListener.onAuthStateChanged(firebaseAuth);
                        }else {
                            Toast.makeText(getApplicationContext(),
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /***********************************************************************************************
     * SIGN IN
     * to sign in an already existing User
     **********************************************************************************************/
    private void signIn(String email, String password){
        Log.d(TAG, "Log In: " + email);
        if(!validateForm()){
            return;
        }
        // sign In process
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //checks if E-Mail has been approved
                            if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.successful_log_in),
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        R.string.no_successful_log_in,
                                        Toast.LENGTH_LONG).show();
                            }
                        }else{

                            Toast.makeText(getApplicationContext(),
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    /***********************************************************************************************
     * SEND RESET PASSWORD MAIL
     * to reset the users password
     **********************************************************************************************/
    private void sendResetPasswordMail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setTitle(getString(R.string.reset_dialog_titel));
        final EditText mailInput = new EditText(this);
        mailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(mailInput);

        builder.setPositiveButton(getString(R.string.positiv_button_text),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String mail = mailInput.getText().toString().trim();
                if(mail.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.warning_no_email_text),
                            Toast.LENGTH_LONG).show();
                    dialogInterface.dismiss();
                }else{
                    firebaseAuth.sendPasswordResetEmail(mail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "Email send");
                                        Toast.makeText(getApplicationContext(),
                                                getString(R.string.email_send_info),
                                                Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(),
                                                task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        builder.setNegativeButton(getString(R.string.negativ_button_text),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /***********************************************************************************************
     * VALIDATE FORM
     * to check if the input of the Edit Text View is valid
     **********************************************************************************************/
    private boolean validateForm(){
        boolean valid = true;

        String email = editTextEMail.getText().toString();
        if(email.isEmpty()){
            editTextEMail.setError("Required.");
            valid = false;
        }else {
            editTextEMail.setError(null);
        }

        String password = editTextPassword.getText().toString();
        if(password.isEmpty()){
            editTextPassword.setError("Required");
            valid = false;
        }else{
            editTextPassword.setError(null);
        }
        return valid;
    }

}