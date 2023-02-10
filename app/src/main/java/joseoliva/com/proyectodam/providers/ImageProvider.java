package joseoliva.com.proyectodam.providers;

//esta clase se encarga de almacenar las imagenes en Firebase

import android.content.Context;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

import joseoliva.com.proyectodam.utils.CompressorBitmapImage;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider(){
        mStorage = FirebaseStorage.getInstance().getReference();//con esta var hacemos referencia al modulo de firebasestorage
    }

    public UploadTask save(Context context, File file){
        //este metodo permite almacenar la imagen
        //los numeros finales son el tamaño que queramos dar a la imagen
        byte[] imageByte = CompressorBitmapImage.getImage(context,file.getPath(),500,500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child(new Date() + ".jpg"); //damos nombre a la imagen
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public StorageReference getStorage(){
        return mStorage;
    }
}
