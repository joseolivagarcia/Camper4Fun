package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.adapters.ComentsAdapter;
import joseoliva.com.proyectodam.adapters.PostsAdapter;
import joseoliva.com.proyectodam.adapters.SliderAdapter;
import joseoliva.com.proyectodam.models.Comentario;
import joseoliva.com.proyectodam.models.FCMBody;
import joseoliva.com.proyectodam.models.FCMResponse;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.models.SliderItem;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.ComentProvider;
import joseoliva.com.proyectodam.providers.LikeProvider;
import joseoliva.com.proyectodam.providers.NotificationProvider;
import joseoliva.com.proyectodam.providers.PostProvider;
import joseoliva.com.proyectodam.providers.TokenProvider;
import joseoliva.com.proyectodam.providers.UserProvider;
import joseoliva.com.proyectodam.utils.RelativeTime;
import joseoliva.com.proyectodam.utils.ViewedMessageHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetallesActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    PostProvider mPostProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    LikeProvider mLikeProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;
    ComentProvider mComentProvider;
    ComentsAdapter mComentAdapter;

    String mExtraPostId;

    TextView mTextviewLugarDetalles;
    TextView mTextViewDescripcionDetalles;
    TextView mTextViewUsuarioDetalles;
    TextView mTextViewModeloDronDetalles;
    ImageView mImageViewRegionDetalles;
    ImageView mImagemaps;
    CircleImageView mCircleIVPerfilDetalles;
    Button mButtonVerPerfilDetalles;
    FloatingActionButton mButtonComentarios;
    RecyclerView mRecyclerviewComentarios;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;
    Toolbar mToolbar;

    String mIdUser = "";

    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detalles);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSliderView = findViewById(R.id.imageSlider);

        mTextviewLugarDetalles = findViewById(R.id.tvlugardetalles);
        mTextViewDescripcionDetalles = findViewById(R.id.tvdescripciondetalles);
        mTextViewUsuarioDetalles = findViewById(R.id.idusernamedetalles);
        mTextViewModeloDronDetalles = findViewById(R.id.modelodrondetalles);
        mTextViewRelativeTime = findViewById(R.id.tvRelativeTime);
        mTextViewLikes = findViewById(R.id.tvlikes);
        mImageViewRegionDetalles = findViewById(R.id.ivregiondetalles);
        mImagemaps = findViewById(R.id.ivmaps);
        mCircleIVPerfilDetalles = findViewById(R.id.circleimageperfildetalles);
        mRecyclerviewComentarios = findViewById(R.id.recyclerViewComentarios);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetallesActivity.this);
        mRecyclerviewComentarios.setLayoutManager(linearLayoutManager);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonVerPerfilDetalles = findViewById(R.id.btnperfildetalles);
        mButtonVerPerfilDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToVerPerfil();
            }
        });

        mButtonComentarios = findViewById(R.id.fabcomentarios);
        mButtonComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComentario();
            }
        });

        mImagemaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String direccion = mTextviewLugarDetalles.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + direccion));
                startActivity(i);
                //Toast.makeText(PostDetallesActivity.this,"te llevo a " + direccion,Toast.LENGTH_SHORT).show();
            }
        });

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mComentProvider = new ComentProvider();
        mLikeProvider = new LikeProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mExtraPostId = getIntent().getStringExtra("id");

        getPost();
        getNumberLikes();
    }

    private void getNumberLikes() {
        mListener = mLikeProvider.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if(queryDocumentSnapshots != null){
                    int numeroLikes = queryDocumentSnapshots.size();
                    if (numeroLikes == 1) {
                        mTextViewLikes.setText(numeroLikes + " Like");
                    } else if (numeroLikes == 0) {
                        mTextViewLikes.setText("ningún like :-(");
                    } else {
                        mTextViewLikes.setText(numeroLikes + " Likes");
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mComentProvider.getComentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comentario> options = new FirestoreRecyclerOptions.Builder<Comentario>()
                .setQuery(query,Comentario.class)
                .build();
        mComentAdapter = new ComentsAdapter(options,PostDetallesActivity.this);
        mRecyclerviewComentarios.setAdapter(mComentAdapter);
        mComentAdapter.startListening();
        ViewedMessageHelper.updateOnline(true,PostDetallesActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,PostDetallesActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mListener.remove();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mComentAdapter.stopListening();
    }

    private void showDialogComentario() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetallesActivity.this);
        alert.setTitle("COMENTARIO");
        alert.setMessage("Ingresa tu comentario");

        final EditText editText = new EditText(PostDetallesActivity.this);
        editText.setHint("Escribe tu comentario");

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        parametros.setMargins(36,0,36,36);
        editText.setLayoutParams(parametros);
        RelativeLayout container = new RelativeLayout(PostDetallesActivity.this);
        RelativeLayout.LayoutParams relativeparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(relativeparams);
        container.addView(editText);
        alert.setView(container);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
                if(!value.isEmpty()) {
                    createComentario(value);
                }
                else{
                    Toast.makeText(PostDetallesActivity.this,"Debes ingresar un comentario",Toast.LENGTH_SHORT).show();
                }

            }
        });
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void createComentario(final String value) {
        Comentario comentario = new Comentario();
        comentario.setComentario(value);
        comentario.setIdPost(mExtraPostId);
        comentario.setIdUser(mAuthProvider.getUid());
        comentario.setTimestamp(new Date().getTime());
        mComentProvider.create(comentario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendNotification(value);
                    Toast.makeText(PostDetallesActivity.this,"Comentario creado correctamente",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PostDetallesActivity.this,"No se pudo crear el comentario",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(final String value) {
            if(mIdUser == null){
                return;
            }
            mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        if(documentSnapshot.contains("token")){
                            Map<String,String> data = new HashMap<>();
                            data.put("titulo", "NUEVO COMENTARIO");
                            data.put("cuerpo", value);
                            String token = documentSnapshot.getString("token");
                            FCMBody body = new FCMBody(token,"high","4500s",data);
                            mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                                @Override
                                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                    if(response.body() != null){
                                        if(response.body().getSuccess() == 1){
                                            //Toast.makeText(PostDetallesActivity.this,"Se envio la notificacion",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            //Toast.makeText(PostDetallesActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        //Toast.makeText(PostDetallesActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<FCMResponse> call, Throwable t) {

                                }
                            });
                        }
                    }
                    else{
                        Toast.makeText(PostDetallesActivity.this,"El token de usuario no existe",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void goToVerPerfil() {
        if(!mIdUser.equals("")){
            Intent intent = new Intent(PostDetallesActivity.this,UserProfileActivity.class);
            intent.putExtra("idUser", mIdUser);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"El id del usuario aun no ha cargado, espera",Toast.LENGTH_SHORT).show();
        }

    }

    private void instanceSlider(){

        mSliderAdapter = new SliderAdapter(PostDetallesActivity.this,mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("image1")){
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image1);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("image2")){
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image2);
                        mSliderItems.add(item);
                    }

                    if(documentSnapshot.contains("lugar")){
                        String lugar = documentSnapshot.getString("lugar");
                        mTextviewLugarDetalles.setText(lugar);
                    }
                    if(documentSnapshot.contains("descripcion")){
                        String descripcion = documentSnapshot.getString("descripcion");
                        mTextViewDescripcionDetalles.setText(descripcion);
                    }
                    if(documentSnapshot.contains("region")){
                        String region = documentSnapshot.getString("region");
                        switch (region){
                            case "ESPAÑA":
                                mImageViewRegionDetalles.setImageResource(R.drawable.espana);
                                break;
                            case "PORTUGAL":
                                mImageViewRegionDetalles.setImageResource(R.drawable.portugal);
                                break;
                            case "FRANCIA":
                                mImageViewRegionDetalles.setImageResource(R.drawable.francia);
                                break;
                            case "EUROPA":
                                mImageViewRegionDetalles.setImageResource(R.drawable.euro);
                                break;

                        }
                    }

                    if(documentSnapshot.contains("idUser")){
                        mIdUser = documentSnapshot.getString("idUser");
                        getUserInfo(mIdUser);
                    }
                    if(documentSnapshot.contains("timestamp")){
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp,PostDetallesActivity.this);
                        mTextViewRelativeTime.setText(relativeTime);
                    }

                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfo(String idUser) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsuarioDetalles.setText(username);
                    }
                    if(documentSnapshot.contains("model_dron")){
                        String modeldron = documentSnapshot.getString("model_dron");
                        mTextViewModeloDronDetalles.setText(modeldron);
                    }
                    if(documentSnapshot.contains("imageperfil")){
                        String imageperfil = documentSnapshot.getString("imageperfil");
                        Picasso.with(PostDetallesActivity.this).load(imageperfil).into(mCircleIVPerfilDetalles);
                    }
                }
            }
        });
    }
}