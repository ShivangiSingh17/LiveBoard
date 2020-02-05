package com.example.livewhiteboard;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SavedDrawings extends AppCompatActivity {

    public static final String TAG = "saved";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_drawings);

        DatabaseHandler dbHelper = new DatabaseHandler(this);

        final SQLiteDatabase writeDb = dbHelper.getWritableDatabase();
        final SQLiteDatabase readDb = dbHelper.getReadableDatabase();

        ArrayList<DrawingModel> drawings = DrawingsTable.getAllDrawings(readDb);
        for(int i=0;i<drawings.size(); i++){
            Log.d(TAG, "onCreate: " + drawings.get(i).getName() + " " + drawings.get(i).getId());
        }

        final DrawingsAdapter adapter = new DrawingsAdapter(this,drawings);
        ListView drawingsList = findViewById(R.id.lvDrawingsList);
        drawingsList.setAdapter(adapter);

        drawingsList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(SavedDrawings.this, Drawing.class);
                        i.putExtra("drawingId", adapter.getItem(position).getId());
                        startActivity(i);
                    }
                }
        );




    }
}
