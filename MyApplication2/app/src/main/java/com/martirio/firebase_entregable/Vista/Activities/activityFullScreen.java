package com.martirio.firebase_entregable.Vista.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.martirio.firebase_entregable.R;
import com.squareup.picasso.Picasso;

public class activityFullScreen extends AppCompatActivity {

    public static final String IMAGEN="IMAGEN";
    private ImageView fullscreen;
    private String rutaFoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        fullscreen=(ImageView)findViewById(R.id.imagenFullScreen);
        Intent unIntent = getIntent();
        Bundle unBundle=unIntent.getExtras();
        rutaFoto=unBundle.getString(IMAGEN);
        Picasso.with(fullscreen.getContext())
                .load(rutaFoto)
                .placeholder(R.drawable.loading)
                .into(fullscreen);

    }
}
