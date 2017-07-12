package com.martirio.firebase_entregable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.ArrayList;
import java.util.List;

public class SegundaActivity extends AppCompatActivity {

    Button botonLogout;
    private LoginManager facebookLoginManager;
    private DatabaseReference mDatabase;

    private List<Paint> listaPinturas;
    private RecyclerView recyclerImagenes;
    private AdapterPinturas adapterPinturas;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segunda);
        // Create a storage reference from our app

        botonLogout=(Button) findViewById(R.id.btn_logout);
        botonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            logoutTwitter();
                logoutFacebook();
                logoutFirebase();
                ClearCookies(v.getContext());
                irAMainActivity();
            }
        });
        listaPinturas=new ArrayList<>();
        recyclerImagenes=(RecyclerView)findViewById(R.id.recyclerImagenes);
        recyclerImagenes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterPinturas=new AdapterPinturas();
        adapterPinturas.setContext(this);
        adapterPinturas.setlistaPinturasOriginales(listaPinturas);
        recyclerImagenes.setAdapter(adapterPinturas);

    }

    public void logoutTwitter() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            ClearCookies(getApplicationContext());
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        }
    }

    public void logoutFacebook(){
        facebookLoginManager = LoginManager.getInstance();
        ClearCookies(getApplicationContext());
        facebookLoginManager.logOut();
    }

    public static void ClearCookies(Context context) {
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

    }

    public void logoutFirebase(){
        ClearCookies(getApplicationContext());
        FirebaseAuth.getInstance().signOut();
    }

    public void irAMainActivity(){
        Intent unIntent= new Intent(this, MainActivity.class);
        startActivity(unIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //QUIERO ESCUCHAR LA LISTA DE DATOS CADA VEZ QUE SE MODIFICA
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = mDatabase.child("artists");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Artist> listaArtistas = new ArrayList<Artist>();
                for (DataSnapshot artistSnaptshot: dataSnapshot.getChildren()) {

                    Artist artista = artistSnaptshot.getValue(Artist.class);
                    listaArtistas.add(artista);
                }
                for (Artist unArtista:listaArtistas
                     ) {
                    for (Paint unaPintura:unArtista.getPaints()
                         ) {
                            descargarImagen(unArtista, unaPintura);

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        });


        Toast.makeText(SegundaActivity.this, "Estoy leyendo datos", Toast.LENGTH_SHORT).show();

    }

    public void descargarImagen(Artist unArtista, final Paint unaPintura){

       FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesref = storage.getReference();

        String rutaImagen = unaPintura.getImage();
        rutaImagen=rutaImagen.replace(unArtista.getName().toLowerCase(),unArtista.getName().toLowerCase()+"_");


        StorageReference imageDownload = imagesref.child(rutaImagen);
        imageDownload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Paint unaPinturita= new Paint();
                unaPinturita.setImage(uri.toString());
                unaPinturita.setName(unaPintura.getName());
                adapterPinturas.agregarPintura(unaPinturita);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

/*
    public void readData(View view) {
        super.onStart();

        //QUIERO LEER UNA UNICA VEZ LOS DATOS
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = mDatabase.child("timeLine");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Tweet> tweetList = new ArrayList<Tweet>();
                for (DataSnapshot tweetSnaptshot: dataSnapshot.getChildren()) {

                    Tweet tweet = tweetSnaptshot.getValue(Tweet.class);
                    tweetList.add(tweet);
                }
                Toast.makeText(Main2Activity.this, tweetList.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        });


        Toast.makeText(Main2Activity.this, "Estoy leyendo datos", Toast.LENGTH_SHORT).show();

    }



    public void updateData(View view){

        mDatabase = FirebaseDatabase.getInstance().getReference();

        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet("Aaaaa","aaaaa","aaaaa"));
        tweets.add(new Tweet("Aaaaa","aaaaa","aaaaa"));
        tweets.add(new Tweet("Aaaaa","aaaaa","aaaaa"));

        mDatabase.child("timeLine").setValue(tweets);
    }

    public void updateTweet(View view){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("timeLine").child("1").child("apellido").setValue("Furlan");
    }

    public void writeDataWithPush(View view){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Tweet tweet = new Tweet("aaaa","aasadas","asdahs");
        mDatabase.child("timeLine").push().setValue(tweet);

    }

    public void writeDataConMiPropiaKey(View view){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Tweet tweet = new Tweet("aaaa","aasadas","asdahs");
        mDatabase.child("timeLine").child("miPropiaKey").setValue(tweet);

    }
*/
}
