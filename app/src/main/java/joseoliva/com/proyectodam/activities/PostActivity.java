package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.ImageProvider;
import joseoliva.com.proyectodam.providers.PostProvider;
import joseoliva.com.proyectodam.utils.FileUtil;
import joseoliva.com.proyectodam.utils.ViewedMessageHelper;

public class PostActivity extends AppCompatActivity {

    ImageView mIvpost1;
    ImageView mIvpost2;
    File mImageFile;
    File mImageFile2;
    CircleImageView mCircleImageBack;
    Button mButtonPost;
    ImageProvider mImageProvider;
    PostProvider mPostprovider;
    AuthProvider mAuthprovider;
    TextInputEditText mTextInputlugar;
    TextInputEditText mTextInputdescripcion;
    ImageView mIvespa;
    ImageView mIvportu;
    ImageView mIvfrancia;
    ImageView mIveuro;
    TextView mTvRegion;
    private final int GALLERY_REQUEST_CODE = 1; //creo esta var porque es necesaria para el startactivityforresult
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;
    String mRegion = "";
    String mlugar = "";
    String mdescripcion = "";
    AlertDialog mDialog;
    AlertDialog.Builder mBuilderSelector; //este dialog es para dar la opcion de tomar foto de la galeria o la camara
    CharSequence options[]; //este array es para mostrar las opciones de AlertDialogBuilder
    //FOTO1
    String mAbsolutePhotoPath; //esta variable sera para guardar el path de la foto que hagamos con la camara
    String mPhotoPath;
    File mPhotoFile;
    //FOTO2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mImageProvider = new ImageProvider();
        mPostprovider = new PostProvider();
        mAuthprovider = new AuthProvider();

        mTextInputlugar =(TextInputEditText)findViewById(R.id.tvlugar);
        mTextInputdescripcion =(TextInputEditText)findViewById(R.id.tvdescrip);
        mIvespa = (ImageView)findViewById(R.id.ivespa);
        mIvportu = (ImageView)findViewById(R.id.ivportugal);
        mIvfrancia = (ImageView)findViewById(R.id.ivfrancia);
        mIveuro = (ImageView)findViewById(R.id.iveuro);
        mTvRegion = (TextView)findViewById(R.id.tvregion);
        mCircleImageBack = (CircleImageView)findViewById(R.id.circleImageBack);
        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de galería", "Tomar Foto"};

