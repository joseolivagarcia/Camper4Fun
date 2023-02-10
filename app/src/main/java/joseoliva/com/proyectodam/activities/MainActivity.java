package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {
    TextView txtRegistroAqui;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;
    AuthProvider mAuthProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTextInputEmail = (TextInputEditText)findViewById(R.id.mail);
        mTextInputPassword = (TextInputEditText)findViewById(R.id.pass);
        mButtonLogin = (Button)findViewById(R.id.btnlogin);
        txtRegistroAqui = (TextView)findViewById(R.id.txtRegistro);
        mAuthProvider = new AuthProvider();
        //inicializamos el dialog
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTextInputEmail.getText().toString().equals("") || mTextInputPassword.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this,"Hay que rellenar todos los campos",Toast.LENGTH_SHORT).show();
                }else {
                    login(); //llamo al metodo login
                }
            }
        });

        txtRegistroAqui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    //metodo para validar si la sesion del usuario existe y si existe no tenemos que logearnos de nuevo
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSesion() != null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//para limpiar el historial
            startActivity(intent);

        }
    }

    private void login(){
        String email = mTextInputEmail.getText().toString(); //recupero el mail que escribe el usuario
        String password = mTextInputPassword.getText().toString(); //recupero el password que escriba el usuario
        mDialog.show();
        mAuthProvider.login(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.d("Campo", "email: " + email); //esto es como un sysout para imprimir en consala
        Log.d("Campo", "password: " + password);
    }
}