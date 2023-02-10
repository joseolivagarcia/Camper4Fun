package joseoliva.com.proyectodam.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.PostDetallesActivity;
import joseoliva.com.proyectodam.models.Like;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.LikeProvider;
import joseoliva.com.proyectodam.providers.PostProvider;
import joseoliva.com.proyectodam.providers.UserProvider;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context contexto;
    UserProvider mUserProvider;
    LikeProvider mLikeProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewNumeroFiltros;
    ListenerRegistration mListener;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context contexto){
        super(options);
        this.contexto = contexto;
        mUserProvider = new UserProvider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();
    }

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context contexto, TextView textView){
        super(options);
        this.contexto = contexto;
        mUserProvider = new UserProvider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();
        mTextViewNumeroFiltros = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        if(mTextViewNumeroFiltros != null){
            int numeroFiltro = getSnapshots().size();
            mTextViewNumeroFiltros.setText(String.valueOf(numeroFiltro));
        }

        holder.textViewLugar.setText(post.getLugar());
        holder.textViewDescripcion.setText(post.getDescripcion());
        if(post.getImage1() != null){
            if(!post.getImage1().isEmpty()){
                Picasso.with(contexto).load(post.getImage1()).into(holder.imageViewPostCard);
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexto, PostDetallesActivity.class);
                intent.putExtra("id", postId);
                contexto.startActivity(intent);
            }
        });
        holder.imageViewLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeAnimation(holder.imageViewLikes,R.raw.hmm );

                Like like = new Like();
                like.setIdUser(mAuthProvider.getUid());
                like.setIdPost(postId);
                like.setTimestamp(new Date().getTime());
                like(like, holder);
            }
        });

        getUserInfo(post.getIdUser(), holder); //metodo para obtener el nombre del usuario a traves del id de usuario que tiene el post
        getNumberLikesByPost(postId,holder);
        chequearsiexistelike(postId,mAuthProvider.getUid(),holder);
    }

    private void likeAnimation(LottieAnimationView imageview, int animation){

        imageview.setAnimation(animation);
        imageview.playAnimation();
    }

    private void getNumberLikesByPost(String idPost, final ViewHolder holder){
        mListener = mLikeProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(queryDocumentSnapshots != null){
                    int numerolikes = queryDocumentSnapshots.size();
                    holder.textViewLikes.setText(String.valueOf(numerolikes) + " Likes");
                }
            }
        });
    }

    private void like(final Like like, final ViewHolder holder) {
        mLikeProvider.getLikeByPostAndUser(like.getIdPost(),mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if(numeroDocumentos > 0){
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLikes.setImageResource(R.drawable.likeoff);
                    mLikeProvider.delete(idLike);
                }
                else{
                    holder.imageViewLikes.setImageResource(R.drawable.likeon);
                    mLikeProvider.create(like);
                }
            }
        });
    }

    private void chequearsiexistelike(String idPost, String idUser, final ViewHolder holder) {
        mLikeProvider.getLikeByPostAndUser(idPost,idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if(numeroDocumentos > 0){
                    holder.imageViewLikes.setImageResource(R.drawable.likeon);
                }
                else{
                    holder.imageViewLikes.setImageResource(R.drawable.likeoff);
                }
            }
        });
    }

    private void getUserInfo(String idUser, final ViewHolder holder) {
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsuario.setText("Por: " + username);
                    }
                }
            }
        });
    }

    public ListenerRegistration getmListener(){
        return mListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            //aqui instanciamos cada elemento de nuestro card(imagen, texto y texto)
            TextView textViewLugar;
            TextView textViewDescripcion;
            TextView textViewUsuario;
            TextView textViewLikes;
            LottieAnimationView imageViewLikes;
            ImageView imageViewPostCard;
            View viewHolder; //esta var contiene toda la info de nuestra tarjeta

            public ViewHolder(View view){
                super(view);

                textViewLugar = view.findViewById(R.id.tvtitulopostcard);
                textViewDescripcion = view.findViewById(R.id.tvdescrippcionpostcard);
                textViewUsuario = view.findViewById(R.id.tvusernamepostcard);
                textViewLikes = view.findViewById(R.id.tvlikes);
                imageViewLikes = view.findViewById(R.id.imagelike);
                imageViewPostCard = view.findViewById(R.id.ivpostcard);
                viewHolder = view;
            }
        }
}
