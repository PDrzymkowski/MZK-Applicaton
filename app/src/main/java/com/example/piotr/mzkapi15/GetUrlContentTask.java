package com.example.piotr.mzkapi15;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {


    private URL userURL;


    protected String doInBackground(String... args) {

        if(userURL!=null) {

            try {

                URL url = userURL;
                System.out.println(url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(7000);
                connection.connect();
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder("");
                String line;
                while ((line = rd.readLine()) != null) {
                    content.append(line + "\n");
                }
                return new String(content);
            }catch(java.net.SocketTimeoutException e1) {
                e1.printStackTrace();
                return null;
            } catch (Exception e2) {
                e2.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {

    }

    public void makeURL(double startPlaceLat, double startPlaceLongtd, double finalPlaceLat, double finalPlaceLongtd, Date dateGiven){

        long unixTimeStamp = dateGiven.getTime()/1000L;
        String makeURL = "http://mzk.elk.pl/routes/search?&from=";
        makeURL = makeURL + startPlaceLat + "%2C" + startPlaceLongtd + "&to=" + finalPlaceLat + "%2C" + finalPlaceLongtd + "&date="+ unixTimeStamp;

        try {
            userURL = new URL(makeURL);

        }catch(Exception e){
            System.err.append("Something went wrong while making URL");
        }


    }


}

