package joseoliva.com.proyectodam.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import joseoliva.com.proyectodam.activities.ChatActivity;
import joseoliva.com.proyectodam.models.Message;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.UserProvider;
import joseoliva.com.proyectodam.utils.RelativeTime;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolder> {

    Context contexto;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    public MessageAdapter(FirestoreRecyclerOptions<Message> options, Context contexto){
        super(options);
        this.contexto = contexto;
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String messageId = document.getId();
        holder.textViewMessage.setText(message.getMessage());
        String relativetime = RelativeTime.timeFormatAMPM(message.getTimestamp(),contexto);
        holder.textVieFecha.setText(relativetime);

        if(message.getIdSender().equals(mAuthProvider.getUid())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150,0,0,0);
            holder.linearLayoutmessage.setLayoutParams(params);
            holder.linearLayoutmessage.setPadding(30,20,0,20);
            holder.linearLayoutmessage.setBackground(contexto.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.imageviewvisto.setVisibility(View.VISIBLE);
            holder.textViewMessage.setTextColor(Color.WHITE);
            holder.textVieFecha.setTextColor(Color.LTGRAY);
        }
        else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0,0,150,0);
            holder.linearLayoutmessage.setLayoutParams(params);
            holder.linearLayoutmessage.setPadding(30,20,30,20);
            holder.linearLayoutmessage.setBackground(contexto.getResources().getDrawable(R.drawable.rounded_linear_layout_gris));
            holder.imageviewvisto.setVisibility(View.GONE);
            holder.textViewMessage.setTextColor(Color.DKGRAY);
            holder.textVieFecha.setTextColor(Color.LTGRAY);
        }

        if(message.isVisto()){
            holder.imageviewvisto.setImageResource(R.drawable.visto);
        }
        else{
            holder.imageviewvisto.setImageResource(R.drawable.novisto);
        }
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            //aqui instanciamos cada elemento de nuestro card(imagen, texto y texto)
            TextView textViewMessage;
            TextView textVieFecha;
            ImageView imageviewvisto;
            LinearLayout linearLayoutmessage;
            View viewHolder; //esta var contiene toda la info de nuestra tarjeta

            public ViewHolder(View view){
                super(view);

                textViewMessage = view.findViewById(R.id.tvmessage);
                textVieFecha = view.findViewById(R.id.tvdatemessage);
                imageviewvisto = view.findViewById(R.id.ivvistomessage);
                linearLayoutmessage = view.findViewById(R.id.linearlayoutmessage);
                viewHolder = view;
            }
        }
}
