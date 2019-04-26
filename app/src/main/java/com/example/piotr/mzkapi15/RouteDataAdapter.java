package com.example.piotr.mzkapi15;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;



public class RouteDataAdapter extends BaseAdapter{


    private Context context;
    private List<RouteData> routeDataList;
    private String language, travelTime;

    public RouteDataAdapter(List<RouteData> routeDataList, Context context, String language){

        this.context = context;
        this.routeDataList = routeDataList;
        this.language = language;
    }




    @Override
    public int getCount() {
        return routeDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return routeDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

      //  View mView = View.inflate(context, R.layout.results_list_row,null);


        View mView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.results_list_row, viewGroup, false);

        TextView direction =  mView.findViewById(R.id.direction);
        TextView busLine =  mView.findViewById(R.id.busLine);
        TextView time =  mView.findViewById(R.id.departure);
        TextView travelTime = mView.findViewById(R.id.travelBusTime);

        RouteData routeData = routeDataList.get(i);

        direction.setText(routeData.getDirection());
        busLine.setText(routeData.getBusLine());

        if(language.equals("PL")){
            time.setText(routeData.getTimePL());
        }else if(language.equals("ENG")) {
            time.setText(routeData.getTimeENG());
        }

        try {
            travelTime.setText(countTravelTime(routeData));
        }catch (Exception e){
            e.printStackTrace();
        }
        mView.setTag(routeDataList.get(i).getBusLine());
        return mView;
    }


    private String countTravelTime(RouteData routeData){



        int resultHour,resultMinute, departureHour, departureMinute, arrivalHour, arrivalMinute;
        int walkFromStartMinutes, walkToFinishMinutes;
            departureHour = routeData.getDepartureHour();
            departureMinute = routeData.getDepartureMinute();
            arrivalHour = routeData.getArrivalHour();
            arrivalMinute = routeData.getArrivalMinute();


        if(( walkFromStartMinutes = (int) (routeData.getDistanceToStartStation()/0.083) ) != 0 ) {
            departureMinute -= walkFromStartMinutes;
            while (departureMinute < 0) {
                departureMinute = 60 + departureMinute;
                departureHour -= 1;
            }
        }

        if( (walkToFinishMinutes = (int)(routeData.getDistanceToFinalStation()/0.083) ) != 0 ) {
            arrivalMinute += walkToFinishMinutes;
            while (arrivalMinute >= 60) {
                arrivalMinute = arrivalMinute - 60;
                arrivalHour += 1;
            }
        }

        resultHour = arrivalHour - departureHour;
        if( (arrivalMinute - departureMinute) <0 ){

            resultMinute = 60 - (departureMinute - arrivalMinute);
            resultHour -=1;
        }else{
            resultMinute = arrivalMinute - departureMinute;
        }



        if(resultHour==0){
           return(" ~" + resultMinute+"min");
        //    else if(language.equals("ENG")) return("Travel time: " + resultMinute+"min");

        }else{
            if(language.equals("PL")) return(" ~" +resultHour + "godz  " + resultMinute+"min");
            else if(language.equals("ENG")) return(" ~"  +resultHour + "h  " + resultMinute+"min");
        }
        return null;
    }



}

