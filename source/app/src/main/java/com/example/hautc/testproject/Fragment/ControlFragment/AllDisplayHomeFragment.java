package com.example.hautc.testproject.Fragment.ControlFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hautc.testproject.Activity.MainActivity;
import com.example.hautc.testproject.Activity.V2PromoDisplayActivity;
import com.example.hautc.testproject.Adpter.CustomPromoInfoAdapter;
import com.example.hautc.testproject.EventCallback.onLocationReceive;
import com.example.hautc.testproject.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

/**
 * Created by hautc on 12/15/2017.
 */

public class AllDisplayHomeFragment extends Fragment implements onLocationReceive {
    // Listview related variables
    ListView listView;
    CustomPromoInfoAdapter customPromoInfoAdapter;
    ArrayList<String> keys;
    ArrayList<PromoInfo> promoInfos;
    SwipeRefreshLayout refresh;

    // FIrebase variable
    FirebaseListAdapter<PromoInfo> promoAdapter;
    FirebaseListOptions<PromoInfo> options;
    DatabaseReference promoData;
    String promoPath = "/promo_list/";
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    boolean isPopulated = false;

    // View
    View view;
    MainActivity activity = (MainActivity) getActivity();

    // Location variable
    private LatLng currentPos;
    final static int RADIUS = 2000;

    String finishCode = "ALL";

    // Biến dùng để xác định chế độ chạy
    final static int NEARBY_MODE = 0;
    final static int TYPE_MODE = 1;
    final static int DISTRICT_MODE = 2;
    String data;
    int currentMode = NEARBY_MODE;
    boolean isSwipeUp = false;

    public AllDisplayHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_promotion_display, container, false);

        mapView();
        //getCurrentLocation();
        initListView();

        return view;
    }

    /***
     *  Khởi tạo những gì liên quan đến listView
     */
    void initListView(){
        // Init swipeRefresh
        refresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_blue_light), getResources().getColor(android.R.color.holo_green_light));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSwipeUp = true;
                activity.onReceiveLocationRequestFromFragment(finishCode);
            }
        });
        // Init DataPreference
        promoData = FirebaseDatabase.getInstance().getReference().child(promoPath);
        // Init mảng keys : dùng để chưa key các item, promoInfos : dùng để lưu các promo
        keys = new ArrayList<>();
        promoInfos = new ArrayList<>();
        // Tạo PromoInfo Adapter
        customPromoInfoAdapter = new CustomPromoInfoAdapter(view.getContext(), R.layout.list_view_display, promoInfos);
        // Set adapter
        listView.setAdapter(customPromoInfoAdapter);
        // Request location từ activity
        activity.onReceiveLocationRequestFromFragment(finishCode);
        // tạo listener khi bấm vào item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, V2PromoDisplayActivity.class);
                intent.putExtra("data", promoInfos.get(i));
                intent.putExtra("key", keys.get(i));
                intent.putExtra("lat", currentPos.latitude);
                intent.putExtra("lng", currentPos.longitude);
                startActivity(intent);
            }
        });
    }

    /***
     * Map view cần sử dụng
     */
    void mapView(){
        listView = view.findViewById(R.id.promoList);
        refresh = view.findViewById(R.id.swiperefresh);
    }

    /***
     * Kiểm tra xem promo có nằm trong RADIUS ( 2km) không
     */
    boolean isNearby(LatLng point){
        if(SimpleLocation.calculateDistance(currentPos.latitude, currentPos.longitude, point.latitude, point.longitude) <= RADIUS)
            return true;
        return false;
    }

    /***
     * Populate listview khi có yêu cầu
     */
    void autopopulateListView(){
        // Kiểm tra việc có đang populate không để tránh trường
        // hợp mạng yếu dẫn đến ListView được populate nhiều lần
        if(isPopulated)
            return;
        isPopulated = true;
        // Xóa hết dữ liệu cữ
        refresh.setRefreshing(true);

        keys.clear();
        promoInfos.clear();
        // bắt đầu populate listview
        promoData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        promoData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PromoInfo info = dataSnapshot.getValue(PromoInfo.class);

                populatedListBaseOnMode(info, dataSnapshot.getKey());

                isPopulated = false;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int position = keys.indexOf(dataSnapshot.getKey());
                if(position > -1)
                {
                    promoInfos.set(position, dataSnapshot.getValue(PromoInfo.class));
                    customPromoInfoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int position = keys.indexOf(dataSnapshot.getKey());
                if(position > -1)
                {
                    promoInfos.remove(position);
                    keys.remove(position);
                    customPromoInfoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /***
     * gọi để cập nhật listview khi có vị trí mới
     */
    void updatePromotionList(){
        Log.i("Update", "Updating");
        customPromoInfoAdapter.clear();
        customPromoInfoAdapter.notifyDataSetChanged();
        autopopulateListView();
    }

    /***
     *  Hàm callback khi nhận được vị trí từ Activity thì update lại listview
     */
    @Override
    public void onReceiveLocationFromActivity(LatLng currentLocation, int mode, String data) {
        // Nếu location cũ thì không cần update
        if (currentPos != null
            && SimpleLocation.calculateDistance(currentPos.latitude, currentPos.longitude, currentLocation.latitude, currentLocation.longitude) < 5
            && !isSwipeUp){
            return;
        }
        isSwipeUp = false;
        // Update mode ở đây
        currentMode = mode;
        switch (currentMode){
            case TYPE_MODE:{
                this.data = data;
                break;
            }
            case DISTRICT_MODE:{
                this.data = data;
                break;
            }
        }
        // gắn currentPos = currentLocation để lấy vị trí hiện tại và update
        currentPos = currentLocation;
        updatePromotionList();
    }
//    // lấy activity chính
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        activity = (MainActivity) context;
//    }

    /***
     * Populate listview dựa trên những chế độ tương ứng
     * @param info dữ liệu lấy từ firebase
     * @param key khóa của promo đó
     */
    void populatedListBaseOnMode(PromoInfo info, String key){
        // Nếu mode là NEARBY : lấy vị trí gần nhất
        // Nếu mode là TYPE:  populate dựa trên loại promo
        // DISTRICT: populate dựa trên quận
        switch (currentMode){
            case NEARBY_MODE:{
                if (isNearby(new LatLng(info.getLat(), info.getLng()))){
                    keys.add(/*dataSnapshot.getKey()*/key);
                    promoInfos.add(info);
                    customPromoInfoAdapter.notifyDataSetChanged();
                }
                break;
            }
            case TYPE_MODE:{
                if (info.getType().compareTo(data) == 0){
                    keys.add(/*dataSnapshot.getKey()*/key);
                    promoInfos.add(info);
                    customPromoInfoAdapter.notifyDataSetChanged();
                }
                break;
            }
            case DISTRICT_MODE:{
                if (info.getDistrict().compareTo(data) == 0){
                    keys.add(/*dataSnapshot.getKey()*/key);
                    promoInfos.add(info);
                    customPromoInfoAdapter.notifyDataSetChanged();
                }
                break;
            }
            default:{
                keys.add(/*dataSnapshot.getKey()*/key);
                promoInfos.add(info);
                customPromoInfoAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}
