package com.deepshikhayadav.facemaskdetection;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    ImageView imageView, Visible, Visible2;
    EditText email, pass, pass2;
    Button button;
    TextView account, Alert;
    FirebaseAuth mFirebaseAuth;
    Boolean c = true;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setTitle("Sign up");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mFirebaseAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.signup);
        email = findViewById(R.id.Email);
        pass = findViewById(R.id.Password);
        pass2 = findViewById(R.id.Password2);
        account = findViewById(R.id.act);
        Visible = findViewById(R.id.visible);
        Visible2 = findViewById(R.id.visible2);
        Alert = findViewById(R.id.alert);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        String image_url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQWZo3Onmz9BQjqD89qNIbFhP2U2jWaeMa4TQ&usqp=CAU";
        Glide.with(this).load(image_url).apply(options).into(imageView);


        Visible2.setOnClickListener(v -> {
            if (c != true) {
                pass2.setTransformationMethod(null);
                Visible2.setImageResource(R.drawable.visibility_off);
                c = true;

            } else {
                pass2.setTransformationMethod(new PasswordTransformationMethod());
                Visible2.setImageResource(R.drawable.eye);
                c = false;
            }
        });

        Visible.setOnClickListener(v -> {
            if (c != true) {
                pass.setTransformationMethod(null);
                Visible.setImageResource(R.drawable.visibility_off);
                c = true;

            } else {
                pass.setTransformationMethod(new PasswordTransformationMethod());
                Visible.setImageResource(R.drawable.eye);
                c = false;
            }
        });
        account.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));


        button.setOnClickListener(v -> {
            String Email = email.getText().toString().trim();
            String password = pass.getText().toString().trim();
            String password2 = pass2.getText().toString().trim();

            if (TextUtils.isEmpty(Email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            } else {
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.length() < 8) {
                        Alert.setVisibility(View.VISIBLE);
                        Alert.setText(R.string.greaterthan6);
                    } else {
                        Alert.setVisibility(View.GONE);
                        if (!isValidPassword(password)) {
                            Alert.setVisibility(View.VISIBLE);
                            Alert.setText(R.string.passAlert);
                        } else {
                            Alert.setVisibility(View.GONE);
                            if (!password.equals(password2)) {
                                Toast.makeText(getApplicationContext(), "Password mismatch", Toast.LENGTH_SHORT).show();

                            } else {

                                progressBar.setVisibility(View.VISIBLE);

                                mFirebaseAuth.createUserWithEmailAndPassword(Email, password)
                                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {


                                                Toast.makeText(Register.this, "Login Successful!" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);

                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(Register.this, "Authentication failed." + task.getException(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    sendVerificationEmail();

                                                }

                                            }
                                        });
                            }
                        }


                    }
                }
            }

        });


    }

    private boolean isValidPassword(String password) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");

        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }


    private void sendVerificationEmail() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    private static final String TAG = "log";

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            Toast.makeText(Register.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(Register.this, Login.class));
                            finish();
                        } else {

                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Register.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}