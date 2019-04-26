package com.example.piotr.mzkapi15;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;

public class MyOSMapsActivity extends AppCompatActivity{
    private WebView myWebView;

    private double startLocLongtd, startLocLat, finalLocLongtd, finalLocLat, startStationLat, startStationLongtd, finalStationLat, finalStationLongtd;
    private StationsData stationsData;
    private ArrayList<Integer> chosenRouteStations;
    String language;


    private final LocationListener userListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

          //  createToast(location.getLatitude() + "," + location.getLongitude());
            String scriptForJS = "javascript:updateGPS(" + location.getLatitude() + "," + location.getLongitude() + ");";
            myWebView.loadUrl(scriptForJS);

        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };

    private LocationManager mLocationManager;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myosmaps);
        myWebView = findViewById(R.id.myWebView);

        myWebView.setWebViewClient(new WebViewClient());
        myWebView.addJavascriptInterface(this,"Android");

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        boolean isRoute = (boolean) getIntent().getSerializableExtra("isRoute");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(isRoute) {
            setLocationPoints();
            setOSMap();
        }else{
            setWalkingRoute();
        }
    }


    private void setWalkingRoute(){

        String usersPlacesCoordinates = (String) getIntent().getSerializableExtra("UsersPlacesCoordinates");
        String[] usersPlaces = usersPlacesCoordinates.split(";");

        String[] startPlace = usersPlaces[0].split(",");
        startLocLat = Double.parseDouble(startPlace[0]) ;
        startLocLongtd = Double.parseDouble(startPlace[1]) ;
        String[] finalPlace = usersPlaces[1].split(",");
        finalLocLat = Double.parseDouble(finalPlace[0]) ;
        finalLocLongtd = Double.parseDouble(finalPlace[1]);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,userListener);
        Location location = null;
        String userStartLocations = "53.8109,22.3482";
        if(mLocationManager!=null) {

            location = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
            if (location != null) {
                userStartLocations = location.getLatitude() + "," + location.getLongitude();
            }
        }


       String walkingRoute = "<html>\n" +
                "              <head>\n" +
                "              \t\n" +
                "              \t<title>Quick Start - Leaflet</title>\n" +
                "              \t<meta charset=\"utf-8\" />\n" +
                "              \t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "              \t\n" +
                "              \t<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"docs/images/favicon.ico\" />\n" +
                "              <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.2.0/dist/leaflet.css\" />\n" +
                "              <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet-routing-machine@latest/dist/leaflet-routing-machine.css\" />\n" +
                "              <script src=\"https://unpkg.com/leaflet@1.2.0/dist/leaflet.js\"></script>\n" +
                "              <script src=\"https://unpkg.com/leaflet-routing-machine@latest/dist/leaflet-routing-machine.js\"></script>\n" +
                "              \t\n" +
                "              </head>\n" +
                "              <body>\n" +
                "\t\t<style> .leaflet-routing-container{display:none} </style>\n" +
                "              <div id=\"mapid\" style=\"width:100%; height:97vh;\"></div>\n" +
                "              <script>\n" +
                "              \tvar mymap = L.map('mapid').setView([" + startLocLat + ", " + startLocLongtd +     "], 15);\n" +

               "var GPSmarker = L.marker([" + userStartLocations + "]).addTo(mymap);\n" +
               "\t\t\t\t\t\t\t\tfunction updateGPS(lat,lng){\n" +
               "\t\t\t\t\t\t\t\t\t\tGPSmarker.setLatLng([lat, lng]);\n" +
               "\t\t\t\t\t\t\t\t}\n" +
                " mymap.attributionControl=false\n" +
                "L.tileLayer('http://163.172.169.255/hot/{z}/{x}/{y}.png', {"+
                "maxZoom: 19"+
                "}).addTo(mymap);" +


                "L.Routing.control({\n"+
                "  plan: L.Routing.plan([\n" +
                " L.latLng(" + startLocLat + "," +
                startLocLongtd + "),\n" +
                " L.latLng(" + finalLocLat + "," +
                finalLocLongtd + ")]," +
               "{createMarker: function(i, wp) {\n" +
               "\t\t\t\treturn L.marker(wp.latLng, {\ndraggable: false," +
               "\t\t\t\t\ticon: L.icon({\n" +
               "    iconUrl: 'http://www.marchigiana.org.br/home/images/Diversas/Map-Marker-Marker-Outside-Chartreuse-icon.png',\n" +
               "    iconSize: [36, 44],\n" +
               "    iconAnchor: [15, 44]\n" +
               "})\n" +
               "\t\t\t\t});\n" +
               "\t\t\t}\n" +
               "})," +
               "show:false,\n" +
                " router: new L.Routing.OSRMv1({serviceUrl: 'http://163.172.169.255:5000/route/v1', profile: 'foot'})," +
               " lineOptions: {\n" +
               "      styles: [{color: '#00923f', opacity: 1, weight: 3, dashArray: '7,12' }]\n" +
               "   }"+
                "}).addTo(mymap);\n" +      "         </script>\n" +
               "              </body>\n" +
               "              </html>";


        String encodedHtml = Base64.encodeToString(walkingRoute.getBytes(),
                Base64.NO_PADDING);


        myWebView.loadData(encodedHtml, "text/html", "base64");
    }



    private void setLocationPoints(){


        String usersPlacesCoordinates = (String) getIntent().getSerializableExtra("UsersPlacesCoordinates");
        String[] usersPlaces = usersPlacesCoordinates.split(";");

        String[] startPlace = usersPlaces[0].split(",");
        startLocLat = Double.parseDouble(startPlace[0]) ;
        startLocLongtd = Double.parseDouble(startPlace[1]) ;
        String[] finalPlace = usersPlaces[1].split(",");
        finalLocLat = Double.parseDouble(finalPlace[0]) ;
        finalLocLongtd = Double.parseDouble(finalPlace[1]);

        stationsData = (StationsData) getIntent().getSerializableExtra("StationsData");
        chosenRouteStations = getIntent().getIntegerArrayListExtra("ChosenRouteStations");
        language = (String) getIntent().getSerializableExtra("Language");

        if(chosenRouteStations.size()!=0) {
            startStationLat = stationsData.getStationLatitude(chosenRouteStations.get(0));
            startStationLongtd = stationsData.getStationLongitude(chosenRouteStations.get(0));
            finalStationLat = stationsData.getStationLatitude(chosenRouteStations.get(chosenRouteStations.size() - 1));
            finalStationLongtd = stationsData.getStationLongitude(chosenRouteStations.get(chosenRouteStations.size() - 1));
        }
    }


    private void setOSMap(){



        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,userListener);

        Location location = null;
        String userStartLocations = "53.8109,22.3482";
        if(mLocationManager!=null) {

            location = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
            if (location != null) {
                userStartLocations = location.getLatitude() + "," + location.getLongitude();
            }
        }

        StringBuilder route =  new StringBuilder("<html>\n" +
                "              <head>\n" +
                "              \t\n" +
                "              \t<title>Quick Start - Leaflet</title>\n" +
                "              \t<meta charset=\"utf-8\" />\n" +
                "              \t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "              \t\n" +
                "              \t<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"docs/images/favicon.ico\" />\n" +
                "              <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.2.0/dist/leaflet.css\" />\n" +
                "              <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet-routing-machine@latest/dist/leaflet-routing-machine.css\" />\n" +
                "              <script src=\"https://unpkg.com/leaflet@1.2.0/dist/leaflet.js\"></script>\n" +
                "              <script src=\"https://unpkg.com/leaflet-routing-machine@latest/dist/leaflet-routing-machine.js\"></script>\n" +
                "              \t\n" +
                "              </head>\n" +
                "              <body>\n" +
                "\t\t<style> .leaflet-routing-container{display:none} </style>\n" +
                "              <div id=\"mapid\" style=\"width:100%; height:97vh;\"></div>\n" +
                "              <script>\n" +
                "              \tvar mymap = L.map('mapid').setView([" + startLocLat + ", " + startLocLongtd +     "], 15);\n" +
                "var GPSIcon = L.icon({\n" +
                "    iconUrl: 'https://qph.fs.quoracdn.net/main-qimg-c14c27631f64ac7395baf50cebae21a8',\n" +
                "    iconSize: [22, 22],\n" +
                "    iconAnchor: [10, 22]\n" +
     //           "    popupAnchor: [-3, -76],\n" +
                "});" +
                "var GPSmarker = L.marker([" + userStartLocations + "],{icon:GPSIcon}).addTo(mymap);\n" +
                "\t\t\t\t\t\t\t\tfunction updateGPS(lat,lng){\n" +
                "\t\t\t\t\t\t\t\t\t\tGPSmarker.setLatLng([lat, lng]);\n" +
                "\t\t\t\t\t\t\t\t}\n" +

               " mymap.attributionControl=false\n" +
                 "L.tileLayer('http://163.172.169.255/hot/{z}/{x}/{y}.png', {"+
                "maxZoom: 19"+
                        "}).addTo(mymap);" +


                "L.Routing.control({\n"+
                "  plan: L.Routing.plan([\n" +
                " L.latLng(" + startLocLat + "," +
               startLocLongtd + "),\n" +
                " L.latLng(" + startStationLat + "," +
               startStationLongtd + ")], " +
                "{createMarker: function(i, wp) {\n" +
                "\t\t\t\treturn L.marker(wp.latLng, {\ndraggable: false," +
                "\t\t\t\t\ticon: L.icon({\n" +
                "    iconUrl: 'http://www.marchigiana.org.br/home/images/Diversas/Map-Marker-Marker-Outside-Chartreuse-icon.png',\n" +
                "    iconSize: [36, 44],\n" +
                "    iconAnchor: [15, 44]\n" +
                "})\n" +
                "\t\t\t\t});\n" +
                "\t\t\t}\n" +
                "})," +
                "show:false,\n" +
                " router: new L.Routing.OSRMv1({serviceUrl: 'http://163.172.169.255:5000/route/v1', profile: 'foot'})," +
                " lineOptions: {\n" +
                "      styles: [{color: '#00923f', opacity: 1, weight: 3, dashArray: '7,12' }]\n" +
                "   }"+
                "}).addTo(mymap);\n" +

                "L.Routing.control({\n"+
                " plan: L.Routing.plan([\n" +
                " L.latLng(" + finalStationLat + "," +
                finalStationLongtd + "),\n" +
                " L.latLng(" + finalLocLat + "," +
                finalLocLongtd + ")]," +
                "{createMarker: function(i, wp) {\n" +
                "\t\t\t\treturn L.marker(wp.latLng, {\ndraggable: false," +
                "\t\t\t\t\ticon: L.icon({\n" +
                "    iconUrl: 'http://www.marchigiana.org.br/home/images/Diversas/Map-Marker-Marker-Outside-Chartreuse-icon.png',\n" +
                "    iconSize: [36, 44],\n" +
                "    iconAnchor: [15, 44]\n" +
                "})\n" +
                "\t\t\t\t});\n" +
                "\t\t\t}\n" +
                "})," +
                "show:false,\n" +
                " router: new L.Routing.OSRMv1({serviceUrl: 'http://163.172.169.255:5000/route/v1', profile: 'foot'})," +
                " lineOptions: {\n" +
                "      styles: [{color: '#00923f', opacity: 1, weight: 3, dashArray: '7,12' }]\n" +
                "   }"+
                "}).addTo(mymap);\n") ;




       StringBuilder waypointsBetween = new StringBuilder("");
       for(int i =1; i<chosenRouteStations.size()-1;i++){

           waypointsBetween.append(" L.latLng(" + stationsData.getStationLatitude(chosenRouteStations.get(i)) + "," +
                   stationsData.getStationLongitude(chosenRouteStations.get(i)) + "),\n");
       }

      String betweenStations = "L.Routing.control({\n "+
               "  plan: L.Routing.plan([\n" +
                       " L.latLng(" + startStationLat + "," +
                      startStationLongtd + "),\n" +
               waypointsBetween +
                       " L.latLng(" + finalStationLat+ "," +
                       finalStationLongtd + ")]," +
              "{createMarker: function(i, wp) {\n" +
              "\t\t\t\treturn L.marker(wp.latLng, {\ndraggable: false," +
              "\t\t\t\t\ticon: L.icon({\n" +
              "    iconUrl: 'http://cdn.onlinewebfonts.com/svg/img_466437.png',\n" +
              "    iconSize: [36, 44],\n" +
              "    iconAnchor: [15, 44]\n" +
           //   "    popupAnchor: [-3, -76]\n" +
              "})\n" +
              "\t\t\t\t});\n" +
              "\t\t\t}\n" +
              "})," +
              "show:false,\n" +
                       " router: new L.Routing.OSRMv1({serviceUrl: 'http://163.172.169.255:5001/route/v1', profile: 'driving'}),\n" +
              " lineOptions: {\n" +
              "      styles: [{color: '#007DC4', opacity: 1, weight: 3 }]\n" +
              "   }"+
                       "}).addTo(mymap);\n" ;


                route.append(betweenStations+      "         </script>\n" +
                        "              </body>\n" +
                        "              </html>");



        System.out.println(route);
        String result = new String(route);
        String encodedHtml = Base64.encodeToString(result.getBytes(),
                Base64.NO_PADDING);


        myWebView.loadData(encodedHtml, "text/html", "base64");

    }


    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }


    private void createToast(String result){

        Toast.makeText(getApplicationContext(), result,
                Toast.LENGTH_LONG).show();

    }
}




