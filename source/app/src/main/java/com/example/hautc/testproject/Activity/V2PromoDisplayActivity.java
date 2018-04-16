package com.example.hautc.testproject.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hautc.testproject.Adpter.CommentAdapter;
import com.example.hautc.testproject.Fragment.ControlFragment.PromoInfo;
import com.example.hautc.testproject.Fragment.User.Comment;
import com.example.hautc.testproject.GlideApp;
import com.example.hautc.testproject.R;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

public class V2PromoDisplayActivity extends AppCompatActivity implements OnMapReadyCallback, RatingDialogListener,RideRequestButtonCallback{

    // Các view
    ImageView brandlogo_iv;
    TextView store_name_txt;
    RatingBar rating_point_rb;
    TextView number_of_view_txt;
    Button getcode_btn;
    TextView address_txt;
    TextView distance_txt;
    Button getmethere_btn;
    TextView detail_descrip_txt;
    Button readmore_btn;
    boolean isExpand = false;
    android.support.v7.widget.Toolbar toolbar;

    //View
    TextView txtRating,txtMark,txtComment;

    // Google map
    GoogleMap mMap;

    // Dữ liệu của promo
    PromoInfo info;
    LatLng currentPos;
    LatLng infoLatLng;

    // Dữ liệu firebase
    String key;
    DatabaseReference root;
    String mail;
    DatabaseReference userpromo;
    String promoPath = "/promo_list/";
    DatabaseReference promoData = FirebaseDatabase.getInstance().getReference().child(promoPath);
    boolean isRate;
    String comment;
    ShareDialog shareDialog;
    Bitmap bitmap;
    ImageButton share;

