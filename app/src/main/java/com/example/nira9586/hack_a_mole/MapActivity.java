package com.example.nira9586.hack_a_mole;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private MapView mMapView;
    private ArcGISMap map;
    private SpatialReference spatialReference;
    private SimpleMarkerSymbol blueCircleSymbol;
    private SimpleMarkerSymbol redCircleSymbol;
    private SimpleLineSymbol greenDashedLineSymbol;
    private Graphic userPointGraphic;
    private GraphicsOverlay graphicsOverlay;
    private PointCollection eventPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        redCircleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);
        blueCircleSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF, 10);
        greenDashedLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFF00FF00, 1);
        mMapView = findViewById(R.id.mapView);
        spatialReference = SpatialReferences.getWgs84();
        eventPoints = new PointCollection(spatialReference);
        Point classOne = new Point(-118.1, 34, spatialReference);
        eventPoints.add(classOne);
        Graphic classOneGraphic = new Graphic(classOne, redCircleSymbol);
        map = new ArcGISMap(Basemap.createStreets());
        graphicsOverlay = new GraphicsOverlay();
        graphicsOverlay.getGraphics().add(classOneGraphic);
        mMapView.getGraphicsOverlays().add(graphicsOverlay);
        mMapView.setMap(map);
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    99 );
        }
        else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    1, mLocationListener);
        }
    }

    @Override
    protected void onPause(){
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Point point = new Point(location.getLongitude(), location.getLatitude(), spatialReference);
            eventPoints.add(point);
            Polyline polyline = new Polyline(eventPoints);
            Graphic lineGraphic = new Graphic(polyline, greenDashedLineSymbol);
            graphicsOverlay.getGraphics().remove(userPointGraphic);
            userPointGraphic = new Graphic(point, blueCircleSymbol);
            graphicsOverlay.getGraphics().add(userPointGraphic);
            graphicsOverlay.getGraphics().add(lineGraphic);
            Viewpoint viewpoint = new Viewpoint(point, 7000);
            mMapView.setViewpointAsync(viewpoint, 7);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
