package com.example.spliwise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addButton;
    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_view);
        addButton = findViewById(R.id.add_button);
        itemList = new ArrayList<>();
        // get a reference to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // get the JSON string from SharedPreferences
        String itemListJson = sharedPreferences.getString("itemList", null);

        // convert the JSON string to an ArrayList
        // Check if itemListJson is null
        if (itemListJson != null) {
            // Convert the JSON string to an ArrayList using Gson
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
             itemList = gson.fromJson(itemListJson, type);
        } else {
            // Handle the case where "itemList" preference is not present in SharedPreferences
            itemList.add("No Transactions");
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        // Add button click listener
        addButton.setOnClickListener(view -> {
            // Open new activity to add item
            Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get new item from intent and add it to list view
            String amount = data.getStringExtra("amount");
            String description = data.getStringExtra("description");
            String payer = data.getStringExtra("payer");
            String users = data.getStringExtra("users");

            String newItem =
                    "Description: " + description + "  " + "Amount: " + amount + "\n"+
                    "Paid by: " + payer + " " +"\n"+
                    "Distribute among: " + users;

            // get a reference to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String added_itemListJson = sharedPreferences.getString("itemList", null);
            if (added_itemListJson == null) itemList.clear();
            itemList.add(newItem);
            // convert the ArrayList to a JSON string
            Gson gson = new Gson();
            String itemListJson = gson.toJson(itemList);

            // save the JSON string in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("itemList", itemListJson);
            editor.apply();

            adapter.notifyDataSetChanged();
        }
    }
}
