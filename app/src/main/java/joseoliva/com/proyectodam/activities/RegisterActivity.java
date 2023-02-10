package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.models.User;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.UserProvider;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView circleIamageViewBack;
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPass;
    Button mBtnRegister;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTextInputUsername = (TextInputEditText)findViewById(R.id.tvusername);
        mTextInputEmail = (TextInputEditText)findViewById(R.id.tvemail);
        mTextInputPassword = (TextInputEditText)findViewById(R.id.tvpass);
        mTextInputConfirmPass = (TextInputEditText)findViewById(R.id.tvconfirpass);
        mBtnRegister = (Button)findViewById(R.id.btnregister);
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();
        //inicializamos el dialog
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        circleIamageViewBack = (CircleImageView)findViewById(R.id.circleImageBack);
        circleIamageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //esto nos lleva a la pantalla anterior
            }
        });
    }

    private void Register(){
        String username = mTextInputUsername.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String confirmpass = mTextInputConfirmPass.getText().toString();

        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmpass.isEmpty()){
            if(mailvalido(email)){
                if(password.equals(confirmpass)){
                    if(password.length() >= 6){
                        createUser(username,email,password);//creo el usu de Firebase pasandole lo que escribio el usuario
                    }
                    else{
                        Toast.makeText(this,"La contraseña debe tener al menos 6 caracteres",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this,"las contraseñas no coinciden",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this,"Campos rellenos pero el mail no es valido",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"Faltan campos para continuar",Toast.LENGTH_LONG).show();
        }
    }

    private void createUser(final String username, final String email, final String password){
        mDialog.show();
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //este metodo se realiza cuando se termine de registrar el usuario en Firebase
                if (task.isSuccessful()) {
                    String id = mAuthProvider.getUid();//obtengo el id del usuario autenticado
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setTimestamp(new Date().getTime());//esto nos devueve la fecha excata de alta del usuario
                    //creo una coleccion con el mail y el pass de cada usuario que autentique
                    mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                                //para que despues de registrarnos como nuevo usuario no pueda volver atras (se cierra la app porque se han limpiado las pantallas)
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            } else {
                                Toast.makeText(RegisterActivity.this, "Usuario no almacenado en la bbdd", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario o el usuario ya existe", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //VERIFICAR QUE SEA UN MAIL VALIDO (ES DECIR QUE TENGA ARROBA)
    public boolean mailvalido(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}