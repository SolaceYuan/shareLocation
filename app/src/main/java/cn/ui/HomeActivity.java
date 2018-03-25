package cn.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.gddisplaymap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.server.GetFList;
import cn.server.Send;
import cn.server.Send_InputStream;
import cn.tools.MarkerOverlay;
import cn.tools.MyApp;
import cn.tools.MyTools;
import cn.tools.SensorEventHelper;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationSource, AMapLocationListener, View.OnClickListener, AMap.OnMapLoadedListener {

    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;// 高德相关
    //private MyLocationStyle myLocationStyle;
    private boolean isFirst = true;
    private static double lat, lng;//实时定位的经纬度
    private Button start;//共享位置按钮
    private Button stop;//停止共享位置按钮
    private Button zoomCenter;//缩放地图
    private Send send = new Send(HomeActivity.this);
    private Send_InputStream closeIn = new Send_InputStream();
    private GetFList listSend = new GetFList(HomeActivity.this);
    private double latitude, longitude;//接收共享位置的经纬度
    private String markerName;
    private boolean followMove = true;//跟随定位移动屏幕中心
    private Marker marker, markerOwner;//接收的marker,自己位置的marker
    private List<Marker> list;//存放共享位置的list
    private boolean isClick = false;//判断是否点击了共享位置按钮,同时显示自己的位置信息
    private boolean isZoom = true;
    private TextView userName;
    private TextView phoneNumber;
    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private MyHandler handler = null;
    private MyApp myApp = null;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    private MarkerOverlay markerOverlay;
    private LatLng center = new LatLng(23.629669867621526, 113.67667290581598);// 中心点
    String[] friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
//        mSM = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mSensor = mSM.getDefaultSensor(Sensor.TYPE_ORIENTATION);
//        mSM.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_UI);
//
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initLocation();//初始化定位参数
        checkStoragePermission();//初始化请求权限，存储权限
        checkLocationPermission();

        initMap();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        userName = (TextView) headerView.findViewById(R.id.userName);
        phoneNumber = (TextView) headerView.findViewById(R.id.phoneNumber);
        userName.setText(MyTools.getParam(this, "userName", "String").toString());
        phoneNumber.setText(MyTools.getParam(this, "phoneNumber", "String").toString());
        userName.setClickable(true);
        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.leftin, R.anim.leftout);
            }
        });
        zoomCenter = (Button) findViewById(R.id.btn_center);
        start = (Button) findViewById(R.id.latlng_start);
        stop = (Button) findViewById(R.id.latlng_stop);
        zoomCenter.setOnClickListener(this);
        stop.setOnClickListener(this);
        stop.setVisibility(View.INVISIBLE);
        start.setOnClickListener(this);
        list = new ArrayList<>();
        zoomCenter.setEnabled(false);
        myApp = (MyApp) getApplication();
        handler = new MyHandler();
        myApp.setHandler(handler);


    }

    public String getUserName() {
        String userName = MyTools.getParam(this, "userName", "String").toString();
        return userName;
    }

    public String getPhoneNum() {
        String PhoneNum = MyTools.getParam(this, "phoneNumber", "String").toString();
        Log.i("3333333333", PhoneNum);
        return PhoneNum;
    }

    private List<LatLng> getPointList() {
        List<LatLng> pointList = new ArrayList<LatLng>();
        pointList.add(new LatLng(23.629669867621526, 113.67667290581598));

        return pointList;
    }

    private List<String> getTitleList() {
        List<String> titleList = new ArrayList<String>();
        titleList.add(new String("沙发"));

        return titleList;
    }

    @Override
    public void onMapLoaded() {
        //添加MarkerOnerlay
        markerOverlay = new ViewMarkerOverlay(aMap, getPointList(), center, getTitleList());
        markerOverlay.addToMap();
        markerOverlay.zoomToSpanWithCenter();
    }

    public class ViewMarkerOverlay extends MarkerOverlay {

        public ViewMarkerOverlay(AMap amap, List<LatLng> points, LatLng centerpoint, List<String> titles) {
            super(amap, points, centerpoint, titles);
        }

        @Override
        protected BitmapDescriptor getBitmapDescriptor(int index) {
            View view = null;
            view = View.inflate(HomeActivity.this, R.layout.custom_view, null);
            TextView textView = ((TextView) view.findViewById(R.id.title));
            textView.setText(getTitle(index));

            return BitmapDescriptorFactory.fromView(view);
        }
    }

    /**
     * 通过handler更新地图点marker
     */
    public final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message ms) {
            super.handleMessage(ms);
            JSONArray jsonArray = (JSONArray) ms.obj;
            try {
                markerOverlay.removeFromMap();
                markerOverlay.removeList();
                String phone;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    latitude = jsonObject.getDouble("lat");
                    longitude = jsonObject.getDouble("lng");
                    phone = jsonObject.getString("phone");
                    if (latitude != 0 && longitude != 0) {
                        for (int j = 0; j < friendsList.length; j++) {
                            if (friendsList[j].equals(phone)) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                markerName = jsonObject.getString("userName");
                                markerOverlay.addTitle(markerName);
                                markerOverlay.addPoint(latLng);
                                markerOverlay.addToMap();
                                Log.i("130", "3333333333添加点完成333333333333" + latLng.toString());
                            }
                        }
                    }
                }
                if (isZoom) {
                    zoomToSpanWithCenter();
                }
            } catch (Exception e) {
                Log.i("130", "解析出错" + e.getMessage());
            }

        }
    }

    /**
     * 所有的位置信息显示在屏幕内
     */
    private void zoomToSpanWithCenter() {
        markerOverlay.zoomToSpanWithCenter();
    }


    private void checkLocationPermission() {
        // 检查是否有定位权限
        // 检查权限的方法: ContextCompat.checkSelfPermission()两个参数分别是Context和权限名.
        // 返回PERMISSION_GRANTED是有权限，PERMISSION_DENIED没有权限
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //没有权限，向系统申请该权限。
            Log.i("MY", "没有权限");
            Toast.makeText(HomeActivity.this, "没有获得定位权限", Toast.LENGTH_SHORT).show();
            requestPermission(LOCATION_PERMISSION_CODE);
        } else {
            //已经获得权限，则执行定位请求。
            Toast.makeText(HomeActivity.this, "已获取定位权限", Toast.LENGTH_SHORT).show();
            // activate();
            //startLocation();

        }
    }

    private void checkStoragePermission() {
        // 检查是否有存储的读写权限
        // 检查权限的方法: ContextCompat.checkSelfPermission()两个参数分别是Context和权限名.
        // 返回PERMISSION_GRANTED是有权限，PERMISSION_DENIED没有权限
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有权限，向系统申请该权限。
            Log.i("MY", "没有权限");
            requestPermission(STORAGE_PERMISSION_CODE);
        } else {
            //同组的权限，只要有一个已经授权，则系统会自动授权同一组的所有权限，比如WRITE_EXTERNAL_STORAGE和READ_EXTERNAL_STORAGE
            Toast.makeText(HomeActivity.this, "已获取存储的读写权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission(int permissioncode) {
        String permission = getPermissionString(permissioncode);
        if (!IsEmptyOrNullString(permission)) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    permission)) {


            } else {
                Log.i("MY", "返回false 不需要解释为啥要权限，可能是第一次请求，也可能是勾选了不再询问");
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{permission}, permissioncode);
            }
        }
    }

    private String getPermissionString(int requestCode) {
        String permission = "";
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case STORAGE_PERMISSION_CODE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
        }
        return permission;
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    /**
     * 初始化
     */
    private void initMap() {
        new Thread(listSend).start();
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    /**
     * 设置地图属性
     */
    private void setUpMap() {
        // aMap.setOnMapClickListener(this);// 地图点击监听
        aMap.setLocationSource(this);
        aMap.setOnMapLoadedListener(this); //地图加载完成监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 跟随模式
        // aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        /**
         *屏幕跟随中心点
         */
        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (followMove) {
                    followMove = false;
                }
                if (isZoom) {
                    isZoom = false;
                }
            }
        });

    }

    /**
     * 定位发生改变时，刷新定位maker
     *
     * @param aMapLocation
     */
    public void onLocationChanged(AMapLocation aMapLocation) {
        mlocationClient.startLocation();
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

                LatLng location = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                center = location;
                // aMap.setMyLocationStyle(myLocationStyle);
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, aMapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    if (followMove) {
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                    }
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(aMapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                    if (followMove) {
                        aMap.animateCamera(CameraUpdateFactory.changeLatLng(location));
                    }

                }
                lat = aMapLocation.getLatitude();
                lng = aMapLocation.getLongitude();
                LatLng newCenter = new LatLng(lat, lng);
                center = newCenter;
                markerOverlay.setCenterPoint(newCenter);
//                if (isClick) {
//                    if (markerOwner != null) {
//                        markerOwner.remove();//每次定位发生改变的时候,把自己的marker先移除再添加
//                    }
//                    markerOwner = aMap.addMarker((help_add_icon(new LatLng(lat, lng), R.mipmap.icon_myp)));
//                }
            } else {
                Log.i("123", aMapLocation.getErrorCode() + "错误码" + aMapLocation.getErrorInfo() + "错误信息");
            }
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(HomeActivity.this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);// 设置定位监听
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);// 设置为高精度定位模式
            mLocationOption.setInterval(2000);//连续定位，2000ms
            mlocationClient.startLocation();


        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mapView.onPause();
        deactivate();
        mFirstFix = false;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocMarker != null) {
            mLocMarker.destroy();
        }
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    public static double getLat() {
        return lat;
    }

    public static double getLng() {
        return lng;
    }




    /**
     * 手机上显示共享位置的图标
     *
     * @param latLng
     * @param id
     * @return
     */
    public static MarkerOptions help_add_icon(LatLng latLng, int id) {
        MarkerOptions markOptiopns = new MarkerOptions().position(latLng)
                .title("121")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
                );
        return markOptiopns;
    }

    /**
     * 移除
     *
     * @param list
     */
    public static void Remove(List<Marker> list) {
        if (list != null) {
            for (Marker marker : list) {
                marker.remove();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    //右划菜单
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_friends) {
            //Toast.makeText(this,"我的好友",Toast.LENGTH_SHORT);
            Intent intent = new Intent(HomeActivity.this, FriendsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.leftout);

            // Handle the camera action
        } else if (id == R.id.logOut) {
            // Toast.makeText(this,"退出登录",Toast.LENGTH_SHORT);
            MyTools.removeParam(this, MyTools.IS_LOGIN);
            Intent intent = new Intent(HomeActivity.this, loginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_message) {
            Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.leftin, R.anim.leftout);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.latlng_start:
                start.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);
                send.SetIsClose(true);
                zoomCenter.setEnabled(true);
                new Thread(send).start();
                isClick = true;
                break;
            case R.id.latlng_stop:
                markerOverlay.removeFromMap();
                send.SetIsClose(false);
                closeIn.setClose(true);
                stop.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
                zoomCenter.setEnabled(false);
                break;
            case R.id.btn_center:
                zoomToSpanWithCenter();
                isZoom = true;
                break;
            default:
                break;
        }
    }

    //监控返回按钮
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isExit;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isExit = MyTools.exit(this);
            if (isExit) {
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 范围圆
     *
     * @param latlng
     * @param radius
     */
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    //添加自己的marker
    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked)));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);

        mLocMarker = aMap.addMarker(options);
        //mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    /**
     * 获取好友列表并保存
     *
     * @param jsonObject
     */
    public void setFriends(JSONObject jsonObject) {
        String friends = "";
        try {
            friends = jsonObject.getString("friends");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (friends.length() > 0) {
            friendsList = friends.split(",");
        }
    }

}
