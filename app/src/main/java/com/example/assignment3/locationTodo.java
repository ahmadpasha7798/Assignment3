package com.example.assignment3;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class locationTodo extends Todo_item{
    LatLng location;
    int distance;

    public locationTodo(){
        location=new LatLng(0,0);
    }

}
