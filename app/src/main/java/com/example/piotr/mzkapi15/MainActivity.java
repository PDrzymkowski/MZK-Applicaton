package com.example.piotr.mzkapi15;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


/**
 * MainActivity class responsible for handling primary UI for user to choose:
 * - departure place
 * - destination
 * - hour and date of departure
 * Its other use is downloading stations numbers with their names which will be
 * in use in next activities
 *
 * This class UI implements Location Listener, autocompleteTextView and AlertDialogs
 */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private TextView dateTextView, hourTextView;
    String startPlace, finalPlace;
    List<ArrayList<String>> MyArrayListOfData;

    /**
     * Object enabling to get place's coordinates using its full name via GeoCoder
     */
    PlaceCoordinates places;




    /**
     * Objects necessary for using Google Places API implemented in
     * Autocomplete TextViews
     */
    private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;
    private AutoCompleteTextView startTextView, finalTextView;

    /**
     * Calendar object for storing users departure time
     */
    private Calendar calendar;


    /**
     * Holds information about stations coordinates, names and numbers
     * Downloads data in external thread
     */
    private StationsData stationsData;

    /**
     * Responsible for downloading data for stationsData object
     */
    private Thread stationsDataThread;

    private double userLatitude;

    /**
     * Displays toolbar object in MainActivity enabling user to change program language
     */
    private Toolbar toolbar;

    /**
     * Takes value for the chosen language
     */
    private String language;


    /**
     * Manages user's changing location
     */
    private LocationManager mLocationManager;

    private ImageButton startPlaceButton, finalPlaceButton;
    private boolean startPlaceButtonGPS=true, finalPlaceButtonGPS=true;


    /**
     * LocationListener object for updating users GPS coordinated
     */
    private final LocationListener startLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            userLatitude = location.getLatitude();
                places.setStartData(location);
               if(language.equals("PL")) startTextView.setHint(getResources().getString(R.string.GPSLocationPL));
               else if(language.equals("ENG")) startTextView.setHint(getResources().getString(R.string.GPSLocationENG));




        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };

    private final LocationListener finalLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            userLatitude = location.getLatitude();
                places.setFinalData(location);
            if(language.equals("PL")) finalTextView.setHint(getResources().getString(R.string.GPSLocationPL));
            else if(language.equals("ENG")) finalTextView.setHint(getResources().getString(R.string.GPSLocationENG));


        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    };


    /**
     * Starting method implementing necessary content for the UI as well as
     * - running necessary methods
     * - getting user's GPS coordinates
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


try {

    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {


        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        runNecessaryThreads();

        language = "PL";

        places = new PlaceCoordinates(getApplicationContext());

        startTextView = findViewById(R.id.start_place);
        finalTextView = findViewById(R.id.final_place);

        setClickableInterface();
        setAutoComplete();
        setToolbarAction();
        setLanguage();
        setLoginInterface();
    } else {
        setContentView(R.layout.activity_no_internet_connection);
        Button letsStartButton = findViewById(R.id.startAppButton);
        letsStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });

    }
}catch(Exception e){
    e.printStackTrace();
}
    }


    /**
     * Private class starting new Thread for the stationData object to create
     * It downloads necessary information in the background of user's activity
     */

    private void runNecessaryThreads(){

        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,startLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,finalLocationListener);
            stationsDataThread = new Thread() {

                @Override
                public void run() {
                    stationsData= new StationsData();
                }
            };

            stationsDataThread.start();
        }catch(Exception e){
            createToast("An error has occurred while running stations data thread", "Wystąpił błąd w wątku");
        }

    }

    /**
     * Method controlling text language of the interface elements
     * Is called at the start and when user clicks Language Button
     */

    private void setLanguage(){

        AutoCompleteTextView startPlace = findViewById(R.id.start_place);
        AutoCompleteTextView finalPlace = findViewById(R.id.final_place);
        Button routeButton = findViewById(R.id.button);
        EditText usernameTextView = findViewById(R.id.usernameTextField);
        EditText passwordTextView = findViewById(R.id.passwordTextField);
        Button loginBtn = findViewById(R.id.loginButton);
        TextView registerTextView = findViewById(R.id.registerTextView);

        if(language.equals("PL")){
            toolbar.setTitle(getResources().getString(R.string.ToolbarTitlePL));
            if(startPlace.getHint().equals(getResources().getString(R.string.StartPlaceTextViewENG)))
                startPlace.setHint(getResources().getString(R.string.StartPlaceTextViewPL));

            else if(startPlace.getHint().equals(getResources().getString(R.string.GPSLocationENG)))
                startPlace.setHint(getResources().getString(R.string.GPSLocationPL));

            else if(startPlace.getHint().equals(getResources().getString(R.string.FindingGPSENG)))
                startPlace.setHint(getResources().getString(R.string.FindingGPSPL));

            if(finalPlace.getHint().equals(getResources().getString(R.string.FinalPlaceTextViewENG)))
                finalPlace.setHint(getResources().getString(R.string.FinalPlaceTextViewPL));

            else if(finalPlace.getHint().equals(getResources().getString(R.string.GPSLocationENG)))
                finalPlace.setHint(getResources().getString(R.string.GPSLocationPL));

            else if(finalPlace.getHint().equals(getResources().getString(R.string.FindingGPSENG)))
                finalPlace.setHint(getResources().getString(R.string.FindingGPSPL));

            routeButton.setText(getResources().getString(R.string.FindRoutePL));
            hourTextView.setText("Godzina: " + "\n" +calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
            dateTextView.setText("Data: " + "\n" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH));
            usernameTextView.setHint(getResources().getString(R.string.UsernamePL));
            passwordTextView.setHint(getResources().getString(R.string.PasswordPL));
            loginBtn.setText(getResources().getString(R.string.LoginPL));
            registerTextView.setText(getResources().getText(R.string.RegisterPL));
        }
        else if(language.equals("ENG")){
            toolbar.setTitle(getResources().getString(R.string.ToolbarTitleENG));
            if(startPlace.getHint().equals(getResources().getString(R.string.StartPlaceTextViewPL)))
                startPlace.setHint(getResources().getString(R.string.StartPlaceTextViewENG));

            else if(startPlace.getHint().equals(getResources().getString(R.string.GPSLocationPL)))
                startPlace.setHint(getResources().getString(R.string.GPSLocationENG));

            else if(startPlace.getHint().equals(getResources().getString(R.string.FindingGPSPL)))
                startPlace.setHint(getResources().getString(R.string.FindingGPSENG));

            if(finalPlace.getHint().equals(getResources().getString(R.string.FinalPlaceTextViewPL)))
                finalPlace.setHint(getResources().getString(R.string.FinalPlaceTextViewENG));

            else if(finalPlace.getHint().equals(getResources().getString(R.string.GPSLocationPL)))
                finalPlace.setHint(getResources().getString(R.string.GPSLocationENG));

            else if(finalPlace.getHint().equals(getResources().getString(R.string.FindingGPSPL)))
                finalPlace.setHint(getResources().getString(R.string.FindingGPSENG));

            routeButton.setText(getResources().getString(R.string.FindRouteENG));
            hourTextView.setText("Hour: " + "\n" +calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
            dateTextView.setText("Date: " + "\n" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH));
            usernameTextView.setHint(getResources().getString(R.string.UsernameENG));
            passwordTextView.setHint(getResources().getString(R.string.PasswordENG));
            loginBtn.setText(getResources().getString(R.string.LoginENG));
            registerTextView.setText(getResources().getText(R.string.RegisterENG));
        }
    }


    /**
     * Sets the appearance and functionality of a toolbar seen at the top of the main screen
     *  - the language button functionality is defined here
     *
     */

    private void setToolbarAction(){

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(getResources().getString(R.string.ToolbarTitlePL));

        setSupportActionBar(toolbar);
        final Button languageButton = new Button(this);
        languageButton.setText(getResources().getString(R.string.LanguagePolski));
        languageButton.setBackground(getResources().getDrawable(R.drawable.button_design));
        languageButton.setTextColor(getResources().getColor(R.color.blueToolbar));
        Toolbar.LayoutParams layoutParams=new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.END;
        languageButton.setLayoutParams(layoutParams);

        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(language.equals("PL")){
                    language="ENG";
                    languageButton.setText(getResources().getString(R.string.LanguageEnglish));
                    setLanguage();}
                else if(language.equals("ENG")){
                    language="PL";
                    languageButton.setText(getResources().getString(R.string.LanguagePolski));
                    setLanguage();

                }

            }
        });
        toolbar.addView(languageButton);

    }

    /**
     * Private method implementing Autocomplete TextViews setting the start and final station
     */

    private void setAutoComplete() {

        startTextView.setHint(getResources().getString(R.string.StartPlaceTextViewPL));
        finalTextView.setHint(getResources().getString(R.string.FinalPlaceTextViewPL));

        LatLngBounds elkLatLngBounds = new LatLngBounds(new LatLng(53.794524044936516, 22.316128516468325),
                new LatLng(53.84908089423052, 22.39721562400564));

        GeoDataClient mGeoDataClient = Places.getGeoDataClient(this);
              AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("PL").build();
              mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGeoDataClient, elkLatLngBounds, filter);


        startTextView.setAdapter(mPlaceAutoCompleteAdapter);

        startTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    startPlace = mPlaceAutoCompleteAdapter.getItem(i).getPrimaryText(null).toString();
                    startPlace = startPlace + ", Ełk";
                    if(language.equals("PL")) startTextView.setHint(getResources().getString(R.string.StartPlaceTextViewPL));
                    else if(language.equals("ENG")) startTextView.setHint(getResources().getString(R.string.StartPlaceTextViewENG));



                    startPlaceButton.setImageResource(R.drawable.clear);
                    startPlaceButtonGPS = false;

                    if(mLocationManager!=null) mLocationManager.removeUpdates(startLocationListener);
                    places.findStartCoordinates(startPlace);
                    closeKeyboard();
                } catch (NullPointerException e) {
                    createToast("An error has occurred on start place text View", "Wystąpił błąd w początkowym polu");
                    closeKeyboard();
                }
            }
        });

        finalTextView.setAdapter(mPlaceAutoCompleteAdapter);
        finalTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    finalPlace = mPlaceAutoCompleteAdapter.getItem(i).getPrimaryText(null).toString();
                    finalPlace = finalPlace + ", Ełk";

                    finalPlaceButton.setImageResource(R.drawable.clear);
                    finalPlaceButtonGPS = false;

                    if(mLocationManager!=null) mLocationManager.removeUpdates(finalLocationListener);
                    places.findFinalCoordinates(finalPlace);
                    closeKeyboard();
                } catch (NullPointerException e) {
                    createToast("An error has occurred on final place text View", "Wystąpił bląd w koncowym polu tekstowym");
                    closeKeyboard();
                }
            }
        });
    }


    /**
     * Private method implementing dateTextView and hourTextView
     * It creates AlertDialogs UI in order to set time and date of the user's departure
     */

    private void setClickableInterface(){


        startPlaceButton = findViewById(R.id.startPlaceButton);
        finalPlaceButton = findViewById(R.id.finalPlaceButton);


        startPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startPlaceButtonGPS){
                    imageButtonGPSAction(startTextView, startLocationListener);

                }else{
                    startPlaceButton.setImageResource(R.drawable.gps);
                    startPlaceButtonGPS = true;
                    startTextView.setText("");
                    places.resetStartData();

                }
            }
        });

        finalPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalPlaceButtonGPS){
                    imageButtonGPSAction(finalTextView,finalLocationListener);

                }else{
                    finalPlaceButton.setImageResource(R.drawable.gps);
                    finalPlaceButtonGPS = true;
                    finalTextView.setText("");
                    places.resetFinalData();
                }
            }
        });


        calendar = Calendar.getInstance();
        MyArrayListOfData = new ArrayList<>();



        dateTextView = findViewById(R.id.date);
        hourTextView = findViewById(R.id.hour);

        if(language.equals("PL")){
            dateTextView.setText("Data: " + "\n" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH));
            hourTextView.setText("Godzina: " + "\n" +calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));}
        else if(language.equals("ENG")) {
            dateTextView.setText("Date: " + "\n" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH));
            hourTextView.setText("Hour: " + "\n" +calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));}



        hourTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder hourDialog = new AlertDialog.Builder(MainActivity.this);
                View hourView = getLayoutInflater().inflate(R.layout.time_set,null);
                        if(language.equals("PL")) hourDialog.setTitle(getResources().getString(R.string.SetDepartureTimePL));
                        else if(language.equals("ENG")) hourDialog.setTitle(getResources().getString(R.string.SetDepartureTimeENG));

                final Spinner hourSpinner = hourView.findViewById(R.id.hourSpinner);
                    String spinnerHours = "";
                    spinnerHours += calendar.get(Calendar.HOUR_OF_DAY);
                        hourSpinner.setSelection(((ArrayAdapter) hourSpinner.getAdapter()).getPosition(spinnerHours));
                final Spinner minuteSpinner =  hourView.findViewById(R.id.minuteSpinner);
                    String spinnerMinutes = "";
                    spinnerMinutes += calendar.get(Calendar.MINUTE);
                       minuteSpinner.setSelection(((ArrayAdapter) minuteSpinner.getAdapter()).getPosition(spinnerMinutes));

                    String dismiss="Dismiss";
                    if(language.equals("PL")) dismiss = "Odrzuć";
                    else if(language.equals("ENG")) dismiss = "Dismiss";

                hourDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int hour = Integer.parseInt(hourSpinner.getSelectedItem().toString());
                        int minutes = Integer.parseInt(minuteSpinner.getSelectedItem().toString());
                               calendar.set(Calendar.HOUR_OF_DAY,hour);
                               calendar.set(Calendar.MINUTE,minutes);
                                if(language.equals("PL")) hourTextView.setText("Godzina: " + "\n" +calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                                else if(language.equals("ENG")) hourTextView.setText("Hour: " + "\n" +calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                        dialogInterface.dismiss();
                    }
                });

                hourDialog.setNegativeButton(dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                hourDialog.setView(hourView);
                final AlertDialog dialog = hourDialog.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blueToolbar));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blueToolbar));
                    }
                });
                      dialog.show();
            }
        });


        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dateDialog = new AlertDialog.Builder(MainActivity.this);
                View dateView = getLayoutInflater().inflate(R.layout.date_set,null);
                           if(language.equals("PL"))  dateDialog.setTitle(getResources().getString(R.string.SetDepartureDatePL));
                           else if(language.equals("ENG")) dateDialog.setTitle(getResources().getString(R.string.SetDepartureDateENG));

                final CalendarView calendarView =  dateView.findViewById(R.id.calendarView);
                       final int year = calendar.get(Calendar.YEAR);
                       final int month = calendar.get(Calendar.MONTH);
                       final int day = calendar.get(Calendar.DAY_OF_MONTH);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                           calendar.set(Calendar.YEAR,i);
                           calendar.set(Calendar.MONTH,i1);
                           calendar.set(Calendar.DAY_OF_MONTH,i2);
                    }
                });


                dateDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(language.equals("PL"))dateTextView.setText("Data: " + "\n" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH));
                        else if(language.equals("ENG")) dateTextView.setText("Date: " + "\n" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH));

                        dialogInterface.dismiss();
                    }
                });

                String dismiss="Dismiss";
                if(language.equals("PL")) dismiss = "Odrzuć";
                else if(language.equals("ENG")) dismiss = "Dismiss";

                dateDialog.setNegativeButton(dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        dialogInterface.dismiss();
                    }
                });
               dateDialog.setView(dateView);
               final AlertDialog dialog =dateDialog.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blueToolbar));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blueToolbar));
                    }
                });

                    dialog.show();
            }
        });
         }


    /**
     * Method responsible for launching GPS location finding when user clicks on the "GPS Location Button"
     * It finds latest user's location coordinates and updates them in case of change
     * @param textView
     * @param listener
     */
    private void imageButtonGPSAction(AutoCompleteTextView textView,LocationListener listener){

            // if(language.equals("ENG"))  textView.setHint(getResources().getString(R.string.FindingGPSENG));
           //  if(language.equals("PL")) textView.setHint(getResources().getString(R.string.FindingGPSPL));
             if(language.equals("PL")) textView.setHint(getResources().getString(R.string.GPSLocationPL));
             else if(language.equals("ENG")) textView.setHint(getResources().getString(R.string.GPSLocationENG));


             mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,listener);
             if(mLocationManager!=null){

                 Location location = mLocationManager.getLastKnownLocation(mLocationManager.NETWORK_PROVIDER);
                 if(location!=null){
                    if(textView==startTextView) places.setStartData(location);
                        else if(textView==finalTextView) places.setFinalData(location);
                 }
             }else{
                 if(language.equals("PL")) textView.setHint(getResources().getString(R.string.StartPlaceTextViewPL));
                 else if(language.equals("ENG")) textView.setHint(getResources().getString(R.string.StartPlaceTextViewENG));

             }
         }


    /**
     * Method responsible for implementing functionality for login and register buttons in the login interface seen in the bottom of the screen
     */
    private void setLoginInterface(){
        LinearLayout loginPanel = findViewById(R.id.loginPanel);
        LinearLayout loggedInPanel = findViewById(R.id.loggedInPanel);
            final EditText usernameTextView = findViewById(R.id.usernameTextField);
            final EditText passwordTextView = findViewById(R.id.passwordTextField);
            Button loginBtn = findViewById(R.id.loginButton);
            TextView registerTextView = findViewById(R.id.registerTextView);
            final Intent registerScene = new Intent(this,RegisterActivity.class);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(usernameTextView.getText().toString().equals("")||passwordTextView.getText().toString().equals("")){
                        createToast(getResources().getString(R.string.FieldNotFilledErrENG),getResources().getString(R.string.FieldNotFilledErrPL));
                    }

                }
            });

            registerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    registerScene.putExtra("Language",language);
                    startActivity(registerScene);


                }
            });

         }


    /**
     * Private method parsing incoming URL and deriving necessary parametres for RouteData objects
     * @param urlToParse
     */
    private void parser(String urlToParse){

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        MyArrayListOfData.clear();

        List<String> routeData = new ArrayList<>();
        String value;


        try{
            NodeList dataParsed = (NodeList) xPath.evaluate("/html/body/div[2]/div[2]/div[1]/table/tbody/tr",new InputSource(new StringReader(urlToParse)) ,  XPathConstants.NODESET);
            for (int i = 0; i < dataParsed.getLength(); i++) {

               Node element = dataParsed.item(i);
                element.getParentNode().removeChild(element);

                /* Downloading data*/

                /* Number of the line */
                value = xPath.evaluate("td[1]/a",element);
                    routeData.add(value);

                /* Name of the line (path) */
                value = xPath.evaluate("td[2]/a",element);
                    routeData.add(value);

                /* Number of the route */
                value = xPath.evaluate("td[3]/a/@href",element);
                value = value.replace("/przejazd/", "");
                    routeData.add(value);

                /* Hour of departure */
                value = xPath.evaluate("td[3]/a",element);
                    routeData.add(value);

                /* Number of the start station */
                value = xPath.evaluate("td[4]/a/@href",element);
                value = value.replace("/przystanek/", "");
                    routeData.add(value);


                /* Distance to the start station */
                value = xPath.evaluate("td[4]/small",element);
                if(!value.equals("")){
                    routeData.add(value);}
                else {
                    routeData.add("");
                }

                /* Hour of arrival */
                value = xPath.evaluate("td[5]/a",element);
                    routeData.add(value);


                /* Number of the final station */
                value = xPath.evaluate("td[6]/a/@href",element);
                value = value.replace("/przystanek/", "");
                    routeData.add(value);


                /* Distance to the final station */
                value = xPath.evaluate("td[6]/small",element);
                if(!value.equals("")){
                    routeData.add(value);}
                else {
                    routeData.add(""); }



                if(routeData.size()>0){
                    MyArrayListOfData.add(new ArrayList<>(routeData)); }

                routeData.clear();
            }


        }catch(Exception e){
            e.printStackTrace();
            createToast("An error has occurred while parsing url", "Wystąpił błąd podczas parsowania URL");
        }

    }


    /**
     * Public method responsible for making URL request to the server in the moment
     * of clicking the "Find Route" button
     * It also start next "ResultsListActivity" Intent and puts extra data:
     * - number of Route results
     * - all created RouteData objects
     * - created object of StationData class
     * - coordinates of user's start and final destination
     * @param view
     */

    public void onBtnClick(View view) {


        if (!places.checkData())
        {
            createToast(getResources().getString(R.string.FieldNotFilledErrENG),getResources().getString(R.string.FieldNotFilledErrPL));
        } else{


        if(stationsDataThread.isAlive()){
            createToast("Data is still processing, please wait a second :)", "Dane są wciąż wczytywane, proszę " +
                    "chwilę poczekać :)");
        }else {

            try {

                mLocationManager.removeUpdates(startLocationListener);
                mLocationManager.removeUpdates(finalLocationListener);

                GetUrlContentTask getUrl = new GetUrlContentTask();


                getUrl.makeURL(places.getStartLatitude(), places.getStartLongitude(), places.getFinalLatitude(), places.getFinalLongitude(),
                        calendar.getTime());


                String url = getUrl.doInBackground();

                    url = url.replace("<!doctype html>", "");
                    url = url.replace("<link href=\"/css/bootstrap.css\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >", "");
                    url = url.replace("<link href=\"/t/elk/style.css\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >\t\t", "");
                    url = url.replace("></dd>", "/></dd>");
                parser(url);


                Intent resultListScene = new Intent(this, ResultsListActivity.class);

                    resultListScene.putExtra("ResultsNumber", MyArrayListOfData.size());


                for (int i = 0; i < MyArrayListOfData.size(); i++) {
                    String name = "RouteData" + i;
                    resultListScene.putExtra(name, MyArrayListOfData.get(i));
                }


                    resultListScene.putExtra("StationsData", stationsData);
                final String usersPlacesCoordinates = places.getStartLatitude() + "," + places.getStartLongitude() + ";" +
                        places.getFinalLatitude() + "," + places.getFinalLongitude();
                    resultListScene.putExtra("UsersPlacesCoordinates", usersPlacesCoordinates);
                    resultListScene.putExtra("Language", language);
                startActivity(resultListScene);


            }

            catch (IllegalArgumentException e) {
                createToast(getResources().getString(R.string.FieldNotFilledErrENG), getResources().getString(R.string.FieldNotFilledErrPL));
            } catch (Exception e) {
                createToast(getResources().getString(R.string.InternetConnectionProblemErrENG), getResources().getString(R.string.InternetConnectionProblemErrPL) );
                e.printStackTrace();
            }
        } }

    }

    private void closeKeyboard(){

        View mview = this.getCurrentFocus();
        if(mview != null){

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mview.getWindowToken(),0);
        }
    }


    private void createToast(String resultENG, String resultPL){

       if(language.equals("PL")) Toast.makeText(getApplicationContext(), resultPL,
                Toast.LENGTH_LONG).show();
       else if(language.equals("ENG")) Toast.makeText(getApplicationContext(), resultENG,
               Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        createToast(getResources().getString(R.string.InternetConnectionErrENG), getResources().getString(R.string.InternetConnectionErrPL));
    }
}
