package com.example.hautc.testproject.Adpter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hautc.testproject.Fragment.ControlFragment.PromoInfo;
import com.example.hautc.testproject.GlideApp;
import com.example.hautc.testproject.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by hautc on 11/24/2017.
 */

public class CustomPromoInfoAdapter extends ArrayAdapter<PromoInfo> {

    Context context;
    int resoure;
    ArrayList<PromoInfo> objects;

    public CustomPromoInfoAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<PromoInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resoure = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context); // lay duoc activity
        convertView = layoutInflater.inflate(resoure,null); // gan activity vao để có thể xài được các id

        final RelativeLayout layout = convertView.findViewById(R.id.testcard);
        TextView description = convertView.findViewById(R.id.promo_descrip);
        final CardView promoCard = convertView.findViewById(R.id.card_info);
//        RatingBar rating = convertView.findViewById(R.id.ratingBar);

        description.setText(objects.get(position).getBriefdescrip());
//        rating.setRating(objects.get(position).getRating());

        String brandPath = objects.get(position).getBrand()+"/";
        String logoPath = brandPath + objects.get(position).getBrand() + ".png";

        StorageReference logo = FirebaseStorage.getInstance().getReference(logoPath);

        StorageReference promo = FirebaseStorage.getInstance().getReference(brandPath + objects.get(position).getPromoimage());


        GlideApp.with(context).load(promo)
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(10, 0 , RoundedCornersTransformation.CornerType.ALL)))
                .centerCrop()
                .into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    promoCard.setBackground(resource);
            }
        });

        return convertView;
    }
}
