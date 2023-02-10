package joseoliva.com.proyectodam.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.activities.FiltersActivity;


public class FilterFragment extends Fragment {

    View mView;
    CardView mCardViewEspa;
    CardView mCardViewPortu;
    CardView mCardViewFrancia;
    CardView mCardViewEuro;

    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filter, container, false);
        mCardViewEspa = mView.findViewById(R.id.filterespana);
        mCardViewPortu = mView.findViewById(R.id.filterportugal);
        mCardViewFrancia = mView.findViewById(R.id.filterfrancia);
        mCardViewEuro = mView.findViewById(R.id.filtereuropa);

        mCardViewEspa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFilterActivity("ESPAÑA");
            }
        });
        mCardViewPortu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFilterActivity("PORTUGAL");
            }
        });
        mCardViewFrancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFilterActivity("FRANCIA");
            }
        });
        mCardViewEuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFilterActivity("EUROPA");
            }
        });
        return mView;
    }

    private void gotoFilterActivity(String region){

        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("region", region);
        startActivity(intent);
    }
}