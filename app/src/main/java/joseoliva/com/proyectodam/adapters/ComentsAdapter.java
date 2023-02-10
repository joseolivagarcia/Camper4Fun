package joseoliva.com.proyectodam.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.PostDetallesActivity;
import joseoliva.com.proyectodam.models.Comentario;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.UserProvider;

public class ComentsAdapter extends FirestoreRecyclerAdapter<Comentario, ComentsAdapter.ViewHolder> {

    Context contexto;
    UserProvider mUserProvider;

    public ComentsAdapter(FirestoreRecyclerOptions<Comentario> options, Context contexto){
        super(options);
        this.contexto = contexto;
        mUserProvider = new UserProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comentario comentario) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String comentId = document.getId();
        String iduser = document.getString("idUser");

        holder.textViewComentario.setText(comentario.getComentario());
        getUserInfo(iduser, holder);
    }

    private void getUserInfo(String idUser, final ViewHolder holder){
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUser.setText(username);
                    }
                    if(documentSnapshot.contains("imageperfil")){
                        String imageperfil = documentSnapshot.getString("imageperfil");
                        if(imageperfil != null){
                            if(!imageperfil.isEmpty()){
                                Picasso.with(contexto).load(imageperfil).into(holder.circleimageViewComentario);
                            }
                        }
                    }
                }
            }
        });
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comentario,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            //aqui instanciamos cada elemento de nuestro card(imagen, texto y texto)
            TextView textViewUser;
            TextView textViewComentario;
            CircleImageView circleimageViewComentario;
            View viewHolder; //esta var contiene toda la info de nuestra tarjeta

            public ViewHolder(View view){
                super(view);

                textViewUser = view.findViewById(R.id.usuariocomentario);
                textViewComentario = view.findViewById(R.id.comentario);
                circleimageViewComentario = view.findViewById(R.id.circleimagecomentario);
                viewHolder = view;
            }
        }
}
