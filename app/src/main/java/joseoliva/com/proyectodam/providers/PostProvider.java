package joseoliva.com.proyectodam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import joseoliva.com.proyectodam.models.Post;

public class PostProvider {

    CollectionReference mCollection;

    public PostProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Posts"); //creo la coleccion posts en firebase
    }
     public Task<Void> save(Post p){

        return mCollection.document().set(p);
     }

     //metodo consulta a la base de datos para obtener todos los post ordenados
     public Query getAll(){

        return mCollection.orderBy("timestamp",Query.Direction.DESCENDING);
    }
    public Query getPostByRegionAndTimeStamp(String region){

        return mCollection.whereEqualTo("region",region).orderBy("timestamp",Query.Direction.DESCENDING);
    }
    public Query getPostByLugar(String lugar){
        //esto busca cualquier lugar que incluya el texto buscado
        return mCollection.orderBy("lugar").startAt(lugar).endAt(lugar + '\uf8ff');
        //return mCollection.whereEqualTo("lugar",lugar);

    }

     public Query getPostByUser(String id){

        return mCollection.whereEqualTo("idUser",id);
    }

    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }

    public Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }
}
