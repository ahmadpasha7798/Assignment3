package com.example.assignment3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyAdapter.MyViewHolder.itemClickListener ,MyAdapter.MyViewHolder.itemLongClickListener{
    int a = 0;
    List<locationTodo> items = new ArrayList<locationTodo>();
    MyAdapter myAdapter;
    RecyclerView recyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        writeFile();
        boolean flag = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).locationaware && items.get(i).Status != "Completed") {
                flag = true;
                break;
            }
        }
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean allowed=sharedPreferences.getBoolean("notify",false);
        if (flag&allowed) {
            Intent intent = new Intent(this, BackgroundLocation.class);
            startService(intent);
        } else {
            Intent intent = new Intent(this, BackgroundLocation.class);
            stopService(intent);
        }

        super.onStop();
    }

    private void writeFile() {
        String Filename = "test.json";
        JSONArray competeArray = new JSONArray();


        try {
            for (int i = 0; i < items.size(); i++) {
                JSONObject entry = new JSONObject();
                JSONObject finalEntry = new JSONObject();
                entry.put("Title", items.get(i).Title);
                entry.put("Description", items.get(i).Description);
                entry.put("Status", items.get(i).Status);
                entry.put("locationaware", items.get(i).locationaware);
                entry.put("Latitude", items.get(i).location.latitude);
                entry.put("Longitude", items.get(i).location.longitude);
                entry.put("Distance", items.get(i).distance);
                finalEntry.put("entry", entry);

                competeArray.put(finalEntry);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String JsonEntry = competeArray.toString();
            FileOutputStream fos = openFileOutput(Filename, Context.MODE_PRIVATE);
            fos.write(JsonEntry.getBytes());
            fos.close();
            Toast.makeText(this, "Entry Created", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fl_btn = findViewById(R.id.fl_btn);
        TextView info=findViewById(R.id.info);
        info.setText("To Change Status: Click the Task\nTo Delete a Task: Long Press The Task");
        fl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Add_task.class);
                startActivityForResult(intent, 0);
            }
        });
        readOutput();
        clearfile();
        myAdapter = new MyAdapter(items, this,this);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(layoutManager);


    }

    private void clearfile() {
        String Filename = "test.json";
        String clear = "";
        try {
            FileOutputStream fos = openFileOutput(Filename, MODE_PRIVATE);
            fos.write(clear.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "new File!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readOutput() {

        String Filename = "test.json";


        try {
            FileInputStream in = openFileInput(Filename);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray completedArray = new JSONArray(sb.toString());
            for (int i = 0; i < completedArray.length(); i++) {
                JSONObject dummy = completedArray.getJSONObject(i);
                JSONObject temp = dummy.getJSONObject("entry");
                locationTodo obj = new locationTodo();
                obj.Title = temp.getString("Title");
                obj.Description = temp.getString("Description");
                obj.Status = temp.getString("Status");
                obj.locationaware = temp.getBoolean("locationaware");
                obj.location = new LatLng(temp.getDouble("Latitude"), temp.getDouble("Longitude"));
                obj.distance = temp.getInt("Distance");
                items.add(obj);
            }


        } catch (FileNotFoundException e) {
            Toast.makeText(this, "New File!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            locationTodo obj = new locationTodo();
            obj.Title = data.getStringExtra("Title");
            obj.Description = data.getStringExtra("Description");
            obj.Status = "Pending";
            obj.locationaware = data.getBooleanExtra("locationaware", false);
            obj.location = new LatLng(data.getDoubleExtra("Latitude", 0), data.getDoubleExtra("Longitude", 0));
            obj.distance = data.getIntExtra("Distance", 0);
            items.add(obj);
            myAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onItemClick(int position) {
        a = (a + 1) % 3;
        if (a == 0) {
            items.get(position).Status = "Pending";
        } else if (a == 1) {
            items.get(position).Status = "Postponed";
        } else if (a == 2) {
            items.get(position).Status = "Completed";
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(int position) {
        items.remove(position);
        myAdapter.notifyDataSetChanged();
        return true;
    }
}