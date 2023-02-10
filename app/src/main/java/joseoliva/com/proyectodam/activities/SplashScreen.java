package joseoliva.com.proyectodam.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import joseoliva.com.proyectodam.R;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //creo la tarea que se ejecutara en un tiempo determinado(en este caso la carga del MainActivity)
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        //creo una var Timer y le paso lo que debe ejecutar "tarea" en el tiempo que digamos en milisegundos
        Timer tiempo = new Timer();
        tiempo.schedule(tarea,3000);
    }
}