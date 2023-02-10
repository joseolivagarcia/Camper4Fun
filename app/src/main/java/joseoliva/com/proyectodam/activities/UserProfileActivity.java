package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.adapters.MyPostsAdapter;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.PostProvider;
import joseoliva.com.proyectodam.providers.UserProvider;
import joseoliva.com.proyectodam.utils.ViewedMessageHelper;

public class UserProfileActivity extends AppCompatActivity {

    CircleImageView mCirclefotoperfil;
    ImageView mfotoportadaperfil;
    TextView mnumeroposts;
    TextView mModelodronperfil;
    TextView mNombreperfil;
    TextView mcorreoperfil;
    TextView mpostExist;
    LinearLayout mLinearlayouteditperfil;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;
    FloatingActionButton mFabchat;

    UserProvider muserProvider;
    AuthProvider mauthProvider;
    PostProvider mPostProvider;

    MyPostsAdapter mAdapter;

    String mExtraIdUser;

    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mLinearlayouteditperfil = findViewById(R.id.linearlayouteditperfil);
        mCirclefotoperfil = findViewById(R.id.fotoperfil);
        mfotoportadaperfil = findViewById(R.id.fotoportada);
        mnumeroposts = findViewById(R.id.numeroposts);
        mModelodronperfil = findViewById(R.id.modelodronperfil);
        mNombreperfil = findViewById(R.id.nombreperfil);
        mcorreoperfil = findViewById(R.id.correoperfil);
        mpostExist = findViewById(R.id.mpostExist);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFabchat = findViewById(R.id.fabchat);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        muserProvider = new UserProvider();
        mauthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mExtraIdUser = getIntent().getStringExtra("idUser");

        if(mauthProvider.getUid().equals(mExtraIdUser)){
            mFabchat.setVisibility(View.GONE);
        }
        mFabchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToChatActivity();
            }
        });

        getUser();
        getPostnumber();
        checkIfExistPost();
    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this,ChatActivity.class);
        intent.putExtra("idUser1", mauthProvider.getUid());
        intent.putExtra("idUser2", mExtraIdUser);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mExtraIdUser);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mAdapter = new MyPostsAdapter(options,UserProfileActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        ViewedMessageHelper.updateOnline(true,UserProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,UserProfileActivity.this);
    }
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mListener.remove();
        }
    }

    private void checkIfExistPost() {
        mListener = mPostProvider.getPostByUser(mExtraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    int numeroPost = value.size();
                    if(numeroPost > 0) {
                        mpostExist.setText("Publicaciones");
                        mpostExist.setTextColor(Color.GREEN);
                    }
                    else{
                        mpostExist.setText("No hay Publicaciones");
                        mpostExist.setTextColor(Color.GRAY);
                    }
                }
            }
        });
    }

    private void getPostnumber(){
        mPostProvider.getPostByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPosts = queryDocumentSnapshots.size();
                mnumeroposts.setText(String.valueOf(numeroPosts));
            }
        });
    }

    private void getUser(){
        muserProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        mcorreoperfil.setText(email);
                    }
                    if(documentSnapshot.contains("username")){
                        String nombre = documentSnapshot.getString("username");
                        mNombreperfil.setText(nombre);
                    }
                    if(documentSnapshot.contains("model_dron")){
                        String modeldron = documentSnapshot.getString("model_dron");
                        mModelodronperfil.setText(modeldron);
                    }
                    if(documentSnapshot.contains("imageperfil")){
                        String imageperfil = documentSnapshot.getString("imageperfil");
                        if(imageperfil != null){
                            if(!imageperfil.isEmpty()){
                                Picasso.with(UserProfileActivity.this).load(imageperfil).into(mCirclefotoperfil);
                            }
                        }
                    }
                    if(documentSnapshot.contains("imageportada")){
                        String imageportada = documentSnapshot.getString("imageportada");
                        if(imageportada != null){
                            if(!imageportada.isEmpty()){
                                Picasso.with(UserProfileActivity.this).load(imageportada).into(mfotoportadaperfil);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}