package com.martirio.firebase_entregable.Vista.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.storage.UploadTask;
import com.martirio.firebase_entregable.Modelo.Artist;
import com.martirio.firebase_entregable.Modelo.Paint;
import com.martirio.firebase_entregable.R;
import com.martirio.firebase_entregable.Vista.Adapters.AdapterPinturas;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SegundaActivity extends AppCompatActivity {

    Button botonLogout;
    private LoginManager facebookLoginManager;
    private DatabaseReference mDatabase;

    private List<Paint> listaPinturas;
    private RecyclerView recyclerImagenes;
    private AdapterPinturas adapterPinturas;

    private RecyclerView recyclerFotos;
    private AdapterPinturas adapterFotos;
    private List<Paint> listaFotos;

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

        //RECYCLER PINTURAS
        listaPinturas=new ArrayList<>();
        recyclerImagenes=(RecyclerView)findViewById(R.id.recyclerImagenes);
        recyclerImagenes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterPinturas=new AdapterPinturas();
        adapterPinturas.setContext(this);
        adapterPinturas.setlistaPinturasOriginales(listaPinturas);
        recyclerImagenes.setAdapter(adapterPinturas);

        //RECYCLER FOTOS
        listaFotos=new ArrayList<>();
        recyclerFotos=(RecyclerView)findViewById(R.id.recyclerFotos);
        recyclerFotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterFotos=new AdapterPinturas();
        adapterFotos.setContext(this);
        adapterFotos.setlistaPinturasOriginales(listaFotos);
        recyclerFotos.setAdapter(adapterFotos);

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

        //ACTUALIZAR LISTA PINTURAS
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference referenceFoto = mDatabase.child("fotos");
        referenceFoto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Paint> listaPintuas = new ArrayList<Paint>();
                for (DataSnapshot paintSnapShot: dataSnapshot.getChildren()) {

                    Paint pintura = paintSnapShot.getValue(Paint.class);
                    listaPintuas.add(pintura);
                }
                for (Paint unaPintura:listaPintuas
                        ) {
                    descargarFoto(unaPintura);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        });

        //ACTUALIZAR LISTA PINTURAS
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesref = storage.getReference();

    }
    public void descargarFotos(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesref = storage.getReference().child("fotos");

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
                adapterPinturas.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void descargarFoto(final Paint unaPintura){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesref = storage.getReference();

        String rutaImagen = unaPintura.getImage();

        StorageReference imageDownload = imagesref.child(rutaImagen);
        imageDownload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Paint unaPinturita= new Paint();
                unaPinturita.setImage(uri.toString());
                unaPinturita.setName(unaPintura.getName());
                adapterFotos.agregarPintura(unaPinturita);
                adapterFotos.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void readData(View view) {
        super.onStart();

        //QUIERO LEER UNA UNICA VEZ LOS DATOS
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = mDatabase.child("artists");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void tomarFoto(View view){
        EasyImage.openChooserWithGallery(this, "Tomar foto", 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                //ImageView imageView = (ImageView) findViewById(R.id.imageView);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                //imageView.setImageBitmap(bitmap);

                //ESCALAR IMAGEN
                uploadToFirebase(bitmap);
            }
        });
    }



    public void uploadToFirebase(Bitmap bitmap){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesref = storage.getReference().child("fotos");
        final String nombreFoto= UUID.randomUUID().toString();
        StorageReference imageUpload= imagesref.child(nombreFoto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageUpload.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Paint foto =new Paint();
                foto.setName(nombreFoto);
                foto.setImage(downloadUrl.toString());
                listaFotos.add(foto);
                adapterFotos.notifyDataSetChanged();

                DatabaseReference databaseReference =mDatabase.child("fotos");
                databaseReference.push().setValue(foto);



            }
        });


    }



}
