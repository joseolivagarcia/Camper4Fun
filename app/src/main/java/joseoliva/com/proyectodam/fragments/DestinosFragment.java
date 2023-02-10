package joseoliva.com.proyectodam.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.PostActivity;
import joseoliva.com.proyectodam.providers.PostProvider;


public class DestinosFragment extends Fragment implements OnMapReadyCallback {

    View mView;
    Toolbar mToolbar;
    private GoogleMap mGooglemap;
    private PostProvider mPostProvider;

    public DestinosFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_destinos, container, false);
        mToolbar = mView.findViewById(R.id.toolbar);
        mPostProvider = new PostProvider();

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Destinos");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentmap);
        mapFragment.getMapAsync(this);

        return mView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mGooglemap = googleMap;
        /*
        LatLng boadilla = new LatLng(40.4052,-3.87);
        mGooglemap.addMarker(new MarkerOptions()
                .position(boadilla)
                .title("Boadilla del monte"));
        mGooglemap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(boadilla,8));
        */
        //crearMarcador();

        mPostProvider.getAll().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments()){
                    String lugar = document.getString("lugar");
                    LatLng coordenadas = getLocationFromAddress(getContext(),lugar);
                    mGooglemap.addMarker(new MarkerOptions()
                            .position(coordenadas)
                            .title(lugar)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icopernoctamaps)));
                    mGooglemap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas,5));
                }
            }
        });
    }

    /*
    private void crearMarcador() {
        //creo el marcador del lugar para el mapa
        String direccion = "Bilbao";
        LatLng coordenadas = getLocationFromAddress(getContext(),direccion);
        mGooglemap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title(direccion));

    }
     */

    //Metodo para convertir la direccion o lugar en latitud y longitud
    public LatLng getLocationFromAddress(Context context, String Straddress){

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try{
            address = coder.getFromLocationName(Straddress,5);
            if(address == null){
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return p1;
    }
}