        mButtonPost = (Button)findViewById(R.id.btnPost);
        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickPost();
            }
        });

        mIvpost1 = (ImageView)findViewById(R.id.ivpost1);
        mIvpost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectOptionImage( 1);

            }
        });

        mIvpost2 = (ImageView)findViewById(R.id.ivpost2);
        mIvpost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectOptionImage( 2);
            }
        });

        mIvespa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegion = "ESPAÑA";
                mTvRegion.setText(mRegion);
            }
        });
        mIvportu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegion = "PORTUGAL";
                mTvRegion.setText(mRegion);
            }
        });
        mIvfrancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegion = "FRANCIA";
                mTvRegion.setText(mRegion);
            }
        });
        mIveuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegion = "EUROPA";
                mTvRegion.setText(mRegion);
            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    if(numberImage == 1){
                        openGallery(GALLERY_REQUEST_CODE);
                    }
                    else if(numberImage == 2){
                        openGallery(GALLERY_REQUEST_CODE_2);
                    }
                }
                else if(i == 1){
                    if(numberImage == 1){
                        tomarFoto(PHOTO_REQUEST_CODE);
                    }
                    else if(numberImage == 2){
                        tomarFoto(PHOTO_REQUEST_CODE_2);
                    }
                }
            }
        });

        mBuilderSelector.show();
    }

    private void tomarFoto(int requestCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createFotoFile(requestCode);
            }catch (Exception e){
                Toast.makeText(this,"error con el archivo",Toast.LENGTH_SHORT).show();
            }
            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this,"joseoliva.com.proyectodam",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent, requestCode );

            }
        }
    }

    private File createFotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",".jpg",
                storageDir
        );
        if(requestCode == PHOTO_REQUEST_CODE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        else if(requestCode == PHOTO_REQUEST_CODE_2){
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
    }

    private void clickPost() {
        mlugar = mTextInputlugar.getText().toString();
        mdescripcion = mTextInputdescripcion.getText().toString();

        if(!mlugar.isEmpty() && !mdescripcion.isEmpty() && !mRegion.isEmpty()){
            //si selecciona ambas imagenes de la galeria
            if(mImageFile != null && mImageFile2 !=null){
                saveImage(mImageFile,mImageFile2);
            }
            //si selecciona las dos fotos de la camara
            else if(mPhotoFile != null && mPhotoFile2 != null){
                saveImage(mPhotoFile,mPhotoFile2);
            }
            //si selecciona 1 de galeria y la otra de la camara
            else if(mImageFile != null && mPhotoFile2 != null){
                saveImage(mImageFile,mPhotoFile2);
            }
            //si selecciona 1 de la camara y la otra de la galeria
            else if(mPhotoFile != null && mImageFile2 != null){
                saveImage(mPhotoFile,mImageFile2);
            }
            else{
                Toast.makeText(this,"Debes seleccionar una imagen",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Completa todos los campos para publicar",Toast.LENGTH_SHORT).show();
        }

    }


    private void saveImage(File imageFile1, File imageFile2) {
        mDialog.show();
        mImageProvider.save(PostActivity.this,imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();//obtenemos la url de la imagen y la pasamos a string para pasarla a la bbdd

                            mImageProvider.save(PostActivity.this,imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();
                                                Post post = new Post();
                                                post.setImage1(url);
                                                post.setImage2(url2);
                                                post.setLugar(mlugar);
                                                post.setDescripcion(mdescripcion);
                                                post.setRegion(mRegion);
                                                post.setIdUser(mAuthprovider.getUid());
                                                post.setTimestamp(new Date().getTime());
                                                mPostprovider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull  Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if(taskSave.isSuccessful()){
                                                            clearForm();
                                                        }else{
                                                            Toast.makeText(PostActivity.this,"No se pudo almacenar la informacion",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else{
                                        mDialog.dismiss();
                                        Toast.makeText(PostActivity.this,"No se pudo almacenar la imagen 2",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                }else{
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this,"Error al almacenar imagen",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void clearForm() {
        mTextInputlugar.setText("");
        mTextInputdescripcion.setText("");
        mTvRegion.setText("REGIÓN");
        mIvpost1.setImageResource(R.drawable.subirimagen);
        mIvpost2.setImageResource(R.drawable.subirimagen);
        mlugar = "";
        mdescripcion = "";
        //mRegion = "REGIÓN";
        mImageFile = null;
        mImageFile2 = null;

    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,requestCode);

    }

    //startactivityforresult necesita que sobreescribamos este metodo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //el onActivityResult es el metodo que espera que el usuario realice una accion (en este caso ir a la galeria)
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * SELECCION DE IMAGEN CON GALERIA
         */
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            //si el usuario ha hecho lo que esperamos y obtenemos una imagen...
            try{
                mPhotoFile = null;
                mImageFile = FileUtil.from(this,data.getData()); //aqui obtenemos la URI convertida a archivo
                mIvpost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));//colocamos en el imageview la imagen seleccionada
            }catch(Exception e){
                Log.d("ERROR", "no has cargado ninguna imagen" + e.getMessage());
                Toast.makeText(this,"se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            //si el usuario ha hecho lo que esperamos y obtenemos una imagen...
            try{
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this,data.getData()); //aqui obtenemos la URI convertida a archivo
                mIvpost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));//colocamos en el imageview la imagen seleccionada
            }catch(Exception e){
                Log.d("ERROR", "no has cargado ninguna imagen" + e.getMessage());
                Toast.makeText(this,"se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**SELECCION DE FOTOGRAFIA
         */
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(PostActivity.this).load(mPhotoPath).into(mIvpost1);
        }
        /**SELECCION DE FOTOGRAFIA
         */
        if(requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK){
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(PostActivity.this).load(mPhotoPath2).into(mIvpost2);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,PostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,PostActivity.this);
    }
}