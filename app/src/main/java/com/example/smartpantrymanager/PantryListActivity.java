package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

/**
 * Home screen: shows every ingredient currently in the user's pantry.
 */
public class PantryListActivity extends AppCompatActivity {

    private RecyclerView recyclerPantry;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        // Connect the Java fields to the views in the XML layout
        recyclerPantry = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);

        dbHelper = new DatabaseHelper(this);

        // Create the adapter and tell it what to do when a row is tapped / deleted
        adapter = new PantryAdapter(new ArrayList<>(), new PantryAdapter.OnPantryItemActionListener() {
            @Override
            public void onItemClick(PantryItem item) {
                // Open the Add/Edit screen in Edit mode, passing this item's id
                Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
                intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(PantryItem item) {
                confirmDelete(item);
            }
        });

        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            // Open the Add/Edit screen in Add mode (no id is sent)
            Intent intent = new Intent(PantryListActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });
    }

    // Runs every time the screen becomes visible again (e.g. after returning
    // from the Add/Edit screen), so the list is always up to date.
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    /** Reads all items from SQLite and shows them (or the empty message). */
    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        adapter.setItems(items);

        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerPantry.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerPantry.setVisibility(View.VISIBLE);
        }
    }

    /** Asks the user to confirm before deleting an item. */
    private void confirmDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    loadPantryItems();
                    Toast.makeText(this, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}