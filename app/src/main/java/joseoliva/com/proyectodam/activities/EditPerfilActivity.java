package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.models.User;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.ImageProvider;
import joseoliva.com.proyectodam.providers.UserProvider;
import joseoliva.com.proyectodam.utils.FileUtil;
import joseoliva.com.proyectodam.utils.ViewedMessageHelper;

public class EditPerfilActivity extends AppCompatActivity {

    CircleImageView mcircleImflecha;
    CircleImageView mcircleimagperfil;
    ImageView mIvportada;
    TextInputEditText mTexinpunusuario;
    TextInputEditText mTextinputdron;
    Button mBtnActualizarPerfil;

    AlertDialog.Builder mBuilderSelector; //este dialog es para dar la opcion de tomar foto de la galeria o la camara
    CharSequence options[]; //este array es para mostrar las opciones de AlertDialogBuilder
    private final int GALLERY_REQUEST_CODE_PROFILE = 1; //creo esta var porque es necesaria para el startactivityforresult
    private final int GALLERY_REQUEST_CODE_COVER = 2;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;
    private final int PHOTO_REQUEST_CODE_COVER = 4;
    //FOTO1
    String mAbsolutePhotoPath; //esta variable sera para guardar el path de la foto que hagamos con la camara
    String mPhotoPath;
    File mPhotoFile;
    //FOTO2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    File mImageFile;
    File mImageFile2;

    String username="";
    String modeloDron="";
    String mImagePerfil="";
    String mImagePortada = "";

    AlertDialog mDialog;

    ImageProvider mImageProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTexinpunusuario = findViewById(R.id.tvusuario);
        mTextinputdron = findViewById(R.id.tvmodeldron);

