package com.example.spliwise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<String> itemList;
    Map<String, Map<String, Double>> balanceSheet;
    private ArrayAdapter<String> adapter;
    String[] users = {"User1", "User2", "User3", "User4"};
    private static final int REQUEST_CODE_Expense = 1;
    private static final int REQUEST_CODE_Detail = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.list_view);
        Button addButton = findViewById(R.id.add_button);
        Button detailActivityButton = findViewById(R.id.show_detail_button);
        itemList = new ArrayList<>();
        balanceSheet= new HashMap<>();

        // get a reference to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // get the JSON string from SharedPreferences
        String itemListJson = sharedPreferences.getString("itemList", null);
        String balanceSheetJson= sharedPreferences.getString("balanceSheet",null);
        // convert the JSON string to an ArrayList
        // Check if itemListJson is null
        if (itemListJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            itemList = gson.fromJson(itemListJson, type);
        }
        if (balanceSheetJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Map<String, Double>>>() {}.getType();
            balanceSheet = gson.fromJson(balanceSheetJson, type);
        }
        else {
            for(String user:users)
                balanceSheet.put(user, new HashMap<>());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        // Add button click listener
        addButton.setOnClickListener(view -> {
            // Open new activity to add item
            Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
            startActivityForResult(intent, REQUEST_CODE_Expense);
        });
        detailActivityButton.setOnClickListener(view -> {
            // Open new activity to add item
            Intent intent = new Intent(MainActivity.this, BalanceSheetActivity.class);
            startActivityForResult(intent,REQUEST_CODE_Detail);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_Expense && resultCode == RESULT_OK) {
            // Get new item from intent and add it to list view
            String amount = data.getStringExtra("amount");
            String description = data.getStringExtra("description");
            String payer = data.getStringExtra("payer");
            String users = data.getStringExtra("users");
            String [] usersArray= users.split(" ");

            // Update the balance sheet
            for (String paidTo: usersArray) {
                Map<String, Double> balances = balanceSheet.get(payer);
                if (!balances.containsKey(paidTo)) {
                    balances.put(paidTo, 0.0);
                }
                DecimalFormat df = new DecimalFormat("#.##");
                double splitAmount=  Double.parseDouble(amount)/(double)(usersArray.length);
                splitAmount= Double.parseDouble(df.format(splitAmount));
                balances.put(paidTo, balances.get(paidTo) +splitAmount);
                balances = balanceSheet.get(paidTo);
                if (!balances.containsKey(payer)){
                    balances.put(payer, 0.0);
                }
                balances.put(payer, balances.get(payer) - splitAmount);
            }

            String newItem =
                    "Description: " + description + "  " + "Amount: " + amount + "\n"+
                    "Paid by: " + payer + " " +"\n"+
                    "Distribute among: " + users;

            itemList.add(newItem);
            adapter.notifyDataSetChanged();

            // get a reference to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            // convert the ArrayList to a JSON string
            Gson gson = new Gson();
            String itemListJson = gson.toJson(itemList);
            String balanceSheetJson = gson.toJson(balanceSheet);
            // save the JSON string in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("itemList", itemListJson);
            editor.putString("balanceSheet",balanceSheetJson);
            editor.apply();
        }
    }
}
