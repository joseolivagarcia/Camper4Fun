package joseoliva.com.proyectodam.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.PostDetallesActivity;
import joseoliva.com.proyectodam.models.Like;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.LikeProvider;
import joseoliva.com.proyectodam.providers.PostProvider;
import joseoliva.com.proyectodam.providers.UserProvider;
import joseoliva.com.proyectodam.utils.RelativeTime;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post, MyPostsAdapter.ViewHolder> {

    Context contexto;
    UserProvider mUserProvider;
    LikeProvider mLikeProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    public MyPostsAdapter(FirestoreRecyclerOptions<Post> options, Context contexto){
        super(options);
        this.contexto = contexto;
        mUserProvider = new UserProvider();
        mLikeProvider = new LikeProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativetime = RelativeTime.getTimeAgo(post.getTimestamp(),contexto);
        holder.textviewrelativetimeMypost.setText(relativetime);
        holder.textviewTituloMyPost.setText(post.getLugar());
        if(post.getIdUser().equals(mAuthProvider.getUid())){
            holder.imageviewDelete.setVisibility(View.VISIBLE);
        }
        else{
            holder.imageviewDelete.setVisibility(View.GONE);
        }


        if(post.getImage1() != null){
            if(!post.getImage1().isEmpty()){
                Picasso.with(contexto).load(post.getImage1()).into(holder.circleimagemypost);
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

        holder.imageviewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(postId);
            }
        });

    }

    private void showConfirmDelete(String postId) {

        new AlertDialog.Builder(contexto)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar Post")
                .setMessage("¿Seguro que quiere eliminar el post?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void deletePost(String postId) {
        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(contexto,"Post eliminado correctamente",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(contexto,"No se pudo eliminar el post",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            //aqui instanciamos cada elemento de nuestro card(imagen, texto y texto)
            TextView textviewTituloMyPost;
            TextView textviewrelativetimeMypost;
            CircleImageView circleimagemypost;
            ImageView imageviewDelete;
            View viewHolder; //esta var contiene toda la info de nuestra tarjeta

            public ViewHolder(View view){
                super(view);

                textviewTituloMyPost = view.findViewById(R.id.tvtitulomypost);
                textviewrelativetimeMypost = view.findViewById(R.id.tvrelativetimemypost);
                circleimagemypost = view.findViewById(R.id.circlemyimagemypost);
                imageviewDelete = view.findViewById(R.id.imagemypostcancel);
                viewHolder = view;
            }
        }
}
