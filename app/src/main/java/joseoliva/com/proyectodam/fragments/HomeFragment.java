package joseoliva.com.proyectodam.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.FiltersActivity;
import joseoliva.com.proyectodam.activities.MainActivity;
import joseoliva.com.proyectodam.activities.PostActivity;
import joseoliva.com.proyectodam.adapters.PostsAdapter;
import joseoliva.com.proyectodam.models.Post;
import joseoliva.com.proyectodam.providers.AuthProvider;
import joseoliva.com.proyectodam.providers.PostProvider;


public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    View mView;
    FloatingActionButton mFab;
    MaterialSearchBar materialSearchBar;
    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostsAdapter;
    PostsAdapter mPostAdapterSearch;


    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mFab = mView.findViewById(R.id.floatingbutton);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = mView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        materialSearchBar = mView.findViewById(R.id.searchBar);

        setHasOptionsMenu(true);
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        materialSearchBar.setOnSearchActionListener(this);
        materialSearchBar.inflateMenu(R.menu.main_menu);
        materialSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()== R.id.itemLogout){
                    logout();
                }
                return true;
            }
        });

        return mView;
    }

    private void getAllPost(){

        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mPostsAdapter = new PostsAdapter(options,getContext());
        mPostsAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostsAdapter);
        mPostsAdapter.startListening();
    }

    private void searchByLugar(String lugar){
        Query query = mPostProvider.getPostByLugar(lugar);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query,Post.class)
                .build();
        mPostAdapterSearch = new PostsAdapter(options, getContext());
        mPostAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mPostAdapterSearch);
        mPostAdapterSearch.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllPost();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostsAdapter.stopListening();
        if(mPostAdapterSearch !=null){
            mPostAdapterSearch.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPostsAdapter.getmListener() != null){
            mPostsAdapter.getmListener().remove();
        }
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //para no poder volver atras
        startActivity(intent);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled){
            //Toast.makeText(getContext(),"estas buscado", Toast.LENGTH_SHORT).show();
            getAllPost();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        //cuando el usuario presiona el boton de busqueda. Retorna el texto escrito
        searchByLugar(String.valueOf(text));
        //Toast.makeText(getContext(),"Has buscado: " + text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}