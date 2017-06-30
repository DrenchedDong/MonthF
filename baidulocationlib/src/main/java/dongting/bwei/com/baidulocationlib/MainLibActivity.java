package dongting.bwei.com.baidulocationlib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import dongting.bwei.com.baidulocationlib.overlayutil.DrivingRouteOverlay;
import dongting.bwei.com.baidulocationlib.overlayutil.OverlayManager;

/*
 * 作者:${董婷}
 * 日期:2017/6/26
 * 描述:主界面
 */

public class MainLibActivity extends Activity implements View.OnClickListener,OnGetRoutePlanResultListener {
    private BaiduMap mBaiduMap;
    private EditText et_end;
    private EditText et_start;

    boolean isFirstLoc = true; //是否是首次定位
    private LocationClient locationClient;

    //浏览路线节点相关
    Button mBtnPre = null;//上一个节点
    Button mBtnNext = null;//下一个节点
    int nodeIndex = -1;//节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;

    //地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    //如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null;    // 地图View
    //搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main_lib);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        Button bt_scan =(Button) findViewById(R.id.scan);
        bt_scan.setOnClickListener(this);

        mBaiduMap = mMapView.getMap();

//普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        // 删除百度地图LoGo
        //mMapView.removeViewAt(1);

/*
//卫星地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
*/

/*//空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);*/

//开启交通图
        mBaiduMap.setTrafficEnabled(true);

/*//开启交通图
        mBaiduMap.setBaiduHeatMapEnabled(true);*/

/*// 将底图标注设置为隐藏，方法如下：
        mBaiduMap.showMapPoi(false);*/


        //定义Maker坐标点
        final LatLng point = new LatLng(40.049256, 116.306406);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);


        //marker的点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                //double latitude = position.latitude;//纬度
                //double longitude = position.longitude;//经度
                //Toast.makeText(MainLibActivity.this, ""+latitude+"---"+longitude, Toast.LENGTH_SHORT).show();

                //实例化一个地理编码查询对象
                GeoCoder geoCoder = GeoCoder.newInstance();
                //设置反地理编码位置坐标
                ReverseGeoCodeOption op = new ReverseGeoCodeOption();
                op.location(position);
                //发起反地理编码请求(经纬度->地址信息)
                geoCoder.reverseGeoCode(op);
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
                        //获取点击的坐标地址
                        String address = arg0.getAddress();

                        //构建infoWindow
                        Button button = new Button(MainLibActivity.this);
                        button.setText(address);
                        button.setTextSize(20);

                        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                        InfoWindow mInfoWindow = new InfoWindow(button, point, -47);
//显示InfoWindow
                        mBaiduMap.showInfoWindow(mInfoWindow);
                    }

                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult arg0) {
                    }
                });
                return false;
            }
        });


        //设置缩放级别
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(14).build()));


        //地图点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            /**
             * 地图单击事件回调函数
             *
             * @param latLng 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng latLng) {

                //先清除图层
                mBaiduMap.clear();

                //实例化一个地理编码查询对象
                GeoCoder geoCoder = GeoCoder.newInstance();
                //设置反地理编码位置坐标
                ReverseGeoCodeOption op = new ReverseGeoCodeOption();
                op.location(latLng);
                //发起反地理编码请求(经纬度->地址信息)
                geoCoder.reverseGeoCode(op);
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
                        //获取点击的坐标地址
                        String address = arg0.getAddress();
                        System.out.println("address=" + address);
                        Toast.makeText(MainLibActivity.this, "" + address, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult arg0) {
                    }
                });
            }

            /**
             * 地图内 Poi 单击事件回调函数
             *
             * @param mapPoi 点击的 poi 信息
             */
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                //Toast.makeText(MainLibActivity.this, ""+mapPoi.getPosition().latitude, Toast.LENGTH_SHORT).show();
                return false;
            }
        });



        //定位
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //实例化 LocationClient类
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                //mBaiduMap 销毁后不再处理 接收新的位置
                if (location == null || mBaiduMap == null) {

                    return;
                }

                MyLocationData locData = new MyLocationData.Builder().accuracy
                        (location.getRadius()).direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();

           /* // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_launcher);
            MyLocationConfiguration config = new MyLocationConfiguration(null, true, mCurrentMarker);
             mBaiduMap.setMyLocationConfiguration(config);*/

                //设置定位数据
                mBaiduMap.setMyLocationData(locData);

                if (isFirstLoc) { //第一次定位

                    isFirstLoc = !isFirstLoc; //改变值  如果是false 则进不来了

                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及 缩放级别
                    mBaiduMap.animateMapStatus(u);
                }
            }
        });

        //注册监听函数
        //设置定位参数
        this.setLocationOption();
        locationClient.start(); //开启定位

/*// 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);*/






        //定义路线
        et_start = (EditText) findViewById(R.id.et_start);
        et_end = (EditText) findViewById(R.id.et_end);

        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(this);

    }



    /**
     * 设置定位参数
     */
    private void setLocationOption() {

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开Gps
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置成定位模式
        option.setCoorType("bd09ll"); //返回的结果 是百度的经纬度，默认值 gcj02
        option.setScanSpan(5000);//设置发起定位 请求的时间隔为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果 包含手机机头的方向
        locationClient.setLocOption(option);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.search) {
            String start = et_start.getText().toString().trim();
            String end = et_end.getText().toString().trim();

            route=null;
            mBaiduMap.clear();

            PlanNode stNode = PlanNode.withCityNameAndPlaceName("四川省",start);
            PlanNode enNode = PlanNode.withCityNameAndPlaceName("河南省",end);

            mSearch = RoutePlanSearch.newInstance();
            mSearch.setOnGetRoutePlanResultListener(this);

            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));


        } else if (i == R.id.scan) {

        }
    }
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainLibActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            Toast.makeText(MainLibActivity.this, "起终点或途经点地址有岐义", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }


    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}