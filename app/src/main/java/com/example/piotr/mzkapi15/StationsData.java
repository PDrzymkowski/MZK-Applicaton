package com.example.piotr.mzkapi15;


import android.os.AsyncTask;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationsData extends AsyncTask<String, Integer, String> implements Serializable{


   private Map<Integer, List<String>> stationsData;

    public StationsData(){

        stationsData = new HashMap<>();


        String url =  doInBackground();


        url = url.replace("<!doctype html>", "");
        url = url.replace("<link href=\"/css/bootstrap.css\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >", "");
        url = url.replace("<link href=\"/t/elk/style.css\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >\t\t", "");
        url = url.replace("></dd>", "/></dd>");
        url = url.replace("&", "");
        url = url.replace("]", "");



        try {
            String urlWithHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + url;

            byte[] bytes = urlWithHeader.getBytes("UTF-8");
            VTDGen vg = new VTDGen();
            vg.setDoc(bytes);
            vg.parse(true);
            VTDNav vn = vg.getNav();
            VTDParser(vn);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void VTDParser(VTDNav vn) {


        AutoPilot ap = new AutoPilot();
        List<String> Name = new ArrayList<>();
        List<String> Coordinates = new ArrayList<>();
        List<Integer> StationsNumber = new ArrayList<>();
        List<String> DataParsed = new ArrayList<>();

        try {
            ap.selectXPath("/html/body/div[2]/div[2]/div[1]/table/tbody/tr");


            ap.bind(vn);


            VTDGen fragmentVG = new VTDGen();
            VTDNav fragmentVN;

            while ( ap.evalXPath() != -1) {

                long l = vn.getContentFragment();

                String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n" +
                        "<body>"+
                        vn.toString((int)l,(int)(l>>32)) +
                        "</body>";



                try {

                    byte[] bytes = xmlFragment.getBytes("UTF-8");
                    fragmentVG.setDoc(bytes);
                    fragmentVG.parse(true);
                    fragmentVN = fragmentVG.getNav();


                    Name.add(fragmentVN.toNormalizedXPathString(6));
                    Coordinates.add(fragmentVN.toNormalizedXPathString(14));

                    String stationNumb;
                    stationNumb = fragmentVN.toString(11);
                    stationNumb = stationNumb.replace("/przystanek/", "");
                    StationsNumber.add(Integer.parseInt(stationNumb));




                }catch(Exception e){
                    e.printStackTrace();
                }




            }
            ap.resetXPath();


            for (int n = 0; n < Name.size(); n++) {

                DataParsed.add(Name.get(n));
                DataParsed.add(Coordinates.get(n));
                stationsData.put(StationsNumber.get(n), new ArrayList<>(DataParsed));
                DataParsed.clear();

            }




        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    protected String doInBackground(String... args) {

        try {

            URL url = new URL( "http://mzk.elk.pl/przystanki");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder text = new StringBuilder("");
            String line;
            while ((line = rd.readLine()) != null) {
                text.append(line + "\n");
            }

            return (new String(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public String getStationName(int stationNumber){

        return (stationsData.get(stationNumber).get(0));
    }


    public double getStationLatitude(int stationNumber){

        String[] coordinates = stationsData.get(stationNumber).get(1).split(", ");
        String latitude = coordinates[0];
        return (Double.parseDouble(latitude));
    }


    public double getStationLongitude(int stationNumber){

        String[] coordinates = stationsData.get(stationNumber).get(1).split(", ");
        String longitude = coordinates[1];
        return (Double.parseDouble(longitude));
    }




    protected void onProgressUpdate(Integer... progress) { }

    protected void onPostExecute(String result) { }




}

