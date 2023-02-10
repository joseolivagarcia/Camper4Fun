package joseoliva.com.proyectodam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import joseoliva.com.proyectodam.models.User;

public class UserProvider {

    private CollectionReference mCollection;

    public UserProvider(){

        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String id){

        return mCollection.document(id).get();
    }

    public DocumentReference getUserRealTime(String id){

        return mCollection.document(id);
    }

    public Task<Void> create(User user){

        return mCollection.document(user.getId()).set(user);
    }

    public Task<Void> update(User user){
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("model_dron", user.getModel_dron());
        map.put("timestamp", new Date().getTime());
        map.put("imageperfil", user.getImageperfil());
        map.put("imageportada",user.getImageportada());
        return mCollection.document(user.getId()).update(map);
    }

    public Task<Void> updateOnline(String idUser, boolean estado){
        Map<String, Object> map = new HashMap<>();
        map.put("online", estado);
        map.put("lastConnect", new Date().getTime());

        return mCollection.document(idUser).update(map);
    }
}
