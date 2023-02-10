package joseoliva.com.proyectodam.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.adapters.MessageAdapter;

import joseoliva.com.proyectodam.models.Chat;
import joseoliva.com.proyectodam.models.FCMBody;
import joseoliva.com.proyectodam.models.FCMResponse;
import joseoliva.com.proyectodam.models.Message;

import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.ChatsProvider;
import joseoliva.com.proyectodam.providers.MessageProvider;
import joseoliva.com.proyectodam.providers.NotificationProvider;
import joseoliva.com.proyectodam.providers.TokenProvider;
import joseoliva.com.proyectodam.providers.UserProvider;
import joseoliva.com.proyectodam.utils.RelativeTime;
import joseoliva.com.proyectodam.utils.ViewedMessageHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    long mIdNotificationChat;

    ChatsProvider mChatProvider;
    MessageProvider mMessageProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    EditText mEditTextMessage;
    ImageView mIVSendMessage;

    CircleImageView mCircleImage;
    TextView mTVUsername;
    TextView mTVRelativeTime;
    ImageView mIVFlechaAtras;
    RecyclerView mRecyclerViewMensajes;

    MessageAdapter mAdapter;

    LinearLayoutManager mLinearLayoutManager;

    ListenerRegistration mListener;

    String mMyUsername;
    String mUsernameChat;
    String mImageReceiver = "";
    String mImageSender = "";

    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mChatProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mEditTextMessage = findViewById(R.id.etmessage);
        mIVSendMessage = findViewById(R.id.ivsendmessage);
        mRecyclerViewMensajes = findViewById(R.id.recyclerviewMensajes);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true); //para que veamos el la pantalla desde el final (ultimo mensaje)
        mRecyclerViewMensajes.setLayoutManager(mLinearLayoutManager);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        showCustomToolbar(R.layout.custom_chat_toolbar);
        getMyInfoUser();

        mIVSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        checkIsChatExist();
    }

    @Override
    public void onStart() {

        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
        ViewedMessageHelper.updateOnline(true, ChatActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
        mAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }

    private void getMessageChat() {
        Query query = mMessageProvider.getMessagesByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();
        mAdapter = new MessageAdapter(options, ChatActivity.this);
        mRecyclerViewMensajes.setAdapter(mAdapter);
        mAdapter.startListening();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateVisto();
                int numeromensajes = mAdapter.getItemCount();
                int lastmessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastmessagePosition == -1 || (positionStart >= (numeromensajes - 1) && lastmessagePosition == (positionStart - 1))) {
                    mRecyclerViewMensajes.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void SendMessage() {
        String textmessage = mEditTextMessage.getText().toString();
        if (!textmessage.isEmpty()) {
            final Message message = new Message();
            if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            } else {
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setVisto(false);
            message.setIdChat(mExtraIdChat);
            message.setMessage(textmessage);

            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mEditTextMessage.setText("");
                        mAdapter.notifyDataSetChanged();
                        getToken(message);
                    } else {
                        Toast.makeText(ChatActivity.this, "el mensaje no se ha creado bien", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("");
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionbar.setCustomView(mActionBarView);

        mCircleImage = mActionBarView.findViewById(R.id.circleimageprofile);
        mTVUsername = mActionBarView.findViewById(R.id.tvusernamechat);
        mTVRelativeTime = mActionBarView.findViewById(R.id.tvrelativeTimeCHat);
        mIVFlechaAtras = mActionBarView.findViewById(R.id.imageflechaatras);

        mIVFlechaAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserInfo();

    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idUserInfo = mExtraIdUser2;
        } else {
            idUserInfo = mExtraIdUser1;
        }
        mListener = mUserProvider.getUserRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        mUsernameChat = documentSnapshot.getString("username");
                        mTVUsername.setText(mUsernameChat);
                    }
                }
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("online")) {
                        boolean online = documentSnapshot.getBoolean("online");
                        if (online) {
                            mTVRelativeTime.setText("En linea");
                        } else if (documentSnapshot.contains("lastConnect")) {
                            long lastconnect = documentSnapshot.getLong("lastConnect");
                            String relativetime = RelativeTime.getTimeAgo(lastconnect, ChatActivity.this);
                            mTVRelativeTime.setText(relativetime);
                        }
                    }
                }
                if (documentSnapshot.contains("imageperfil")) {
                    mImageReceiver = documentSnapshot.getString("imageperfil");
                    if (mImageReceiver != null) {
                        if (!mImageReceiver.equals("")) {
                            Picasso.with(ChatActivity.this).load(mImageReceiver).into(mCircleImage);
                        }
                    }

                }
            }
        });
    }


    private void checkIsChatExist() {
        mChatProvider.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                } else {
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mIdNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                    getMessageChat();
                    updateVisto();
                }
            }
        });
    }

    private void updateVisto() {
        String idSender = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        } else {
            idSender = mExtraIdUser1;
        }
        mMessageProvider.getMessagesByChatAndSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    mMessageProvider.updateVisto(document.getId(), true);
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWritting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);
        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setIdNotification(n);
        mIdNotificationChat = n;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatProvider.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();
    }

    private void getToken(final Message message) {
        String idUser = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idUser = mExtraIdUser2;
        } else {
            idUser = mExtraIdUser1;
        }
        mTokenProvider.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        getLastThreeMessages(message, token);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "El token de usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLastThreeMessages(Message message, final String token) {
        mMessageProvider.getLastThreeMessagesByChatAndSender(mExtraIdChat, mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                ArrayList<Message> messagesArrayList = new ArrayList<>();

                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    if (d.exists()) {
                        Message message = d.toObject(Message.class);
                        messagesArrayList.add(message);
                    }
                }
                if (messagesArrayList.size() == 0) {
                    messagesArrayList.add(message);
                }

                Collections.reverse(messagesArrayList);

                Gson gson = new Gson();
                String messages = gson.toJson(messagesArrayList);

                sendNotification(token, messages, message);

            }
        });
    }

    private void sendNotification(final String token, String messages, Message message) {

        final Map<String, String> data = new HashMap<>();
        data.put("titulo", "NUEVO MENSAJE");
        data.put("cuerpo", message.getMessage());
        data.put("idNotification", String.valueOf(mIdNotificationChat));
        data.put("messages", messages);
        data.put("usernameSender", mMyUsername);
        data.put("usernameReceiver", mUsernameChat);
        data.put("idSender", message.getIdSender());
        data.put("idReceiver", message.getIdReceiver());
        data.put("idChat", message.getIdChat());

        if(mImageSender.equals("")){
            mImageSender = "IMAGEN_NO_VALIDA";
        }
        if(mImageReceiver.equals("")){
            mImageReceiver = "IMAGEN_NO_VALIDA";
        }

        data.put("imageSender", mImageSender);
        data.put("imageReceiver", mImageReceiver);

        String idSender = "";
        if (mAuthProvider.getUid().equals(mExtraIdUser1)) {
            idSender = mExtraIdUser2;
        } else {
            idSender = mExtraIdUser1;
        }
        mMessageProvider.getLastMessageSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String lastMessage = "";
                if (size > 0) {
                    lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    data.put("lastMessage", lastMessage);
                }
                FCMBody body = new FCMBody(token, "high", "4500s", data);
                mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() == 1) {

                            } else {
                                Toast.makeText(ChatActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void getMyInfoUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        mMyUsername = documentSnapshot.getString("username");
                    }
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("imageperfil")) {
                            mImageSender = documentSnapshot.getString("imageperfil");
                        }
                    }
                }
            }
        });
    }
}