        mcircleimagperfil = findViewById(R.id.circleimageperfil);
        mcircleimagperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(1);
            }
        });
        mIvportada = findViewById(R.id.ivportada);
        mIvportada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOptionImage(2);
            }
        });

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opción");
        options = new CharSequence[]{"Imagen de galería", "Tomar Foto"};

        mcircleImflecha = findViewById(R.id.circleImageBack);
        mcircleImflecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtnActualizarPerfil = findViewById(R.id.btnactperfil);
        mBtnActualizarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActualizaPerfil();
            }
        });
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mImageProvider = new ImageProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        //llamo al metodo que trae los datos del usuario que se ha registrado para cargar su info
        getUser();
    }

    private void getUser(){
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        username = documentSnapshot.getString("username");
                        mTexinpunusuario.setText(username);
                    }
                    if(documentSnapshot.contains("model_dron")){
                        modeloDron = documentSnapshot.getString("model_dron");
                        mTextinputdron.setText(modeloDron);
                    }
                    if(documentSnapshot.contains("imageperfil")){
                        mImagePerfil = documentSnapshot.getString("imageperfil");
                        if(mImagePerfil != null){
                            if(!mImagePerfil.isEmpty()){
                                Picasso.with(EditPerfilActivity.this).load(mImagePerfil).into(mcircleimagperfil);
                            }
                        }
                    }
                    if(documentSnapshot.contains("imageportada")){
                        mImagePortada = documentSnapshot.getString("imageportada");
                        if(mImagePortada !=null){
                            if(!mImagePortada.isEmpty()){
                                Picasso.with(EditPerfilActivity.this).load(mImagePortada).into(mIvportada);
                            }
                        }
                    }
                }
            }
        });
    }

    private void clickActualizaPerfil() {
        username = mTexinpunusuario.getText().toString();
        modeloDron = mTextinputdron.getText().toString();
        if(!username.isEmpty() && !modeloDron.isEmpty()){
            if(mImageFile != null && mImageFile2 !=null){
                saveImagePortadaYPerfil(mImageFile,mImageFile2);
            }
            //si selecciona las dos fotos de la camara
            else if(mPhotoFile != null && mPhotoFile2 != null){
                saveImagePortadaYPerfil(mPhotoFile,mPhotoFile2);
            }
            //si selecciona 1 de galeria y la otra de la camara
            else if(mImageFile != null && mPhotoFile2 != null){
                saveImagePortadaYPerfil(mImageFile,mPhotoFile2);
            }
            //si selecciona 1 de la camara y la otra de la galeria
            else if(mPhotoFile != null && mImageFile2 != null){
                saveImagePortadaYPerfil(mPhotoFile,mImageFile2);
            }
            else if(mPhotoFile != null){
                saveImagen(mPhotoFile, true);
            }
            else if(mPhotoFile2 != null){
                saveImagen(mPhotoFile2,false);
            }
            else if(mImageFile != null){
                saveImagen(mImageFile,true);
            }
            else if(mImageFile2 != null){
                saveImagen(mImageFile2,false);
            }
            else{
                User user = new User();
                user.setUsername(username);
                user.setModel_dron(modeloDron);
                user.setId(mAuthProvider.getUid());
                updateInfo(user);
            }
        }
        else{
            Toast.makeText(this,"Rellene todos los campos",Toast.LENGTH_SHORT).show();
        }
    }
    private void saveImagePortadaYPerfil(File imageFile1, File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditPerfilActivity.this,imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlperfil = uri.toString();//obtenemos la url de la imagen y la pasamos a string para pasarla a la bbdd

                            mImageProvider.save(EditPerfilActivity.this,imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlportada = uri2.toString();
                                                User user = new User();
                                                user.setUsername(username);
                                                user.setModel_dron(modeloDron);
                                                user.setImageperfil(urlperfil);
                                                user.setImageportada(urlportada);
                                                user.setId(mAuthProvider.getUid());
                                                updateInfo(user);
                                            }
                                        });
                                    }
                                    else{
                                        mDialog.dismiss();
                                        Toast.makeText(EditPerfilActivity.this,"No se pudo almacenar la imagen 2",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(EditPerfilActivity.this,"Error al almacenar imagen",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveImagen(File image, boolean isPerfilImage){
        mDialog.show();
        mImageProvider.save(EditPerfilActivity.this,image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();//obtenemos la url de la imagen y la pasamos a string para pasarla a la bbdd
                            User user = new User();
                            user.setUsername(username);
                            user.setModel_dron(modeloDron);
                            if(isPerfilImage){
                                user.setImageperfil(url);
                                user.setImageportada(mImagePortada);
                            }
                            else{
                                user.setImageportada(url);
                                user.setImageperfil(mImagePerfil);
                            }
                            user.setId(mAuthProvider.getUid());
                            updateInfo(user);
                        }
                    });
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(EditPerfilActivity.this,"Error al almacenar imagen",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateInfo(User user){
        if(mDialog.isShowing()) {
            mDialog.show();
        }
        mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(EditPerfilActivity.this,"la info se actualizo correctamente",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(EditPerfilActivity.this,"No se pudo actualizar la info",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectOptionImage(final int numberImage) {

        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    if(numberImage == 1){
                        openGallery(GALLERY_REQUEST_CODE_PROFILE);
                    }
                    else if(numberImage == 2){
                        openGallery(GALLERY_REQUEST_CODE_COVER);
                    }
                }
                else if(i == 1){
                    if(numberImage == 1){
                        tomarFoto(PHOTO_REQUEST_CODE_PROFILE);
                    }
                    else if(numberImage == 2){
                        tomarFoto(PHOTO_REQUEST_CODE_COVER);
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
                Uri photoUri = FileProvider.getUriForFile(EditPerfilActivity.this,"joseoliva.com.proyectodam",photoFile);
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
        if(requestCode == PHOTO_REQUEST_CODE_PROFILE){
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        else if(requestCode == PHOTO_REQUEST_CODE_COVER){
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }
        return photoFile;
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
        if(requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            //si el usuario ha hecho lo que esperamos y obtenemos una imagen...
            try{
                mPhotoFile = null;
                mImageFile = FileUtil.from(this,data.getData()); //aqui obtenemos la URI convertida a archivo
                mcircleimagperfil.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));//colocamos en el imageview la imagen seleccionada
            }catch(Exception e){
                Log.d("ERROR", "no has cargado ninguna imagen" + e.getMessage());
                Toast.makeText(this,"se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            //si el usuario ha hecho lo que esperamos y obtenemos una imagen...
            try{
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this,data.getData()); //aqui obtenemos la URI convertida a archivo
                mIvportada.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));//colocamos en el imageview la imagen seleccionada
            }catch(Exception e){
                Log.d("ERROR", "no has cargado ninguna imagen" + e.getMessage());
                Toast.makeText(this,"se produjo un error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**SELECCION DE FOTOGRAFIA
         */
        if(requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(EditPerfilActivity.this).load(mPhotoPath).into(mcircleimagperfil);
        }
        /**SELECCION DE FOTOGRAFIA
         */
        if(requestCode == PHOTO_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(EditPerfilActivity.this).load(mPhotoPath2).into(mIvportada);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,EditPerfilActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,EditPerfilActivity.this);
    }
}