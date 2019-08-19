package com.ibrahim.whatsappbackup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;

public class OpenLargerImageActivity extends AppCompatActivity {

    FirebaseStorage firebaseStorage;

    ImageView myImage;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_larger_image);

        url = getIntent().getStringExtra("image_url");

        Log.d("Uri", url);

        myImage = findViewById(R.id.myImage);
        Glide.with(this).load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(myImage);
    }
}
