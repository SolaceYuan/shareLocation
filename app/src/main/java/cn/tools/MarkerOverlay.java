package cn.tools;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import com.gddisplaymap.R;

/**
 * Created by my on 2016/12/19.
 */

public class MarkerOverlay {


    private List<LatLng> pointList = new ArrayList<LatLng>();
    private List<String> title = new ArrayList<String>();
    private AMap aMap;
    private LatLng centerPoint;
    private Marker centerMarker;
    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();
    private boolean isFirst = true;

    public MarkerOverlay(AMap amap, List<LatLng> points, LatLng centerpoint, List<String> titles) {
        this.aMap = amap;
        this.centerPoint = centerpoint;
        initPointList(points);
        initTitle(titles);
        initCenterMarker();
    }


    public void newList(LatLng latLng, String userName) {

    }

    //初始化list
    private void initPointList(List<LatLng> points) {
        if (points != null && points.size() > 0) {
            for (LatLng point : points) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    pointList.add(point);
                }

            }
        }
    }

    private void initTitle(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            for (String text : titles) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    title.add(text);
                }

            }
        }
    }

    //初始化中心点Marker.anchor(0.5f, 0.5f)
    private void initCenterMarker() {
        this.centerMarker = aMap.addMarker(new MarkerOptions()
                .position(centerPoint));
        centerMarker.showInfoWindow();
    }

    /**
     * 设置改变中心点经纬度
     *
     * @param centerpoint 中心点经纬度
     */
    public void setCenterPoint(LatLng centerpoint) {
        this.centerPoint = centerpoint;
        if (centerMarker == null)
            initCenterMarker();
        this.centerMarker.setPosition(centerpoint);
        centerMarker.setVisible(false);
        centerMarker.showInfoWindow();
    }

    /**
     * 添加Marker到地图中。
     */
    public void addToMap() {
        if (isFirst) {
            try {
                for (int i = 0; i < pointList.size(); i++) {
                    Marker marker = aMap.addMarker(getMarkerOptions(i));
                    marker.setObject(i);
                    //marker.showInfoWindow();
                    // mMarkers.add(marker);
                    this.isFirst = false;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                for (int i = 0; i < pointList.size(); i++) {
                    Marker marker = aMap.addMarker(getMarkerOptions(i));
                    marker.setObject(i);
                    //marker.showInfoWindow();
                    mMarkers.add(marker);

                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 配置每个marker的信息
     *
     * @param index
     * @return
     */
    private MarkerOptions getMarkerOptions(int index) {
        return new MarkerOptions()
                .position(
                        pointList.get(index))
                .title(getTitle(index))
                .zIndex(-1)
                .icon(getBitmapDescriptor(index));
    }

    /**
     * 返回第index的Marker的标题。
     *
     * @param index 第几个Marker。
     * @return marker的标题。
     */
    protected String getTitle(int index) {
        return title.get(index);
    }

    protected BitmapDescriptor getBitmapDescriptor(int index) {
        return null;
    }

    /**
     * 去掉MarkerOverlay上所有的Marker。
     */
    public void removeFromMap() {
        for (Marker mark : mMarkers) {
            mark.remove();
        }
        removeList();
        centerMarker.remove();
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中，且地图中心点不变。
     */
    public void zoomToSpanWithCenter() {
        if (pointList != null && pointList.size() > 0) {
            if (aMap == null)
                return;
            centerMarker.setVisible(false);
            centerMarker.showInfoWindow();
            LatLngBounds bounds = getLatLngBounds(centerPoint, pointList);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }

    //根据中心点和自定义内容获取缩放bounds
    private LatLngBounds getLatLngBounds(LatLng centerpoint, List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (centerpoint != null) {
            for (int i = 0; i < pointList.size(); i++) {
                LatLng p = pointList.get(i);
                LatLng p1 = new LatLng((centerpoint.latitude * 2) - p.latitude, (centerpoint.longitude * 2) - p.longitude);
                b.include(p);
                b.include(p1);
            }
        }
        return b.build();
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中。
     */
    public void zoomToSpan() {
        if (pointList != null && pointList.size() > 0) {
            if (aMap == null)
                return;
            centerMarker.setVisible(false);
            LatLngBounds bounds = getLatLngBounds(pointList);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }

    /**
     * 根据自定义内容获取缩放bounds
     */
    private LatLngBounds getLatLngBounds(List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < pointList.size(); i++) {
            LatLng p = pointList.get(i);
            b.include(p);
        }
        return b.build();
    }

    /**
     * 添加一个Marker点
     *
     * @param latLng 经纬度
     */
    public void addPoint(LatLng latLng) {
        //String friendsName="好友0";
        pointList.add(latLng);
//        Marker marker = aMap.addMarker(new MarkerOptions().position(latLng)
//                .icon(BitmapDescriptorFactory
//                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
//        marker.showInfoWindow();
//        marker.setObject(pointList.size() - 1);
//        mMarkers.add(marker);
    }

    /**
     * userName添加到集合
     */
    public void addTitle(String userName) {
        title.add(userName);
    }

    public void removeList() {
        pointList.clear();
        title.clear();
    }
}
