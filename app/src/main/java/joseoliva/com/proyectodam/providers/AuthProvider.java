package joseoliva.com.proyectodam.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthProvider {

    private FirebaseAuth mAuth;

    public AuthProvider(){

        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> register(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password){

        return mAuth.signInWithEmailAndPassword(email,password);
    }

    public String getUid() {

        if(mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser().getUid();
        }else{
            return null;
        }
    }

    //metodo para saber si el usuario ya se ha logeado, si ya se habia logeado no tiene que hacerlo otra vez
    public FirebaseUser getUserSesion() {

        if(mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser();
        }else{
            return null;
        }
    }

    //metodo para cerrar una sesion. Lo llamaremos desde el menu cerrar sesion
    public void logout(){
        if(mAuth != null) {
            mAuth.signOut();
        }
    }
}
