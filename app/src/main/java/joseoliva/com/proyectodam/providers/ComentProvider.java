package joseoliva.com.proyectodam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import joseoliva.com.proyectodam.models.Comentario;

public class ComentProvider {

    CollectionReference mCollection;

    public ComentProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Comentarios");
    }

    public Task<Void> create(Comentario comentario){
        return mCollection.document().set(comentario);
    }
    public Query getComentsByPost(String idPost){

        return mCollection.whereEqualTo("idPost",idPost).orderBy("timestamp",Query.Direction.DESCENDING);
    }
}
