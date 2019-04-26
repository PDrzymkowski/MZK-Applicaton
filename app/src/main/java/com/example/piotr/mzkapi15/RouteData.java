package com.example.piotr.mzkapi15;

import java.io.Serializable;
import java.util.List;

public class RouteData implements Serializable{

    private int busLine, departureStation, arrivalStation, routeNumber;
    private String busLineDirection, departureTime, arrivalTime, distanceToStartStation, distanceToFinalStation;

    public RouteData(List<String> dataList){


        this.busLine = Integer.parseInt(dataList.get(0));

        this.busLineDirection = dataList.get(1);

        this.routeNumber = Integer.parseInt(dataList.get(2));

        this.departureTime = dataList.get(3);

        this.departureStation = Integer.parseInt(dataList.get(4));

        this.distanceToStartStation = dataList.get(5);

        this.arrivalTime =dataList.get(6);

        this.arrivalStation = Integer.parseInt(dataList.get(7));

        this.distanceToFinalStation = dataList.get(8);

    }

    public String getData(){

        return( "Line: " + busLine + "\n" + " Direction: " + busLineDirection + "\n" + " Route: " + routeNumber + "\n" +
                " Departure: " + departureTime + "\n" + " Start: " + departureStation + "\n" + " Distance: " + distanceToStartStation +
                "\n" + " Arrival: " + arrivalTime + "\n" + " Final: " + arrivalStation + "\n" + " Distnace: " + distanceToFinalStation );

    }

    public String getDirection(){

        return busLineDirection;
    }

    public String getTimeENG(){

        return ("Departure: " + departureTime + "    Arrival: " + arrivalTime);

    }

    public String getTimePL(){

        return ("Odjazd: " + departureTime + "    Przyjazd: " + arrivalTime);

    }

    public String getBusLine(){

        return ("Nr: " + busLine);
    }



    public int getRouteNumber(){

        return routeNumber;
    }


    public String getArrivalTime(){

        return ("("+arrivalTime+")");
    }

    public String getDepartureTime(){

        return("("+departureTime+")");
    }

    public int getDepartureStation(){

        return departureStation;
    }

    public int getArrivalStation(){

        return arrivalStation;
    }

    public int getDepartureMinute(){

        String[] departure = departureTime.split(":");

        return(Integer.parseInt(departure[1]));
    }

    public int getDepartureHour(){

        String[] departure = departureTime.split(":");

        return(Integer.parseInt(departure[0]));
    }

    public int getArrivalMinute(){

        String[] arrival = arrivalTime.split(":");

        return(Integer.parseInt(arrival[1]));
    }

    public int getArrivalHour(){

        String[] arrival = arrivalTime.split(":");

        return(Integer.parseInt(arrival[0]));
    }

    public double getDistanceToStartStation() {

        if (!distanceToStartStation.equals("") ) {
            System.out.println("!!!!!   " + distanceToStartStation);
            System.out.println("zzzzz   " + distanceToFinalStation);
            String value = distanceToStartStation.replace("(","");
            value = value.replace("km)","");

            return Double.parseDouble(value);
        }else{
            return 0.0;
        }

    }

    public double getDistanceToFinalStation() {
        if (!distanceToFinalStation.equals("") ) {
            System.out.println("!!!!!   " + distanceToStartStation);
            System.out.println("zzzz  " + distanceToFinalStation);
            String value = distanceToFinalStation.replace("(", "");
            value = value.replace("km)", "");

            return Double.parseDouble(value);
        }else{
            return 0.0;
        }
    }
}

