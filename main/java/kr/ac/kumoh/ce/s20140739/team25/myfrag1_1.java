package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag1_1 extends Activity {

    String result = "";
    protected ArrayList<myfrag1_1.sroominfo> rArray = new ArrayList<myfrag1_1.sroominfo>();


    protected JSONObject mResult = null;
    protected ListView mList;
    protected myfrag1_1.sroomAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;


    private NMapContext mMapContext;
    private static final String CLIENT_ID = "fFSjB4GRDIV21x1EQJfa";// 애플리케이션 클라이언트 아이디 값
    private static final String LOG_TAG = "NMapViewer";
    private static final boolean DEBUG = false;


    private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(126.978371, 37.5666091);
    private static final int NMAP_ZOOMLEVEL_DEFAULT = 11;
    private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
    private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
    private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

    private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
    private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
    private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
    private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
    private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
    private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";
    private SharedPreferences mPreferences;

    NMapView mapView;
    private NMapController mMapController;

    private NMapOverlayManager mOverlayManager;

    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;

    private NMapViewerResourceProvider mMapViewerResourceProvider;

    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapPOIitem mFloatingPOIitem;

    private MapContainerView mMapContainerView;
    private NMapContext a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my1_1);
        //map=(ImageView)findViewById(R.id.map);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String img = intent.getStringExtra("img");
        rArray = new ArrayList<myfrag1_1.sroominfo>();
        mAdapter = new myfrag1_1.sroomAdapter(this, R.layout.listitem2, rArray);
        mList = (ListView) findViewById(R.id.listview2);
        mList.setAdapter(mAdapter);

        mMapContext =  new NMapContext(super.getBaseContext());
        mMapContext.onCreate();

        mapView = (NMapView) findViewById(R.id.mapView);
        mapView.setClientId(CLIENT_ID);// 클라이언트 아이디 설정
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setFocusable(true);
        mapView.setFocusableInTouchMode(true);
        mapView.requestFocus();
        mMapContext.setupMapView(mapView);

        mapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mapView.setOnMapViewDelegate(onMapViewTouchDelegate);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mapView.getMapController();

        // use built in zoom controls
        NMapView.LayoutParams lp = new NMapView.LayoutParams(NMapView.LayoutParams.WRAP_CONTENT,
                NMapView.LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);

        // 확대/축소를 위한 줌 컨트롤러 표시 옵션 활성화
        mapView.setBuiltInZoomControls(true, lp);

        // 오버레이 리소스 관리객체 할당 create resource provider
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        // set data provider listener
        setMapDataProviderListener(onDataProviderListener);

        // create overlay manager
        // 지도위에 표시되는 오버레이 객체들을 관리
        mOverlayManager = new NMapOverlayManager(this, mapView, mMapViewerResourceProvider);
        // register callout overlay listener to customize it.
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        // register callout overlay view listener to customize it.
        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

        // location manager
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        // compass manager
        mMapCompassManager = new NMapCompassManager(this);

        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        Cache cache = new DiskBasedCache(this.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(this)));
        myfrag1_1.back task = new myfrag1_1.back();
        task.execute(MainActivity.SERVER_IP_PORT+"/home/info/" + id);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = mAdapter.getItem(i).getId();
                Intent intent = new Intent(myfrag1_1.this, myfrag1_2.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mMapContext.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }

    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }

    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }

    private void restoreInstanceState() {
        mPreferences = getPreferences(MODE_PRIVATE);

        int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
        int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
        int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
        int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
        boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
        boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

        mMapController.setMapViewMode(viewMode);
        mMapController.setMapViewTrafficMode(trafficMode);
        mMapController.setMapViewBicycleMode(bicycleMode);
        mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);

        mapView.setScalingFactor(1.0F);

    }

    private void startMyLocation() {

        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }

            if (mMapLocationManager.isMyLocationEnabled()) {

                if (!mapView.isAutoRotateEnabled()) {
                    mMyLocationOverlay.setCompassHeadingVisible(true);

                    mMapCompassManager.enableCompass();

                    mapView.setAutoRotateEnabled(true, false);

                    mMapContainerView.requestLayout();
                } else {
                    stopMyLocation();
                }

                mapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(myfrag1_1.this, "Please enable a My Location source in system settings",
                            Toast.LENGTH_LONG).show();

                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
            }
        }
    }

    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mapView.setAutoRotateEnabled(false, false);

                mMapContainerView.requestLayout();
            }
        }
    }


    public void setMapDataProviderListener(NMapActivity.OnDataProviderListener var1) {
        if (this.a != null) {
            this.a.setMapDataProviderListener(var1);
        }

    }

    private final NMapActivity.OnDataProviderListener onDataProviderListener = new NMapActivity.OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {

            if (DEBUG) {
                Log.i(LOG_TAG, "onReverseGeocoderResponse: placeMark="
                        + ((placeMark != null) ? placeMark.toString() : null));
            }

            if (errInfo != null) {
                Log.e(LOG_TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                Toast.makeText(myfrag1_1.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                if (placeMark != null) {
                    mFloatingPOIitem.setTitle(placeMark.toString());
                }
                mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
            }
        }

    };
    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            if (mMapController != null) {
                mMapController.animateTo(myLocation);
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            // stop location updating
            //			Runnable runnable = new Runnable() {
            //				public void run() {
            //					stopMyLocation();
            //				}
            //			};
            //			runnable.run();

            Toast.makeText(myfrag1_1.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(myfrag1_1.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

            stopMyLocation();
        }

    };

    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

            if (errorInfo == null) { // success
                // restore map view state such as map center position and zoom level.
                //restoreInstanceState();
                startMyLocation();
            } else { // fail
                Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

                Toast.makeText(myfrag1_1.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView mapView) {

        }
    };
    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

        @Override
        public void onLongPress(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLongPressCanceled(NMapView mapView) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTouchDown(NMapView mapView, MotionEvent ev) {

        }

        @Override
        public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
        }

        @Override
        public void onTouchUp(NMapView mapView, MotionEvent ev) {
            // TODO Auto-generated method stub

        }

    };

    private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

        @Override
        public boolean isLocationTracking() {
            if (mMapLocationManager != null) {
                if (mMapLocationManager.isMyLocationEnabled()) {
                    return mMapLocationManager.isMyLocationFixed();
                }
            }
            return false;
        }

    };

    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
            }

            // [[TEMP]] handle a click event of the callout
            Toast.makeText(myfrag1_1.this, "여기바꾸면됩니다: " + item.getTitle(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                if (item != null) {
                    Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
                } else {
                    Log.i(LOG_TAG, "onFocusChanged: ");
                }
            }
        }
    };

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
                                                         Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay) itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Toast.makeText(myfrag1_1.this, text, Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem) overlayItem;

                if (poiItem.showRightButton()) {
                    return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
                            mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }

    };

    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {

        @Override
        public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

            if (overlayItem != null) {
                // [TEST] 말풍선 오버레이를 뷰로 설정함
                String title = overlayItem.getTitle();
                if (title != null && title.length() > 5) {
                    return new NMapCalloutCustomOverlayView(myfrag1_1.this, itemOverlay, overlayItem, itemBounds);
                }
            }

            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }

    };

    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }

            if (changed) {
                mOverlayManager.onSizeChanged(width, height);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpecWidth = widthMeasureSpec;
            int sizeSpecHeight = heightMeasureSpec;

            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);

                if (view instanceof NMapView) {
                    if (mapView.isAutoRotateEnabled()) {
                        int diag = (((int) (Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
                        sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
                        sizeSpecHeight = sizeSpecWidth;
                    }
                }

                view.measure(sizeSpecWidth, sizeSpecHeight);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                Log.i("task", "연결?");
                conn.connect();
                Log.i("task", "연결!");

                Log.i("task", "비트맵?");
                InputStream inputStream = conn.getInputStream();

                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                JSONObject jsResult = new JSONObject(str);
                JSONObject studyroom = jsResult.getJSONObject("studyroom");
                String img = studyroom.getString("img");
                String name = studyroom.getString("name");
                String address = studyroom.getString("address");
                NetworkImageView immg = (NetworkImageView) findViewById(R.id.room);
                immg.setImageUrl(MainActivity.SERVER_IP_PORT+"/" + img, mImageLoader);
                TextView nname = (TextView) findViewById(R.id.rname);
                nname.setText(name);
                TextView adr = (TextView) findViewById(R.id.address);
                adr.setText(address);
                JSONArray jsonMainNode = jsResult.getJSONArray("rooms");
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    String id = jsonChildNode.getString("id");
                    String ip = jsonChildNode.getString("ip");
                    String sname = jsonChildNode.getString("name");
                    String max = jsonChildNode.getString("max");
                    String desc = jsonChildNode.getString("description");
                    String image = jsonChildNode.getString("img");

                    rArray.add(new myfrag1_1.sroominfo(id, ip, sname, max, desc, image));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(myfrag1_1.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            Log.i("실행", "완료");
            return result;
        }
    }

    public class sroominfo {
        private String id;
        private String ip;
        private String img;
        private String sroom;
        private String max;
        private String etc;


        public sroominfo(String id, String ip, String sroom, String max, String etc, String img) {
            this.id = id;
            this.ip = ip;
            this.img = img;
            this.sroom = sroom;
            this.max = max;
            this.etc = etc;

        }

        public String getId() {
            return id;
        }

        public String getip() {
            return ip;
        }

        public String getImg() {
            return img;
        }

        public String getSroom() {
            return sroom;
        }

        public String getMax() {
            return max;
        }

        public String getEtc() {
            return etc;
        }

    }


    static class sroomViewHolder {
        TextView txsroom;
        TextView txmax;
        TextView txetc;
        NetworkImageView imimage;
    }

    public class sroomAdapter extends ArrayAdapter<myfrag1_1.sroominfo> {

        public sroomAdapter(Context context, int resource, List<myfrag1_1.sroominfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            myfrag1_1.sroomViewHolder holder;
            if (convertView == null) {
                convertView = myfrag1_1.this.getLayoutInflater().inflate(R.layout.listitem2, parent, false);
                holder = new myfrag1_1.sroomViewHolder();
                holder.txsroom = (TextView) convertView.findViewById(R.id.sname);
                holder.txmax = (TextView) convertView.findViewById(R.id.max);
                holder.txetc = (TextView) convertView.findViewById(R.id.etc);
                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.srimage);
                convertView.setTag(holder);

            } else {
                holder = (myfrag1_1.sroomViewHolder) convertView.getTag();
            }
            holder.txsroom.setText(getItem(position).getSroom());
            holder.txmax.setText(getItem(position).getMax());
            holder.txetc.setText(getItem(position).getEtc());
            holder.imimage.setImageUrl(MainActivity.SERVER_IP_PORT+"/" + getItem(position).getImg(), mImageLoader);
            return convertView;
        }
    }
}


