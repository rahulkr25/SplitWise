package com.example.spliwise;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceSheetActivity extends AppCompatActivity {
    Map<String, Map<String, Double>> balanceSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ListView listView = findViewById(R.id.detail_list_view);
        List<String> itemList;
        balanceSheet = new HashMap<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get a reference to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String balanceSheetJson = sharedPreferences.getString("balanceSheet", null);
        if (balanceSheetJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Map<String, Double>>>() {
            }.getType();
            balanceSheet = gson.fromJson(balanceSheetJson, type);
        }
        List<String> updated_item = new ArrayList<>();
        for (Map.Entry<String, Map<String, Double>> allBalances : balanceSheet.entrySet()) {
            for (Map.Entry<String, Double> userBalance : allBalances.getValue().entrySet()) {
                if (userBalance.getValue() > 0) {
                    if (userBalance.getValue() < 0) {
                        String str = allBalances.getKey() + " owes " + userBalance.getKey() + ": " + Math.abs(userBalance.getValue());
                        updated_item.add(str);
                    } else if (userBalance.getValue() > 0) {
                        updated_item.add(userBalance.getKey() + " owes " + allBalances.getKey() + ": " + Math.abs(userBalance.getValue()));
                    }
                }
            }
        }
        itemList = new ArrayList<>(updated_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
