package joseoliva.com.proyectodam.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.EditPerfilActivity;
import joseoliva.com.proyectodam.adapters.MyPostsAdapter;
import joseoliva.com.proyectodam.adapters.PostsAdapter;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.PostProvider;
import joseoliva.com.proyectodam.providers.UserProvider;


public class ProfileFragment extends Fragment {

    CircleImageView mCirclefotoperfil;
    ImageView mfotoportadaperfil;
    TextView mnumeroposts;
    TextView mModelodronperfil;
    TextView mNombreperfil;
    TextView mcorreoperfil;
    TextView mpostExist;
    LinearLayout mLinearlayouteditperfil;
    RecyclerView mRecyclerView;
    View mview;
    UserProvider muserProvider;
    AuthProvider mauthProvider;
    PostProvider mPostProvider;

    MyPostsAdapter mAdapapter;

    ListenerRegistration mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_profile, container, false);
        mRecyclerView = mview.findViewById(R.id.recyclerViewMyPost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mLinearlayouteditperfil = mview.findViewById(R.id.linearlayouteditperfil);
        mLinearlayouteditperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEditPerfil();
            }
        });

        mCirclefotoperfil = mview.findViewById(R.id.fotoperfil);
        mfotoportadaperfil = mview.findViewById(R.id.fotoportada);
        mnumeroposts = mview.findViewById(R.id.numeroposts);
        mModelodronperfil = mview.findViewById(R.id.modelodronperfil);
        mNombreperfil = mview.findViewById(R.id.nombreperfil);
        mcorreoperfil = mview.findViewById(R.id.correoperfil);
        mpostExist = mview.findViewById(R.id.tvpostexist);
        muserProvider = new UserProvider();
        mauthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        getUser();
        getPostnumber();
        checkIfExistPost();
        return mview;
    }

    private void checkIfExistPost() {
        mListener = mPostProvider.getPostByUser(mauthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    int numeroPost = value.size();
                    if(numeroPost > 0) {
                        mpostExist.setText("Posts");
                        mpostExist.setTextColor(Color.GREEN);
                    }
                    else{
                        mpostExist.setText("No hay Posts");
                        mpostExist.setTextColor(Color.GRAY);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByUser(mauthProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mAdapapter = new MyPostsAdapter(options,getContext());
        mRecyclerView.setAdapter(mAdapapter);
        mAdapapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mListener.remove();
        }
    }

    private void gotoEditPerfil() {

        Intent intent = new Intent(getContext(), EditPerfilActivity.class);
        startActivity(intent);
    }

    private void getPostnumber(){
        mPostProvider.getPostByUser(mauthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPosts = queryDocumentSnapshots.size();
                mnumeroposts.setText(String.valueOf(numeroPosts));
            }
        });
    }

    private void getUser(){
        muserProvider.getUser(mauthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                Picasso.with(getContext()).load(imageperfil).into(mCirclefotoperfil);
                            }
                        }
                    }
                    if(documentSnapshot.contains("imageportada")){
                        String imageportada = documentSnapshot.getString("imageportada");
                        if(imageportada != null){
                            if(!imageportada.isEmpty()){
                                Picasso.with(getContext()).load(imageportada).into(mfotoportadaperfil);
                            }
                        }
                    }
                }
            }
        });
    }
}