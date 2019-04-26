package com.example.piotr.mzkapi15;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;

public class PlaceCoordinates {


    private Context context;
    private double startLongitude, finalLongitude;
    private double startLatitude, finalLatitude;



    public PlaceCoordinates(Context context){
        super();
        this.context = context;

        startLatitude = 0;
        startLatitude = 0;
        finalLatitude = 0;
        finalLongitude = 0;
    }

    public void findStartCoordinates(String locationName){


        Geocoder geocoder = new Geocoder(context);
        Address typedAddress;
        try {
            List<Address> startAddress= geocoder.getFromLocationName(locationName,1);
            typedAddress = startAddress.get(0);

            setStartData(typedAddress);
            if(startAddress != null && !startAddress.isEmpty()){

                System.err.println("An error has occurred while checking start address list");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findFinalCoordinates(String locationName){


        Geocoder geocoder = new Geocoder(context);
        Address typedAddress;
        try {
            List<Address> finalAddress = geocoder.getFromLocationName(locationName,1);
            typedAddress = finalAddress.get(0);

            setFinalData(typedAddress);
            if(finalAddress != null && !finalAddress.isEmpty()){

                System.err.println("An error has occurred while checking final address list");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void resetStartData(){

        startLongitude = 0;
        startLatitude = 0;
    }

    public void resetFinalData(){

        finalLongitude = 0;
        finalLatitude = 0;
    }

    public void setStartData(Location location){

        startLongitude = location.getLongitude();
        startLatitude = location.getLatitude();
    }
    public void setFinalData(Location location){

        finalLongitude = location.getLongitude();
        finalLatitude = location.getLatitude();
    }


    private void setStartData(Address typedAddress){

        startLongitude = typedAddress.getLongitude();
        startLatitude = typedAddress.getLatitude();

    }

    private void setFinalData(Address typedAddress){

        finalLongitude = typedAddress.getLongitude();
        finalLatitude = typedAddress.getLatitude();

    }


    public double getStartLongitude(){

        return  startLongitude;
    }

    public double getStartLatitude(){

        return  startLatitude;
    }

    public double getFinalLongitude(){

        return  finalLongitude;
    }

    public double getFinalLatitude(){

        return  finalLatitude;
    }

    public boolean checkData(){

        if(startLatitude==0 || startLongitude==0 || finalLatitude==0 || finalLongitude==0) return false;
        else{
            return true;
        }
    }


}

