package com.example.hautc.testproject.Adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hautc.testproject.Activity.UserPromo;
import com.example.hautc.testproject.GlideApp;
import com.example.hautc.testproject.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by gakon on 12/9/2017.
 */
// Save the information of user's history
public class UserPromoAdapter extends ArrayAdapter<UserPromo> {
    Context context;
    int resource;
    ArrayList<UserPromo> objects;
    public UserPromoAdapter(@NonNull Context context, int resource, @NonNull ArrayList<UserPromo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, null);
        TextView name,address,code,date;
        ImageView imageView;
        //Ánh xạ
        name=convertView.findViewById(R.id.promoName);
        address=convertView.findViewById(R.id.address);
        code=convertView.findViewById(R.id.code);
        date=convertView.findViewById(R.id.date);
        imageView=convertView.findViewById(R.id.img_view);
        // Set text
        name.setText(objects.get(position).getIdPromo());
        address.setText(objects.get(position).getAddress()+","+objects.get(position).getDistrict());
        code.setText(objects.get(position).getCode());
        date.setText(objects.get(position).getDate());

        String brandPath = objects.get(position).getBrand()+"/";
        String logoPath = brandPath + objects.get(position).getBrand() + ".png";
        StorageReference logo = FirebaseStorage.getInstance().getReference(logoPath);
        GlideApp.with(getContext()).load(logo).circleCrop().into(imageView);
        return convertView;
    }
}
