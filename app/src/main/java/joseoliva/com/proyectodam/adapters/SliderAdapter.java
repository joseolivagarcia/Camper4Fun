package joseoliva.com.proyectodam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import joseoliva.com.proyectodam.R;
import joseoliva.com.proyectodam.models.SliderItem;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public SliderAdapter(Context context, List<SliderItem> sliderItems) {
        this.context = context;
        mSliderItems = sliderItems;
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        SliderItem sliderItem = mSliderItems.get(position);
        if(sliderItem.getImageUrl() !=null){
            if(!sliderItem.getImageUrl().isEmpty()){
                Picasso.with(context).load(sliderItem.getImageUrl()).into(viewHolder.ivslider);
            }
        }
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView ivslider;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            ivslider = itemView.findViewById(R.id.ivslider);

            this.itemView = itemView;
        }
    }

}
