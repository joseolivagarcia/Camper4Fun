package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.fragments.ChatFragment;
import joseoliva.com.proyectodam.fragments.DestinosFragment;
import joseoliva.com.proyectodam.fragments.FilterFragment;
import joseoliva.com.proyectodam.fragments.HomeFragment;
import joseoliva.com.proyectodam.fragments.ProfileFragment;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.TokenProvider;
import joseoliva.com.proyectodam.providers.UserProvider;
import joseoliva.com.proyectodam.utils.ViewedMessageHelper;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        openFragment(new HomeFragment()); //ponemos este fragment por defecto
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,HomeActivity.this);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_home:
                            openFragment(new HomeFragment());
                            return true;
                        case R.id.item_chats:
                            openFragment(new ChatFragment());
                            return true;
                        case R.id.item_profile:
                            openFragment(new ProfileFragment());
                            return true;
                        case R.id.item_filtros:
                            openFragment(new FilterFragment());
                            return true;
                        case R.id.item_location:
                            openFragment(new DestinosFragment());
                            return true;
                    }
                    return true; //al ponerlo a true me indicara el boton presionado bajo su icono
                }
            };

    private void createToken(){
        mTokenProvider.crearToken(mAuthProvider.getUid());
    }
}