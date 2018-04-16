package com.example.hautc.testproject.Adpter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hautc.testproject.Fragment.User.Comment;
import com.example.hautc.testproject.GlideApp;
import com.example.hautc.testproject.R;

import java.util.ArrayList;

/**
 * Created by gakon on 12/23/2017.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    Context context;
    int resoure;
    ArrayList<Comment> objects;

    public CommentAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Comment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resoure = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resoure, null);
        TextView name,com;
        ImageView imageView;
        //Ánh xạ
        name=convertView.findViewById(R.id.mail);
        com=convertView.findViewById(R.id.content);
        imageView=convertView.findViewById(R.id.img_avatar);
        // Set text
        name.setText(objects.get(position).getName());
        com.setText(objects.get(position).getContent());
        if(!objects.get(position).getAvatar().equals("n/a"))
             GlideApp.with(getContext()).load(objects.get(position).getAvatar()).circleCrop().into(imageView);
        return convertView;
    }



}
