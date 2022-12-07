package com.deepshikhayadav.facemaskdetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 101;
    TextView name, email;
    ImageView Profile;
    FloatingActionButton fab;
    Toolbar toolbar;
    private Bitmap bitmap;
    Uri imageuri;
    String imageEncoded;
    ArrayList<Grid_model> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.emailid);
        Profile = findViewById(R.id.profile);
        name = findViewById(R.id.textView);
        fab = findViewById(R.id.camera);
        toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setTitle("Face Mask Detection");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.menu);
        arrayList = new ArrayList<>();

        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.dark) {
                SharedPreferences sharedPreferences
                        = getSharedPreferences(
                        "sharedPrefs", MODE_PRIVATE);
                final SharedPreferences.Editor editor
                        = sharedPreferences.edit();
                final boolean isDarkModeOn
                        = sharedPreferences
                        .getBoolean(
                                "isDarkModeOn", true);

                if (isDarkModeOn) {
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);

                } else {
                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);

                }

                if (isDarkModeOn) {

                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_NO);

                    editor.putBoolean(
                            "isDarkModeOn", false);
                    editor.apply();


                } else {

                    AppCompatDelegate
                            .setDefaultNightMode(
                                    AppCompatDelegate
                                            .MODE_NIGHT_YES);
                    editor.putBoolean(
                            "isDarkModeOn", true);
                    editor.apply();
                }


            } else if (item.getItemId() == R.id.logout) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Log Out");

                builder.setMessage("Do you want to log out?")
                        .setPositiveButton("Yes", (dialog, id) -> {

                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                            Toast.makeText(getApplicationContext(), "Log Out Successfully!!",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();


            }

            return true;

        });


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {


            Profile.setOnClickListener(v -> {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 12);
            });
            name.setText(signInAccount.getDisplayName());
            email.setText(signInAccount.getEmail());
        }

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(intent, CAMERA_REQUEST);


        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("sharepref", MODE_PRIVATE);

        String s = sharedPreferences.getString("imagePreferance", "");
        Profile.setImageBitmap(decodeBase64(s));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                imageuri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                SharedPreferences sharedPreferences = getSharedPreferences("sharepref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("imagePreferance", EncodeToBase64(bitmap));
                editor.commit();

                Profile.setImageBitmap(decodeBase64(imageEncoded));


            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null /*&& data.getData()!=null*/) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent i = new Intent(this, Camera.class);
            i.putExtra("img", byteArray);
            startActivity(i);


        }
    }

    private Bitmap decodeBase64(String imageEncoded) {
        byte[] decodedByte = Base64.decode(imageEncoded, 0);
        Bitmap output = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        return output;
    }

    private String EncodeToBase64(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }


    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

        //or finish();
    }


}
