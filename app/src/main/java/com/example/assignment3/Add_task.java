package com.example.assignment3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class Add_task extends AppCompatActivity {

    locationTodo obj=new locationTodo();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            {
                obj.location=new LatLng(data.getDoubleExtra("Latitude",0),data.getDoubleExtra("Longitude",0));
                obj.distance=data.getIntExtra("Distance",0);
                obj.locationaware=true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        obj.locationaware=false;
        Button btn=findViewById(R.id.add_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                EditText t = findViewById(R.id.Title_box);
                EditText d = findViewById(R.id.des_box);
                if (!t.getText().toString().isEmpty() && !d.getText().toString().isEmpty()) {
                    intent.putExtra("Title", t.getText().toString());
                    intent.putExtra("Description", d.getText().toString());
                    intent.putExtra("locationaware",obj.locationaware);
                    intent.putExtra("Latitude", obj.location.latitude);
                    intent.putExtra("Longitude", obj.location.longitude);
                    intent.putExtra("Distance",obj.distance);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(Add_task.this, "Fill all Fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button location=findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Add_task.this,MapsActivity.class);
                startActivityForResult(intent,123);
            }
        });

    }
}