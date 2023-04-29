package com.example.spliwise;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {
    private String payer;
    private List<String>distributor;
    String[] users = {"User1", "User2", "User3", "User4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        distributor=new ArrayList<>();

        // Initialize UI elements
        EditText amountEditText = findViewById(R.id.editTextAmount);
        EditText descriptionEditText = findViewById(R.id.editTextDescription);
        Button addButton = findViewById(R.id.buttonAddExpense);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button selectPayerButton = findViewById(R.id.selectpayerbutton);
        selectPayerButton.setOnClickListener(v -> {
            final ArrayList<String> selectedUsers = new ArrayList<>();
            AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseActivity.this);
            builder.setTitle("Select Payer");
            builder.setMultiChoiceItems(users, null, (dialog, which, isChecked) -> {
                // handle user selection
                String selectedUser = users[which];
                if (isChecked) {
                    selectedUsers.add(selectedUser);
                } else {
                    selectedUsers.remove(selectedUser);
                }
            });
            builder.setPositiveButton("OK", (dialog, which) -> {
                // convert ArrayList to array
                String [] selectedUsersArray = selectedUsers.toArray(new String[selectedUsers.size()]);
                // handle selected users
                for (String user : selectedUsersArray) {
                    payer=user;
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

        });
        Button selectDistributorButton = findViewById(R.id.selectDistributorbutton);
        selectDistributorButton.setOnClickListener(v -> {
            final ArrayList<String> selectedUsers = new ArrayList<>();
            AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseActivity.this);
            builder.setTitle("Select Distributor");
            builder.setMultiChoiceItems(users, null, (dialog, which, isChecked) -> {
                // handle user selection
                String selectedUser = users[which];
                if (isChecked) {
                    selectedUsers.add(selectedUser);
                } else {
                    selectedUsers.remove(selectedUser);
                }
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                // convert ArrayList to array
                String [] selectedUsersArray = selectedUsers.toArray(new String[selectedUsers.size()]);
                // handle selected users
                for (String user : selectedUsersArray) {
                    distributor.add(user);
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();

        });
        // Add button click listener
        addButton.setOnClickListener(view -> {
            // Get input data from edit texts and spinners
            String amount = amountEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String paiBy = payer;
            String distributeAmong="";
            for(String user: distributor){
                distributeAmong += user+" ";
            }
            // Create intent with input data
            Intent intent = new Intent();
            intent.putExtra("amount", amount);
            intent.putExtra("description", description);
            intent.putExtra("payer", paiBy);
            intent.putExtra("users", distributeAmong);
            setResult(RESULT_OK, intent);

            // Close activity
            finish();
        });
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