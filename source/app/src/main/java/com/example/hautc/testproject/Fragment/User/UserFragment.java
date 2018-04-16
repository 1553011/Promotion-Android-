package com.example.hautc.testproject.Fragment.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hautc.testproject.Activity.MainActivity;
import com.example.hautc.testproject.Activity.UserPromo;
import com.example.hautc.testproject.Activity.V2PromoDisplayActivity;
import com.example.hautc.testproject.Adpter.UserPromoAdapter;
import com.example.hautc.testproject.EventCallback.onLocationReceive;
import com.example.hautc.testproject.Fragment.ControlFragment.PromoInfo;
import com.example.hautc.testproject.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;


public class UserFragment extends Fragment implements onLocationReceive {

    //load info user
    DatabaseReference user;
    DatabaseReference dataInfo;
    ArrayList<UserPromo> promos;
    ListView listView;
    UserPromoAdapter userPromoAdapter;
    // user mail
    String mail;
    ArrayList<PromoInfo>  info;
    MainActivity activity;

    LatLng position;
    public UserFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_user, container, false);
        init(v);
        return v;
    }

    void init(View v)
    {
        position = new LatLng(0,0);
        activity.onReceiveLocationRequestFromFragment("HISTORY");
        //Anhs xa
        listView=v.findViewById(R.id.list_promo);

        promos=new ArrayList<>();
        info=new ArrayList<>();
        loadInfo();
        mail= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.',',');
        user= FirebaseDatabase.getInstance().getReference().child("/user/").child(mail);
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    promos.add(node.getValue(UserPromo.class));
                }
                if(promos.isEmpty())
                    Toast.makeText(getContext(), "You doesnt have any discount", Toast.LENGTH_LONG).show();
                userPromoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        userPromoAdapter=new UserPromoAdapter(getContext(),R.layout.user_promo_item,promos);
        listView.setAdapter(userPromoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, V2PromoDisplayActivity.class);
                for(PromoInfo q:info)
                    if(promos.get(i).getIdPromo().equals(q.getPromoId()))
                        intent.putExtra("data", q);
                intent.putExtra("key",  promos.get(i).getIdPromo());
                SimpleLocation simpleLocation = new SimpleLocation(activity, false, false);
                intent.putExtra("lat", position.latitude);
                intent.putExtra("lng", position.longitude);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onReceiveLocationFromActivity(LatLng currentLocation, int mode, String data) {
        position = currentLocation;
    }

    void loadInfo()
    {
        dataInfo=FirebaseDatabase.getInstance().getReference().child("promo_list");

        dataInfo.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                info.add(dataSnapshot.getValue(PromoInfo.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
