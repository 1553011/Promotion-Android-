package com.example.hautc.testproject.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hautc.testproject.EventCallback.onLocationRequest;
import com.example.hautc.testproject.Fragment.ControlFragment.AllDisplayHomeFragment;
import com.example.hautc.testproject.Fragment.ControlFragment.DistrictHomeFragment;
import com.example.hautc.testproject.Fragment.ControlFragment.HomeFragment;
import com.example.hautc.testproject.Fragment.ControlFragment.TypeHomeFragment;
import com.example.hautc.testproject.Fragment.User.UserFragment;
import com.example.hautc.testproject.GlideApp;
import com.example.hautc.testproject.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity implements onLocationRequest {

    boolean isFinished = false;

    // Fragment
    HomeFragment homeFragment;
    boolean homeFragmentCreateSuccess = false;
    DistrictHomeFragment districtHomeFragment;
    boolean districtHomeFragmentCreateSuccess = false;
    TypeHomeFragment typeHomeFragment;
    boolean typeHomeFragmentCreateSuccess = false;
    UserFragment userFragment;
    boolean userFragmentCreateSuccess=false;
    AllDisplayHomeFragment allDisplayHomeFragment;
    boolean allDisplayHomeFragmentCreateSuccess = false;

    Toolbar toolbar;
    // Firebase location
    String locationPath = "/location_list/";
    String typePath = "/type/";
    DatabaseReference LocationReference = FirebaseDatabase.getInstance().getReference().child(locationPath);
    DatabaseReference TypeReference = FirebaseDatabase.getInstance().getReference().child(typePath);

    // Chứa các danh sách vi65 trí, và loại location
    ArrayList<String> LocationList;
    ArrayList<String> TypeList;
    ArrayList<String> tmp;
    boolean isError = false;

    // Lấy vị trí hiện tại và gửi tới các Fragment
    private SimpleLocation getPos;
    LatLng currnetPos;
    SimpleLocation.Listener listener;


    // Các chế độ load
    final static int NEARBY_MODE = 0;
    final static int TYPE_MODE = 1;
    final static int DISTRICT_MODE = 2;
    final static int HISTORY_MODE = 3;
    final static int ALL_MODE = -1;
    int currentMode = ALL_MODE;

    final static int ACCESS_FINE_LOCATION = 101;
    final static int ACCESS_COARSE_LOCATION = 102;
    final static int INTERNET = 103;

    String data = "null";

    // Variable
    private static final int RC_SIGN_IN = 123;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_DISTRICT = "district";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_TYPE = "type";
    private static final String TAG_ALL = "all";
    private static final String TAG_LOGOUT = "logout";
    public static String CURRENT_TAG = TAG_HOME;
    private String[] activityTitles;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    // Lấy dữ liệ người dùng
    FirebaseUser user;
    String name;
    String mail;
    Uri photoUrl;
    Activity cur;

    // Các index của cho các Fragment riêng biệt
    final static int NEARBY_INDEX = 1;
    final static int DISTRICT_INDEX = 2;
    final static int TYPE_INDEX = 3;
    final static int HISTORY_INDEX = 4;
    final static int ALL_INDEX = 0;
    int currentFrament = 1;


    // PERMISSION
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {android.Manifest.permission.INTERNET
                            , android.Manifest.permission.ACCESS_FINE_LOCATION
                            , android.Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cur=this;
        super.onCreate(savedInstanceState);
        // Check permission
        Log.i("HelloWorld", "onCreate");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.hautc.testproject",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("<_GWKh4pSwlcsHBGM9Hx3UMvG-yZGHLUr>")
                // required for enhanced button features
                .setServerToken("<YbyTp7KQwk03zrTg7UZB8Kak4AuacmLw9JeklZlL>")
                // required for implicit grant authentication

                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(config);

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        // Đăng nhập
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        //.setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                        //new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.mipmap.logo)
                        .build(),RC_SIGN_IN);

        // Bắt đầu lấy vị trí hiện tại
        getCurrentLocation();

        if (savedInstanceState == null) {
            navItemIndex = ALL_INDEX;
            CURRENT_TAG = TAG_ALL;
            //loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        Log.i("HelloWorld", "loadNavHeader");
        // name, website

        txtName.setText(name);
        txtWebsite.setText(mail);
        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);
        if(photoUrl!=null)
        {
            // Loading profile image
            GlideApp.with(this).load(photoUrl).circleCrop()
                    .into(imgProfile);
        }
    }

    /***
     * Trả vài fragment ứng với Fragment người dùng
     * chọn từ navigation menu
     */
    private void loadHomeFragment() {
        Log.i("HelloWorld", "loadHomeFragment, index:" + navItemIndex);
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

//        if (currentFrament == navItemIndex){
//            drawer.closeDrawers();
//
//            return;
//        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Log.i("HelloWorld", "mPendingRunnable start");
        Runnable mPendingRunnable = new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                Log.i("HelloWorld", navItemIndex+"");
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            Log.i("HelloWorld", "mPendingRunnable not null");
            mHandler.post(mPendingRunnable);
        }
        Log.i("HelloWorld", "mPendingRunnable null");
        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    /***
        Lấy Fragement hiện thời
     */
    private Fragment getHomeFragment() {
        // Lấy fragment ứng với index mà người dùng chọn
        switch (navItemIndex) {
            case ALL_INDEX:{
                Toast.makeText(cur, "ALL", Toast.LENGTH_SHORT).show();
                Log.i("HelloWorld", "ALL");
                currentMode = ALL_MODE;
                allDisplayHomeFragment = new AllDisplayHomeFragment();
                return allDisplayHomeFragment;
            }
            case NEARBY_INDEX:
                Toast.makeText(cur, "NEARBY", Toast.LENGTH_SHORT).show();
                Log.i("HelloWorld", "NEARBY");
                currentMode = NEARBY_MODE;
                homeFragment = new HomeFragment();
                return homeFragment;
            case DISTRICT_INDEX:
                Toast.makeText(cur, "DISTRICT", Toast.LENGTH_SHORT).show();
                Log.i("HelloWorld", "DISTRICT");
                currentMode = DISTRICT_MODE;
                getLocationData();
                districtHomeFragment = new DistrictHomeFragment();
                return districtHomeFragment;
            case TYPE_INDEX:
                Toast.makeText(cur, "TYPE", Toast.LENGTH_SHORT).show();
                Log.i("HelloWorld", "TYPE");
                currentMode = TYPE_MODE;
                getPromoType();
                typeHomeFragment = new TypeHomeFragment();
                return typeHomeFragment;
            case HISTORY_INDEX:
                Toast.makeText(cur, "HISTORY", Toast.LENGTH_SHORT).show();
                Log.i("HelloWorld", "HISTORY");
                currentMode = HISTORY_MODE ;
                userFragment = new UserFragment();
                return userFragment;
            default:
                return homeFragment;
        }
    }

    /***
     * Chỉnh tên của toolbar
     */
    private void setToolbarTitle() {
        Log.i("HelloWorld", "setToolbarTitle");
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    /***
     *
     */
    private void selectNavMenu() {
        Log.i("HelloWorld", "selectNavMenu");
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    /***
     * Xử lý việc Chọn những item nào trong NavigationVIew
     */
    private void setUpNavigationView() {
        Log.i("HelloWorld", "setUpNavigationView");
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        navItemIndex = ALL_INDEX;
                        currentFrament = ALL_INDEX;
                        CURRENT_TAG = TAG_ALL;
                        homeFragmentCreateSuccess = false;
                        districtHomeFragmentCreateSuccess = false;
                        typeHomeFragmentCreateSuccess = false;
                        userFragmentCreateSuccess=false;
                        homeFragment = null;
                        districtHomeFragment = null;
                        typeHomeFragment = null;
                        userFragment=null;
                        break;
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_nearby:
                        navItemIndex = NEARBY_INDEX;
                        currentFrament = NEARBY_INDEX;
                        CURRENT_TAG = TAG_HOME;
                        allDisplayHomeFragmentCreateSuccess = false;
                        districtHomeFragmentCreateSuccess = false;
                        typeHomeFragmentCreateSuccess = false;
                        userFragmentCreateSuccess=false;
                        allDisplayHomeFragment = null;
                        districtHomeFragment = null;
                        typeHomeFragment = null;
                        userFragment=null;
                        break;
                    case R.id.nav_district:
                        //Toast.makeText(cur, "District selected", Toast.LENGTH_SHORT).show();
                        navItemIndex = DISTRICT_INDEX;
                        currentFrament = DISTRICT_INDEX;
                        CURRENT_TAG = TAG_DISTRICT;
                        allDisplayHomeFragmentCreateSuccess = false;
                        homeFragmentCreateSuccess = false;
                        typeHomeFragmentCreateSuccess = false;
                        allDisplayHomeFragment = null;
                        homeFragment = null;
                        typeHomeFragment = null;
                        userFragment=null;
                        userFragmentCreateSuccess=false;
                        break;
                    case R.id.nav_type:
                        //Toast.makeText(cur, "Type selected", Toast.LENGTH_SHORT).show();
                        navItemIndex = TYPE_INDEX;
                        CURRENT_TAG = TAG_TYPE;
                        currentFrament = TYPE_INDEX;
                        allDisplayHomeFragmentCreateSuccess = false;
                        homeFragmentCreateSuccess = false;
                        districtHomeFragmentCreateSuccess = false;
                        allDisplayHomeFragment = null;
                        homeFragment = null;
                        districtHomeFragment = null;
                        userFragment=null;
                        userFragmentCreateSuccess=false;
                        break;
                    case R.id.nav_history:
                        navItemIndex = HISTORY_INDEX;
                        CURRENT_TAG = TAG_HISTORY;
                        currentFrament = HISTORY_INDEX;
                        allDisplayHomeFragmentCreateSuccess = false;
                        typeHomeFragmentCreateSuccess = false;
                        homeFragmentCreateSuccess = false;
                        districtHomeFragmentCreateSuccess = false;
                        homeFragment = null;
                        districtHomeFragment = null;
                        allDisplayHomeFragment = null;
                        typeHomeFragment = null;
                        break;

                    case R.id.nav_logout:
                        AuthUI.getInstance()
                                .signOut((FragmentActivity) cur)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // Toast.makeText(getApplicationContext(), "Logout!", Toast.LENGTH_LONG).show();
                                        // user is now signed out
                                        startActivity(new Intent(MainActivity.this, MainActivity.class)); // cái vế sau này m truyền cái signin activity của m vào

                                    }
                                });
                        break;
                    default:
                        navItemIndex = ALL_INDEX;
                        currentFrament = ALL_INDEX;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    /***
     * XỬ lý nút Back
     */
    @Override
    public void onBackPressed() {
        // Nếu nhấn nút Back mà Drawer còn mở thì đóng Drawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    /***
     * Gắn menu cho từng Fragment riêng biệt
     * @param menu menu dùng để gắn
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == NEARBY_INDEX || navItemIndex == ALL_INDEX) {
            getMenuInflater().inflate(R.menu.menu, menu);
        }

        // when fragment is district, load the menu created for district
        if (navItemIndex == DISTRICT_INDEX) {
            getMenuInflater().inflate(R.menu.district_menu, menu);
        }

        // when fragment is district, load the menu created for district
        if (navItemIndex == TYPE_INDEX) {
            getMenuInflater().inflate(R.menu.type_menu, menu);
        }
        // when fragment is district, load the menu created for user

        return true;
    }

    /***
     * Xử lý các menu item
     * @param item biến dùng để xem menu item nào được chọn
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_logout:{
                Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                // Toast.makeText(getApplicationContext(), "Logout!", Toast.LENGTH_LONG).show();
                                // user is now signed out
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                //finish();
                            }
                        });

                return true;
            }
            case R.id.district_menu_item_filter:{
                getLocationData();
                break;
            }
            case R.id.type_menu_item_filter:{
                getPromoType();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Sau khi đăng nhập thành công thì bắt đầu khởi tạo MainActivity và các fragment liên quan
     * @param requestCode mã yêu cầu
     * @param resultCode mã kết quả
     * @param data dữ liệu
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Log.i("HelloWorld", "onActivityResult");
                // Gắn biến isFinished thành true để bắt đầu cập nhật vị trí
                isFinished = true;

                user = FirebaseAuth.getInstance().getCurrentUser();

                String uid = user.getUid();
                // Name, email address, and profile photo Url
                name = user.getDisplayName();
                mail = user.getEmail();

                photoUrl = user.getPhotoUrl();

                // Khởi tạo MainActivity: map các view và seet layout
                setContentView(R.layout.activity_main1);
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                mHandler = new Handler();

                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                navigationView = (NavigationView) findViewById(R.id.nav_view);

                // Navigation view header
                navHeader = navigationView.getHeaderView(0);
                txtName = (TextView) navHeader.findViewById(R.id.name);
                txtWebsite = (TextView) navHeader.findViewById(R.id.website);
                imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
                imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

                // load toolbar titles from string resources
                activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
                // Init Control Fragment
                //homeFragment = new HomeFragment();

                // load nav menu header data
                loadNavHeader();
                // initializing navigation menu
                setUpNavigationView();
                loadHomeFragment();




                //finish();
                return;
            } else {
                if(resultCode== RESULT_CANCELED)
                {
                    finish();
                }
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    // showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    // showSnackbar(R.string.unknown_error);
                    return;
                }
            }
        }

    }

    public void onClick(View v) {
        if (v.getId() == R.id.action_logout) {
           /* AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // Toast.makeText(getApplicationContext(), "Logout!", Toast.LENGTH_LONG).show();
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, MainActivity.class)); // cái vế sau này m truyền cái signin activity của m vào

                        }
                    });*/


        }
    }

    /***
     * Lấy các quận hiện tại trong database
     */
    void getLocationData(){
        LocationList = new ArrayList<>();
        // Lấy các uận có trong Firebase
        LocationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ Database
                HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();

                // KT xem data có tồn tại ko
                if(data == null) {
                    Toast.makeText(MainActivity.this, "Lỗi không nhận được dữ liệu từ hệ thống", Toast.LENGTH_SHORT).show();
                    isError = true;
                    return;
                }

                // Lấy key từng giá trị và duyệt key để lấy giá trị
                Set<String> keySet = data.keySet();
                for (String key : keySet){
                    LocationList.add(data.get(key));
                }
                // Tạo dialog để chọn vị trí
                createLocationPicker();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /***
        Tạo dialog để chọn quận sau khi
     */
    void createLocationPicker(){
        // Khởi tạo dialog để chọn quận
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn quận:");

        final String[] locations = LocationList.toArray(new String[0]);
        // KHi click vào thì sẽ gắn data = locations[which] để lấy ví trí
        builder.setItems(locations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, LocationList.get(which), Toast.LENGTH_SHORT).show();
                data = locations[which];
                sentCurrentLocation();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /***
     * Hàm được gọi khi nhận request từ Fragment
     * @param sender tên của Fragment gửi yêu cầu vị trí
     */
    @Override
    public void onReceiveLocationRequestFromFragment(String sender) {
        // Nếu là sender là PROMO thì xác nahn65 là Promo fragment được khởi tạo và bắt đầu gửi vị trí
        if (sender.compareTo("HOME") == 0){
            homeFragmentCreateSuccess = true;
            homeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        }
        if (sender.compareTo("DISTRICT") == 0){
            districtHomeFragmentCreateSuccess = true;
            districtHomeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        }
        if (sender.compareTo("TYPE") == 0){
            typeHomeFragmentCreateSuccess = true;
            typeHomeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        }
        if (sender.compareTo("ALL") == 0){
            allDisplayHomeFragmentCreateSuccess = true;
            allDisplayHomeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        }
        if (sender.compareTo("HISTORY") == 0){
            userFragmentCreateSuccess = true;
            userFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        }
    }

    /***
     * Khởi tạo biến và bắt đầu Lấy vị trí hiện tại
     */
    void getCurrentLocation(){
        // Tạo listener để update vị trí liện tục
        listener = new SimpleLocation.Listener() {
            @Override
            public void onPositionChanged() {
                if (isFinished){
                    currnetPos = new LatLng(getPos.getLatitude(), getPos.getLongitude());
                    sentCurrentLocation();
                }
            }
        };

        // Init SimpleLocation để lấy vị trí
        final boolean fineLocation = false;
        final boolean passiveMode = false;
        int updatePeriod = 1000 * 3;
        boolean newLocation;
        getPos = new SimpleLocation(this, fineLocation, passiveMode, updatePeriod);

        // Kiểm tra đã cấp quyền truy cập chưa
        if (!getPos.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }

        // Lấy vị trí hiện tại khi mới start app
        currnetPos = new LatLng(getPos.getLatitude(), getPos.getLongitude());

        // Gắn listener
        getPos.setListener(listener);
    }

    /***
     * Gửi đi vị trí hiện tại sau khi update
     */
    void sentCurrentLocation(){
        // Nếu homeFragmentCreateSuccess = true thì homeFragment khởi tạo thành công rồi bắt đầu gửi
        if (homeFragmentCreateSuccess)
            homeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        if (districtHomeFragmentCreateSuccess)
            districtHomeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        if (typeHomeFragmentCreateSuccess)
            typeHomeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
        if (allDisplayHomeFragmentCreateSuccess)
            allDisplayHomeFragment.onReceiveLocationFromActivity(currnetPos, currentMode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(hasPermissions(this, PERMISSIONS)){
            getPos.beginUpdates();
        }
    }

    @Override
    public void onPause() {
        getPos.endUpdates();
        super.onPause();
    }

    /***
     * Lấy các loại promo hiện tại trong database
     */
    void getPromoType(){
        TypeList = new ArrayList<>();

        TypeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy dữ liệu từ Database
                HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();

                // KT xem data có tồn tại ko
                if(data == null) {
                    Toast.makeText(MainActivity.this, "Lỗi không nhận được dữ liệu từ hệ thống", Toast.LENGTH_SHORT).show();
                    isError = true;
                    return;
                }

                // Lấy key từng giá trị và duyệt key để lấy giá trị
                Set<String> keySet = data.keySet();
                for (String key : keySet){
                    TypeList.add(data.get(key));
                }

                createTypePickerDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /***
     * Tạo dialog để chọn loại
     */
    void createTypePickerDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn loại Promotion:");

        final String[] types = TypeList.toArray(new String[0]);

        builder.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, LocationList.get(which), Toast.LENGTH_SHORT).show();
                data = types[which];
                sentCurrentLocation();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//    /***
//     * Yêu cầu mở 1 permission nào đó
//     */
//    void askforPermission(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M //  kt xem nó lớn hơn API nào thì add vào
//           &&  (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
//                || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
//                || checkSelfPermission(android.Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)) {
//            // kiểm tra xem máy có cho phép permission chưa
//            requestPermissions(,101); // nếu chưa thì gọi hàm này
//        }
//        else {
//            Toast.makeText(cur, "", Toast.LENGTH_SHORT).show();
//
//        }
//    }

    /***
     * Kiểm tra xem có permission ko
     * @param context context của activity
     * @param permissions danh sách các permission
     * @return true nếu đã có dử permision, false nếu không có đủ
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


        /***
         * Kiểm trra xem là permission đã được grant chưa
         * @param requestCode
         * @param permissions
         * @param grantResults
         */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        // Nếu đúng requestCode thì sẽ kt xem đã được granted chưa
//        if(requestCode == 101){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED
//               && grantResults[1] == PackageManager.PERMISSION_GRANTED
//               && grantResults[2] == PackageManager.PERMISSION_GRANTED ){
//                 askforPermission();
//            }
//            else{
//                Toast.makeText(cur, "Permission not granted! Exiting now!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }
}
