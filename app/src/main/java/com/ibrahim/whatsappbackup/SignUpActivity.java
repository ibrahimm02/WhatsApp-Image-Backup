package com.ibrahim.whatsappbackup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;

    private EditText SignUpInputUsername;
    private EditText SignUpInputEmail;
    private EditText SignUpInputPassword;
    private EditText SignUpInputConfirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        SignUpInputUsername = (EditText) findViewById(R.id.signUp_username_input);
        SignUpInputEmail = (EditText) findViewById(R.id.signUp_email_input);
        SignUpInputPassword = (EditText) findViewById(R.id.signUp_password_input);
        SignUpInputConfirmPassword = (EditText) findViewById(R.id.signUp_confirmPassword_input);
        CreateAccountButton = (Button) findViewById(R.id.signUp_button);
        progressDialog = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final String uname = SignUpInputUsername.getText().toString();
                String email = SignUpInputEmail.getText().toString();
                String password = SignUpInputPassword.getText().toString();
                String cpassword = SignUpInputConfirmPassword.getText().toString();

                RegisterAccount(uname,email,password, cpassword);
            }
        });


    }

    private void RegisterAccount(final String uname, final String email, String password, String cpassword) {

        if (TextUtils.isEmpty(uname)) {
            Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Email Id", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Choose a Password", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password should be minimum of 6 characters", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cpassword)) {
            Toast.makeText(this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
        } else if (!cpassword.equals(password)) {
            Toast.makeText(this, "Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please wait, while your account is being created");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String current_user_Id = mAuth.getCurrentUser().getUid();
                                storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id);

                                storeUserDefaultDataReference.child("user_name").setValue(uname);
                                storeUserDefaultDataReference.child("user_email").setValue(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Intent homeIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(homeIntent);
                                                    finish();
                                                }
                                            }
                                        });


                            } else {
                                Toast.makeText(SignUpActivity.this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });


        }
    }
}
