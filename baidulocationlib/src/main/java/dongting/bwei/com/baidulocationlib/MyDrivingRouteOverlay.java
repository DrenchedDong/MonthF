package dongting.bwei.com.baidulocationlib;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;

import dongting.bwei.com.baidulocationlib.overlayutil.DrivingRouteOverlay;

/**
 * 作者:${董婷}
 * 日期:2017/6/27
 * 描述:
 */

public class MyDrivingRouteOverlay extends DrivingRouteOverlay {

    boolean useDefaultIcon = false;

    public MyDrivingRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public BitmapDescriptor getStartMarker() {
        if (useDefaultIcon) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        }
        return null;
    }

    @Override
    public BitmapDescriptor getTerminalMarker() {
        if (useDefaultIcon) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        }
        return null;
    }
}
