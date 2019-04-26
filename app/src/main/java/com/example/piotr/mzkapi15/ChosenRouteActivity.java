package com.example.piotr.mzkapi15;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class ChosenRouteActivity extends AppCompatActivity  {


    private TextView arrivalTime,departureTime,arrivalStation,departureStation, betweenStationsTextView;
    private RouteData chosenRouteData;
    private StationsData stationsData;
    private ArrayList<Integer> DataParsed;
    private String language;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_route);

        arrivalTime = findViewById(R.id.arrivalTime);
        departureTime = findViewById(R.id.departureTime);
        arrivalStation = findViewById(R.id.arrivalStation);
        departureStation = findViewById(R.id.departureStation);
        betweenStationsTextView = findViewById(R.id.betweenStations);

        chosenRouteData =(RouteData) getIntent().getSerializableExtra("ChosenRoute");
        stationsData = (StationsData) getIntent().getSerializableExtra("StationsData");
        language = (String) getIntent().getSerializableExtra("Language");

        runNecessaryThreads();


    }

    private void runNecessaryThreads(){

        try{
            Thread stationsParserThread = new Thread(){

                @Override
                public void run(){

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            betweenStationsTextView.setText(  stationsParser());
                            countTravelTime();
                        }
                    });


                }
            };
            stationsParserThread.start();

        }catch(Exception e){
            createToast("An error has occurred in stations parser Thread");
            e.printStackTrace();
        }

        try{
            Thread setInterfaceThread = new Thread(){

                @Override
                public void run(){
                  setInterface();
                }
            };
            setInterfaceThread.start();
        }catch(Exception e){
            createToast("An error has occurred in set interface Thread");
            e.printStackTrace();
        }

    }

    private void setInterface(){

        departureTime.setText(chosenRouteData.getDepartureTime());
        departureStation.setText(stationsData.getStationName(chosenRouteData.getDepartureStation()));
        arrivalTime.setText(chosenRouteData.getArrivalTime());
        arrivalStation.setText(stationsData.getStationName(chosenRouteData.getArrivalStation()));

        TextView departureText = findViewById(R.id.textView);
        TextView arrivalText = findViewById(R.id.textView7);
        Button showOnMapButton = findViewById(R.id.showOnMapBtn);
        if(language.equals("ENG")){
            departureText.setText(getResources().getText(R.string.DepartureStationENG));
            arrivalText.setText(getResources().getText(R.string.ArrivalStationENG));
            showOnMapButton.setText(getResources().getString(R.string.ShowOnMapENG));

        }else if(language.equals("PL")){
            departureText.setText(getResources().getText(R.string.DepartureStationPL));
            arrivalText.setText(getResources().getText(R.string.ArrivalStationPL));
            showOnMapButton.setText(getResources().getString(R.string.ShowOnMapPL));
        }

        try{
            setWalkingInterface();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setWalkingInterface(){

        TextView fromStartTextView = findViewById(R.id.fromStartTextView);
        TextView toFinishTextView = findViewById(R.id.toFinishTextView);
        TextView fromStartTimeTextView = findViewById(R.id.fromStartTimeTextView);
        TextView toFinishTimeTextView = findViewById(R.id.toFinishTimeTextView);

        countWalkingTime(fromStartTimeTextView,toFinishTimeTextView);

        if(language.equals("PL")){
            fromStartTextView.setText(getResources().getString(R.string.FromStartWalkPL));
            toFinishTextView.setText(getResources().getString(R.string.ToFinishWalkPL));
        }else if(language.equals("ENG")){
            fromStartTextView.setText(getResources().getString(R.string.FromStartWalkENG));
            toFinishTextView.setText(getResources().getString(R.string.ToFinishWalkENG));
        }

    }


    private String stationsParser(){

        String url = doInBackground();
        url = url.replace("<!doctype html>", "");
        url = url.replace("<link href=\"/css/bootstrap.css\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >", "");
        url = url.replace("<link href=\"/t/elk/style.css\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >\t\t", "");
        url = url.replace("></dd>", "/></dd>");
        url = url.replace("&", "");
        url = url.replace("]", "");

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        DataParsed = new ArrayList<>();
        String value;
        int stationNumber;
        boolean search = false;


        try{

            NodeList stations = (NodeList) xPath.evaluate("/html/body/div[2]/div[2]/div[1]/table/tbody/tr",new InputSource(new StringReader(url)) ,  XPathConstants.NODESET);

            for (int i = 0; i < stations.getLength(); i++) {

                Node element =  stations.item(i);
                element.getParentNode().removeChild(element);

                value = xPath.evaluate("td[contains(@class, 'text-muted small text-right')]", element);
                    stationNumber = Integer.parseInt(value);


                if(stationNumber == chosenRouteData.getArrivalStation()){
                    search = false;
                }

                if(search){
                    if(!value.equals(""))
                    DataParsed.add(stationNumber);
                }

                if(stationNumber == chosenRouteData.getDepartureStation()){
                    search = true;
                }

            }


            }catch(Exception e){
                e.printStackTrace();
            }
            StringBuilder content = new StringBuilder("\n");
            TextView betweenStationsTitle = findViewById(R.id.betweenStationsTitle);

            if(language.equals("PL")) betweenStationsTitle.setText( getResources().getString(R.string.PassStationsPL));
            else if(language.equals("ENG")) betweenStationsTitle.setText( getResources().getString(R.string.PassStationsENG));


            for(int n = 0; n<DataParsed.size();n++){


                if(n == 0) {content.append("-" + stationsData.getStationName(DataParsed.get(n)));}
                else
                    content.append( "\n" + "-" + stationsData.getStationName(DataParsed.get(n))); ;

            }

            value = new String(content);
            DataParsed.add(0,chosenRouteData.getDepartureStation());
            DataParsed.add(chosenRouteData.getArrivalStation());

            return value;
    }


    private void countTravelTime(){

        TextView travelTime = findViewById(R.id.travelTime);

        int resultHour,resultMinute, departureHour, departureMinute, arrivalHour, arrivalMinute;
        departureHour = chosenRouteData.getDepartureHour();
        departureMinute = chosenRouteData.getDepartureMinute();
        arrivalHour = chosenRouteData.getArrivalHour();
        arrivalMinute = chosenRouteData.getArrivalMinute();

        resultHour = arrivalHour - departureHour;
        if( (arrivalMinute - departureMinute) <0 ){

            resultMinute = 60 - (departureMinute - arrivalMinute);
            resultHour -=1;
        }else{
            resultMinute = arrivalMinute - departureMinute;
        }

        if(resultHour==0){
             travelTime.setText(" ~" + resultMinute+"min");

        }else{
            if(language.equals("PL")) travelTime.setText(" ~" +resultHour + "godz  " + resultMinute+"min");
            else if(language.equals("ENG")) travelTime.setText(" ~" +resultHour + "h  " + resultMinute+"min");
        }
    }

    private void countWalkingTime(TextView fromStartTimeTextView, TextView toFinishTimeTextView){

        int walkFromStartMinutes, walkToFinishMinutes;

        walkFromStartMinutes = (int) (chosenRouteData.getDistanceToStartStation()/0.083);
        walkToFinishMinutes = (int)(chosenRouteData.getDistanceToFinalStation()/0.083);

        if(walkFromStartMinutes<1){
            fromStartTimeTextView.setText(" <1min");
        }else{
            fromStartTimeTextView.setText(" ~" + walkFromStartMinutes + "min");
        }

        if(walkToFinishMinutes<1){
            toFinishTimeTextView.setText(" <1min");
        }else{
            toFinishTimeTextView.setText(" ~" + walkToFinishMinutes + "min");
        }

    }


    protected String doInBackground(String... args) {

        try {

            URL url = new URL( "http://mzk.elk.pl/przejazd/" + chosenRouteData.getRouteNumber());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder ("");
            String line;
            while ((line = rd.readLine()) != null) {
                content.append(line + "\n");
            }
            return (new String(content));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void onShowOnMapBtnClick(View view){

        Intent mapScene = new Intent(this, MyOSMapsActivity.class);
        boolean isRoute = (boolean) getIntent().getSerializableExtra("isRoute");
        String usersPlacesCoordinates = (String) getIntent().getSerializableExtra("UsersPlacesCoordinates");

            mapScene.putExtra("UsersPlacesCoordinates", usersPlacesCoordinates);
            mapScene.putIntegerArrayListExtra("ChosenRouteStations", DataParsed);
            mapScene.putExtra("StationsData", stationsData);
            mapScene.putExtra("Language", language);
            mapScene.putExtra("isRoute", isRoute);

        startActivity(mapScene);

    }

    private void createToast(String result){

        Toast.makeText(getApplicationContext(), result,
                Toast.LENGTH_LONG).show();

    }

}
