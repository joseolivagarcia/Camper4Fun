package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.adapters.PostsAdapter;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.PostProvider;
import joseoliva.com.proyectodam.utils.ViewedMessageHelper;

public class FiltersActivity extends AppCompatActivity {

    String mExtraRegion;

    Toolbar mToolbar;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;

    TextView mTextViewNumeroFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.mRecyclerViewFilters);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FiltersActivity.this);
        //mRecyclerView.setLayoutManager(linearLayoutManager);
        /*
        * Si quisiera mostrar los resutados filtrados en columnas de a 2, tendria que eliminar el LinearLayoutManager
        * de arriba y al mRecyclerView pasarle un new GridLayoutManager que recibe el contexto y el numero de columnas
        * */
        mRecyclerView.setLayoutManager(new GridLayoutManager(FiltersActivity.this,2));


        mTextViewNumeroFiltros = findViewById(R.id.tvnumerosfilter);

        mExtraRegion = getIntent().getStringExtra("region");
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByRegionAndTimeStamp(mExtraRegion);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mPostsAdapter = new PostsAdapter(options,FiltersActivity.this, mTextViewNumeroFiltros);
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
        ViewedMessageHelper.updateOnline(true,FiltersActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,FiltersActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}