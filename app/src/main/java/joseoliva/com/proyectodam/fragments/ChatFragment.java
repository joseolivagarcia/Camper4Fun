package joseoliva.com.proyectodam.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.adapters.ChatsAdapter;
import joseoliva.com.proyectodam.models.Chat;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.ChatsProvider;


public class ChatFragment extends Fragment {

    ChatsAdapter mChatAdapter;
    RecyclerView mRecyclerView;
    View mView;

    ChatsProvider mChatsProvider;
    AuthProvider mAuthProvider;

    Toolbar mToolbar;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerView = mView.findViewById(R.id.recyclerViewChats);
        mToolbar = mView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chats");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mChatsProvider = new ChatsProvider();
        mAuthProvider = new AuthProvider();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatsProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query,Chat.class)
                .build();
        mChatAdapter = new ChatsAdapter(options,getContext());
        mRecyclerView.setAdapter(mChatAdapter);
        mChatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChatAdapter.getmListener() != null){
            mChatAdapter.getmListener().remove();
        }
        if(mChatAdapter.getmListenerLastMessage() != null){
            mChatAdapter.getmListenerLastMessage().remove();
        }
    }
}