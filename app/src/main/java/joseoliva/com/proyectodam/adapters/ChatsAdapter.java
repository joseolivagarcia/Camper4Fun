package joseoliva.com.proyectodam.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.ChatActivity;
import joseoliva.com.proyectodam.models.Chat;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.ChatsProvider;
import joseoliva.com.proyectodam.providers.MessageProvider;
import joseoliva.com.proyectodam.providers.UserProvider;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context contexto;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessageProvider mMessageProvider;
    ListenerRegistration mListener;
    ListenerRegistration mListenerLastMessage;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context contexto){
        super(options);
        this.contexto = contexto;
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if(mAuthProvider.getUid().equals(chat.getIdUser1())){
            getUserInfo(chat.getIdUser2(), holder);
        }
        else{
            getUserInfo(chat.getIdUser1(),holder);
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(chatId,chat.getIdUser1(),chat.getIdUser2());
            }
        });

        getLastMessage(chatId, holder.textViewultimoMens);

        String idSender = "";
        if(mAuthProvider.getUid().equals(chat.getIdUser1())){
            idSender = chat.getIdUser2();
        }
        else{
            idSender = chat.getIdUser1();
        }

        getMessagesNoLeidos(chatId,idSender, holder.textViewMensnoleidos, holder.flmessagenoleidos);

        holder.imagedelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(chatId);
            }
        });
    }

    private void showConfirmDelete(String chatId) {

        new AlertDialog.Builder(contexto)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar Caht")
                .setMessage("¿Seguro que quiere eliminar el chat? Se eliminará en ambos dispositivos")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteChat(chatId);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void deleteChat(String chatId) {
        mChatsProvider.delete(chatId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(contexto,"Chat eliminado correctamente",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(contexto,"No se pudo eliminar el chat",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getMessagesNoLeidos(String chatId, String idSender, TextView textViewMensnoleidos, FrameLayout flmessagenoleidos) {
        mListener = mMessageProvider.getMessagesByChatAndSender(chatId,idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    int size = value.size();
                    if (size > 0) {
                        flmessagenoleidos.setVisibility(View.VISIBLE);
                        textViewMensnoleidos.setText(String.valueOf(size));
                    } else {
                        flmessagenoleidos.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public ListenerRegistration getmListener(){
        return mListener;
    }
    public ListenerRegistration getmListenerLastMessage(){
        return mListenerLastMessage;
    }

    private void getLastMessage(String chatId, TextView textViewultimoMens) {
        mListenerLastMessage = mMessageProvider.getLastMessage(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    int size = value.size();
                    if(size > 0){
                        String ultimomensaje = value.getDocuments().get(0).getString("message");
                        textViewultimoMens.setText(ultimomensaje);
                    }
                }
                }
            });
    }

    private void goToChatActivity(String chatId, String idUser1, String idUser2) {

        Intent intent = new Intent(contexto, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        contexto.startActivity(intent);
    }

    private void getUserInfo(String idUser, final ViewHolder holder){
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewChatUser.setText(username);
                    }
                    if(documentSnapshot.contains("imageperfil")){
                        String imageperfil = documentSnapshot.getString("imageperfil");
                        if(imageperfil != null){
                            if(!imageperfil.isEmpty()){
                                Picasso.with(contexto).load(imageperfil).into(holder.circleimageViewChat);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            //aqui instanciamos cada elemento de nuestro card(imagen, texto y texto)
            TextView textViewChatUser;
            TextView textViewultimoMens;
            TextView textViewMensnoleidos;
            FrameLayout flmessagenoleidos;
            CircleImageView circleimageViewChat;
            ImageView imagedelete;
            View viewHolder; //esta var contiene toda la info de nuestra tarjeta

            public ViewHolder(View view){
                super(view);

                textViewChatUser = view.findViewById(R.id.tvusernamechat);
                textViewultimoMens = view.findViewById(R.id.tvultimomens);
                textViewMensnoleidos = view.findViewById(R.id.tvmensajesnoleidos);
                flmessagenoleidos = view.findViewById(R.id.FLMessagesNoLeidos);
                circleimageViewChat = view.findViewById(R.id.circlemyimagechat);
                imagedelete = view.findViewById(R.id.imageborrarchat);
                viewHolder = view;
            }
        }
}
