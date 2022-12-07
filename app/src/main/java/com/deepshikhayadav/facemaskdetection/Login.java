package com.deepshikhayadav.facemaskdetection;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    ImageView imageView, Visible;
    Button signin;
    TextView forgot;
    EditText logemail, logpass;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    Boolean c = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setTitle("Sign in");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        imageView = findViewById(R.id.imageView);
        signin = findViewById(R.id.signin);
        forgot = findViewById(R.id.forgot);
        logemail = findViewById(R.id.logEmail);
        logpass = findViewById(R.id.logPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Visible = findViewById(R.id.visible);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        String image_url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQWZo3Onmz9BQjqD89qNIbFhP2U2jWaeMa4TQ&usqp=CAU";
        Glide.with(this).load(image_url).apply(options).into(imageView);

        forgot.setOnClickListener(v -> resetPassword());

        Visible.setOnClickListener(v -> {
            if (c != true) {
                logpass.setTransformationMethod(null);
                Visible.setImageResource(R.drawable.visibility_off);
                c = true;

            } else {
                logpass.setTransformationMethod(new PasswordTransformationMethod());
                Visible.setImageResource(R.drawable.eye);
                c = false;
            }


        });


        auth = FirebaseAuth.getInstance();

        signin.setOnClickListener(v -> {
            String email = logemail.getText().toString();
            final String password = logpass.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {

                                Toast.makeText(Login.this, "Auth Failed!", Toast.LENGTH_LONG).show();

                            } else {
                                checkIfEmailVerified();

                            }
                        }
                    });

        });


    }

    private void checkIfEmailVerified() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {

            Toast.makeText(Login.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {

            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Login.this, "Verify Your Email Address", Toast.LENGTH_SHORT).show();


        }
    }


    private void resetPassword() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_forgot_pass, null);
        dialogBuilder.setView(dialogView);
        final EditText editEmail = (EditText) dialogView.findViewById(R.id.editText);
        final Button btnReset = (Button) dialogView.findViewById(R.id.buttonclick);
        final ProgressBar progressBar1 = (ProgressBar) dialogView.findViewById(R.id.progressBar1);
        final AlertDialog dialog = dialogBuilder.create();

        btnReset.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar1.setVisibility(View.VISIBLE);

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar1.setVisibility(View.GONE);
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }

}
