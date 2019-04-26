package com.example.piotr.mzkapi15;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Second important activity displaying the results of the route the user has set
 * It displays a number of bus connection between two coordinates as well as an optional walking route
 * User can choose any displayed connections by clicking on them, next activity will be shown with information of the route
 * When choosing walking path user is directed to the map showing the shortest and most suitable path to the final destination
 */


public class ResultsListActivity extends AppCompatActivity{

    private List<RouteData> MyRouteData = new ArrayList<>();
    private ListView listView;

    private String language, usersPlacesCoordinates, walkingTime;
    private boolean isRoute;



    protected void onCreate(Bundle savedInstanceState) {

        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);



            int results = (int) getIntent().getSerializableExtra("ResultsNumber");
            language = (String) getIntent().getSerializableExtra("Language");
            countWalkingTime(getTravelTime(getJSONData()));

            for (int i = 0; i < results; i++) {

                String name = "RouteData" + i;

                ArrayList<String> list = (ArrayList<String>) getIntent().getSerializableExtra(name);
                MyRouteData.add(new RouteData(new ArrayList<>(list)));
            }

            setWalkingInterface();

            if (MyRouteData.size() == 0) {
                createToast(getResources().getString(R.string.noRoutePL), getResources().getString(R.string.noRouteENG));

            } else {

                listView = findViewById(R.id.my_result_list);
                isRoute = true;
                RouteDataAdapter adapter = new RouteDataAdapter(MyRouteData, getApplicationContext(), language);
                listView.setAdapter(adapter);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


 //   /**
  //   * Method running a thread responsible for counting walking time using 3 functions
  //   * Inside this thread walkTimeTextView text value is also updated
  //   */
     /*
    private void runNecessaryThreads(){

        try{
            Thread countTravelTimeThread = new Thread(){


                @Override
                public void run() {
                   countWalkingTime(getTravelTime(getJSONData()));
                }
            };
                countTravelTimeThread.start();

        }catch(Exception e){
            e.printStackTrace();
        }
    } */

    /**
     * Method responsible for displaying walking button interface
     * It sets textView values depending on chosen language and launches another method when button is pressed
     */

    private void setWalkingInterface(){

        LinearLayout walkingPanel = findViewById(R.id.walkingPanel);
        TextView walkTextView = findViewById(R.id.walkTextView);

        if(language.equals("PL")) {
            walkTextView.setText(getResources().getString(R.string.onFootPL));
        }
        else if(language.equals("ENG")) {
            walkTextView.setText(getResources().getString(R.string.onFootENG));
        }

        walkingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWalkingActivity();
            }
        });
    }

    /**
     * Method creating another activity when user chooses to take a walking route
     * It sets "isRoute" parameter to "false" as to show another activity that walking route has to be displayed
     * It also sends necessary values to the next activity
     */

    private void startWalkingActivity(){

        Intent mapScene = new Intent(this, MyOSMapsActivity.class);
        String usersPlacesCoordinates = (String) getIntent().getSerializableExtra("UsersPlacesCoordinates");
        mapScene.putExtra("UsersPlacesCoordinates", usersPlacesCoordinates);
        isRoute = false;
        mapScene.putExtra("isRoute", isRoute);
        startActivity(mapScene);
    }


    /**
     * Method explaining what happens when user clicks on any travel option displayed
     * It starts another activity displaying chosen route data as well as sends necessary values
     * It also sets "isRoute" parameter to "true" as to convey message to another activity that a full route has to be shown
     * @param view
     */

    public void onClick(View view){

        int result = listView.getPositionForView(view);
        StationsData stationsData = (StationsData) getIntent().getSerializableExtra("StationsData");
             isRoute = true;

        Intent chosenRouteScene = new Intent(this, ChosenRouteActivity.class);
            chosenRouteScene.putExtra("Language", language);
            chosenRouteScene.putExtra("ChosenRoute", MyRouteData.get(result));
            chosenRouteScene.putExtra("StationsData", stationsData);
            chosenRouteScene.putExtra("UsersPlacesCoordinates", usersPlacesCoordinates);
            chosenRouteScene.putExtra("isRoute", isRoute);

        startActivity(chosenRouteScene);

    }

    /**
     * Method opening Htttp connection between client and server which holds data for the walking route
     * It returns the JSON file content in a form of string
     * @return
     */

    private String getJSONData(){

        HttpURLConnection connection = null;

        try{
            usersPlacesCoordinates = (String) getIntent().getSerializableExtra("UsersPlacesCoordinates");

            System.out.println("http://163.172.169.255:5000/route/v1/foot/" + coordinatesSwap(usersPlacesCoordinates));
            URL url = new URL("http://163.172.169.255:5000/route/v1/foot/" + coordinatesSwap(usersPlacesCoordinates));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder JSONResult = new StringBuilder("");
            String line;
            while( (line = reader.readLine())!=null ){
                JSONResult.append(line + "\n");
            }
                reader.close();
            connection.disconnect();
            return(JSONResult.toString());
        }catch(MalformedURLException e1){
            e1.printStackTrace();
            connection.disconnect();
            return null;
        }catch(NullPointerException e2){
            e2.printStackTrace();
            connection.disconnect();
            return null;
        } catch(Exception e){
            e.printStackTrace();
            connection.disconnect();
            return null;
        }
    }

    /**
     * Method parsing JSON file and deriving distance value between start and final place which is returned to another function
     * It takes JSON document from the server as a parameter
     * @param JSONResult
     * @return
     */

    private Double getTravelTime(String JSONResult){

        try{
                //   System.out.println(JSONResult);
            JSONObject json = new JSONObject(JSONResult);
            JSONArray results = json.getJSONArray("routes");

            for(int i=0;i<results.length();i++){
                JSONObject object = results.getJSONObject(i);

                return object.getDouble("distance");

            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Method countring walking time from start to final place
     * It updates walkTimeTextView to display the amount of minutes necessary for the walk
     * The parameter of this method is distance value derived from JSON file
     * @param distance
     */
    private void countWalkingTime(Double distance){

        int countedTime = (int)( (distance/5000)*60 );

        if(countedTime==0){
          walkingTime="  < 1min";
        //    else if(language.equals("ENG")) walkingTime="  less than a minute";
        }else {
            walkingTime = "  ~" + countedTime + "min";
        }

        TextView walkTimeTextView = findViewById(R.id.walkTimeTextView);
        walkTimeTextView.setText( walkingTime);
    }


    /**
     * Method used to swap longitude parameter with latitude of user's start and final place
     * JSON server requires swapped values in url in order to work properly
     * @param usersPlacesCoordinatesString
     * @return
     */
    private String coordinatesSwap(String usersPlacesCoordinatesString){

        String stringArray[]= new String[2];
        String stringArray_start[] = new String[2];
        String stringArray_final[] = new String[2];
        stringArray = usersPlacesCoordinatesString.split(";");

        stringArray_start = stringArray[0].split(",");
        stringArray_final = stringArray[1].split(",");

        return(stringArray_start[1] + "," + stringArray_start[0] + ";" + stringArray_final[1] + "," + stringArray_final[0]);
    }


    private void createToast(String resultENG, String resultPL){

        if(language.equals("PL")) Toast.makeText(getApplicationContext(), resultPL,
                Toast.LENGTH_LONG).show();
        else if(language.equals("ENG")) Toast.makeText(getApplicationContext(), resultENG,
                Toast.LENGTH_LONG).show();
    }

}