    // lấy dữ liệu comment
    ArrayList<Comment>PromoComment;
    CommentAdapter commentAdapter;
    DatabaseReference commentData;
    ListView listView;
    RideRequestButton requestButton;
    int mark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prototype_promo_display);


        // Lấy thông tin cảu promo đó thogn6 qua intent
        Intent intent = getIntent();
        info = (PromoInfo) intent.getSerializableExtra("data");
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);
        currentPos = new LatLng(lat, lng);
        key = intent.getStringExtra("key");
        // Map các view và khởi tạo thogn6 só đầu vào
        // Lấy các dữ liệu người dùng
        getCurrentUserHistory();
        mapView();
        initData();

        requestButton = findViewById(R.id.uber);


        RideParameters rideParams = new RideParameters.Builder()
                .setPickupLocation(currentPos.latitude, currentPos.longitude, "Current", "")
                .setDropoffLocation(info.getLat(), info.getLng(), info.getBrand(), info.getAddress()+info.getDistrict()) // Price estimate will only be provided if this is provided.
                .build();

        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("_GWKh4pSwlcsHBGM9Hx3UMvG-yZGHLUr")
                .setServerToken("YbyTp7KQwk03zrTg7UZB8Kak4AuacmLw9JeklZlL")
                .build();
        ServerTokenSession session = new ServerTokenSession(config);
        requestButton.setRideParameters(rideParams);
        requestButton.setSession(session);
        requestButton.setCallback(this);
        requestButton.loadRideInformation();


    }

    /***
     * Map các view vào đúng ID của nó và khởi tạo các biến liên quan đến firebase
     * Lấy danh sách các promo mà người dùng để lấy rồi nếu đã lấy promo này thì hiển thị len nút
     */
    void mapView() {
        // Khởi tạo loading dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading.....");
        dialog.show();
        isRate=false;
        // Map các view
        brandlogo_iv = findViewById(R.id.app_bar_image);
        store_name_txt = findViewById(R.id.store_name_v2);
        getcode_btn = findViewById(R.id.rate_here_v2);
        address_txt = findViewById(R.id.address_v2);
        distance_txt = findViewById(R.id.distance_v2);
        getmethere_btn = findViewById(R.id.show_me_the_way);
        detail_descrip_txt = findViewById(R.id.detail_v2);
        readmore_btn = findViewById(R.id.btnReadMore);
        toolbar = findViewById(R.id.toolbar);


        txtComment=findViewById(R.id.commenttxt);
        txtMark=findViewById(R.id.ratingmark);
        txtRating=findViewById(R.id.ratingtxt);
        share=findViewById(R.id.share);

        PromoComment=new ArrayList<>();
        // Map toolbar vào actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setTitle(info.getBrand().toUpperCase() + ": " + info.getAddress());

        // get comment
        listView=findViewById(R.id.list_comment);
        getComment();

        commentAdapter=new CommentAdapter(this,R.layout.comment_item,PromoComment);
        TextView textView=findViewById(R.id.list_empty);
        textView.setText("No comment in this promo");
        listView.setEmptyView(textView);
        listView.setAdapter(commentAdapter);

         shareDialog=new ShareDialog(this);
        String brandPath =info.getBrand()+"/";
        String logoPath = brandPath + info.getPromoimage();
        StorageReference logo = FirebaseStorage.getInstance().getReference(logoPath);

        GlideApp.with(this)
                .asBitmap()
                .load(logo)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        bitmap=resource;
                    }
                });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bitmap==null)
                    Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
                SharePhoto photo=new SharePhoto.Builder().setBitmap(bitmap).build();
                SharePhotoContent content=new SharePhotoContent.Builder().addPhoto(photo).build();
                shareDialog.show(content);
            }
        });

        // Lấy danh sách promo người dùng đã lấy rồi
        userpromo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    if (node.getValue(UserPromo.class).getIdPromo().equals(info.getPromoId())) {
                        getcode_btn.setText("Your Code:" + info.getCode());
                        getcode_btn.setClickable(false);
                        getcode_btn.setEnabled(false);

                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getcode_btn.setText("Rate promo and get your code");
        getcode_btn.setEnabled(true);
        getcode_btn.setClickable(true);
        getcode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRateDialog();
            }
        });
        getmethere_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CHuyển qua map full ở đây
                Intent intent = new Intent(V2PromoDisplayActivity.this, DirectionActivity.class);
                intent.putExtra("data", info);
                intent.putExtra("lat", currentPos.latitude);
                intent.putExtra("lng", currentPos.longitude);
                startActivity(intent);
            }
        });

        readmore_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Nếu textview đã được expand thì đóng nó lại
                // Không thì sẽ mở rộng nó ra
                if (isExpand) {
                    isExpand = false;
                    detail_descrip_txt.setMaxLines(1);
                    readmore_btn.setText("Read more");
                } else {
                    isExpand = true;
                    detail_descrip_txt.setMaxLines(Integer.MAX_VALUE);
                    readmore_btn.setText("Read less");
                }
            }
        });
    }

    /***
     * Khởi tạo dữ liệu đầu vào như mapFragment load ảnh logo, tên của hàng , mô tả khuyến mãi, v.v
     */
    void initData() {
        // Bắt đầu chạy Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Lấy logo của của hàng
        String brandPath = info.getBrand() + "/";
        String logoPath = brandPath + info.getBrand() + ".png";
        StorageReference logo = FirebaseStorage.getInstance().getReference(logoPath);
        GlideApp.with(this).load(logo).circleCrop().into(brandlogo_iv);

        // Lấy vị trí của cửa hàng để hiện thị trên map
        infoLatLng = new LatLng(info.getLat(), info.getLng());

        // CHỉnh nội dụng các view ứng với dữ liệu
        store_name_txt.setText(info.getBrand().toUpperCase());
//        rating_point_rb.setRating(info.getRating());
//        number_of_view_txt.setText(Integer.toString(info.getNumberofview()));
        address_txt.setText(info.getAddress());
        distance_txt.setText(Long.toString(Math.round(SimpleLocation.calculateDistance(currentPos.latitude, currentPos.longitude, info.getLat(), info.getLng()))) + "m");
        detail_descrip_txt.setText(info.getDescrip());

        txtComment.setText(""+PromoComment.size());
        txtRating.setText(""+info.getNumberofview());
        txtMark.setText(""+((int)(info.getRating()*2*10))/10.0);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        Log.i("hello", "hello");
        mMap = googleMap;
        MarkerOptions current = new MarkerOptions();
        current.position(new LatLng(info.getLat(), info.getLng()));
        mMap.addMarker(current);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current.getPosition(), 16));
    }

    /***
     * Lấy đường dẫn chứa dữ liệu người dùng
     */
    void getCurrentUserHistory() {
        mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String string = mail.replace('.', ',');
        userpromo = FirebaseDatabase.getInstance().getReference().child("/user/").child(string);
        root = FirebaseDatabase.getInstance().getReference();
        String q=key;
        commentData=FirebaseDatabase.getInstance().getReference().child("/comment/").child(key);
    }

    void getComment()
    {
        commentData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PromoComment.add(dataSnapshot.getValue(Comment.class));
                commentAdapter.notifyDataSetChanged();
                txtComment.setText(""+PromoComment.size());
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


    /***
     * Khởi tạo rate dialog để đánh giá
     */
    void startRateDialog() {
//        LayoutInflater layoutInflater = LayoutInflater.from(this); // lay form chinh
//        View v = layoutInflater.inflate(R.layout.rating_dialog_layout, null); // map layout vo form chinh
//

       // RatingBar ratingBar = v.findViewById(R.id.rating_promo);

//        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
//                updatePromoInfo(v);
//                ratingBar.setIsIndicator(true);
//                // THêm promotion này vào lịch sử người dùng
//                UserPromo userPromo=new UserPromo(info.getAddress(),info.getCode(),info.getDate(),info.getDistrict(),info.getPromoId(),info.getBrand(),info.getPromoimage());
//                Map<String,Object> map=userPromo.toMap(mail,info.getPromoId());
//                root.updateChildren(map);
//                code.setText(info.getCode());
//
//            }
//        });

//        AlertDialog.Builder builder= new AlertDialog.Builder(this);
//        builder.setView(v);
//        builder.setTitle("Please rate to get your code");
//        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog alertDialog=builder.create();
//        alertDialog.show();

        //Rating dialog
        RatingDialog();

    }

    /***
     * Update lại thông tin Promo
     * @param rating rating mới
     */
    void updatePromoInfo(float rating) {
//        float oldRating = info.getRating();
//        int n = info.getNumberofview();
//
//        float newRating = (oldRating * n + rating) / (n + 1);
//        promoData.child(key).child("rating").setValue(newRating);
//        promoData.child(key).child("numberofview").setValue(n + 1);
//        getcode_btn.setText("Your Code:" + info.getCode());
//        getcode_btn.setClickable(false);
//        getcode_btn.setEnabled(false);

        float oldRating = info.getRating();
        int n = info.getNumberofview();

        float newRating = (oldRating * n + rating) / (n + 1);
        promoData.child(key).child("rating").setValue(newRating);
        promoData.child(key).child("numberofview").setValue(n + 1);

        txtRating.setText(""+n+1);
        txtMark.setText(""+((int)(newRating*2*10))/10.0);


        getcode_btn.setText("Your Code:" + info.getCode());
        getcode_btn.setClickable(false);
        getcode_btn.setEnabled(false);
//        rating_point_rb.setRating(newRating);
//        number_of_view_txt.setText(Integer.toString(info.getNumberofview()+1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.onBackPressed();
                break;
            }
        }
        return true;
    }

    private void RatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(5)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.starColor)
                .setTitleTextColor(R.color.titleTextColor)
                .setDescriptionTextColor(R.color.contentTextColor)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.hintTextColor)
                .setCommentTextColor(R.color.commentTextColor)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create(this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        //updatePromoInfo(i);
        //Toast.makeText(this, ""+i+" "+comment, Toast.LENGTH_SHORT).show();
        updatePromoInfo(i);
        // THêm promotion này vào lịch sử người dùng
        UserPromo userPromo=new UserPromo(info.getAddress(),info.getCode(),info.getDate(),info.getDistrict(),info.getPromoId(),info.getBrand(),info.getPromoimage());
        Map<String,Object> map=userPromo.toMap(mail,info.getPromoId());
        root.updateChildren(map);
        // Comment user
        if(!s.isEmpty())
        {
            String photouri;
            Uri photo=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
            if(photo==null)
            {
                photouri="n/a";
            }
            else
            {
                photouri=photo.toString();
            }
            Comment tmp=new Comment(photouri,s,i,mail);
            Map<String, Object>obj=tmp.toMap(mail.replace('.',','),key);
            root.updateChildren(obj);
        }
        Toast.makeText(this, "You receive successful code:"+info.getCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onRideInformationLoaded() {

    }

    @Override
    public void onError(ApiError apiError) {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}
