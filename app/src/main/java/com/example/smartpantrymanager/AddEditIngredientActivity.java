package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.util.FormatUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * One screen used for BOTH adding a new ingredient and editing an existing one.
 * If the Intent carries an item id, we are in Edit mode; otherwise Add mode.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    // Key used to pass the item's id from the list screen to this screen
    public static final String EXTRA_ITEM_ID = "com.example.smartpantrymanager.EXTRA_ITEM_ID";

    // Units offered in the dropdown
    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs", "tsp", "tbsp", "cup"};

    private TextView tvFormTitle;
    private TextView tvClearDate;
    private EditText etName;
    private EditText etQuantity;
    private EditText etExpiry;
    private Spinner spUnit;
    private Button btnSave;
    private Button btnCancel;

    private DatabaseHelper dbHelper;

    // -1 means "Add mode"; any other value is the id of the item being edited
    private int editItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        // Connect the Java fields to the views in the XML layout
        tvFormTitle = findViewById(R.id.tvFormTitle);
        tvClearDate = findViewById(R.id.tvClearDate);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new DatabaseHelper(this);

        // Fill the unit dropdown
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnit.setAdapter(unitAdapter);

        // Did the list screen send us an item id? If yes, we are editing.
        editItemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);

        if (editItemId != -1) {
            PantryItem existing = dbHelper.getPantryItem(editItemId);
            if (existing == null) {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            tvFormTitle.setText("Edit Ingredient");
            btnSave.setText("Update");

            // Pre-fill the form with the item's current values
            etName.setText(existing.getName());
            etQuantity.setText(FormatUtils.formatQuantity(existing.getQuantity()));
            etExpiry.setText(existing.getExpiryDate());
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(existing.getUnit())) {
                    spUnit.setSelection(i);
                    break;
                }
            }
        }

        // Tapping the date field opens a calendar dialog
        etExpiry.setOnClickListener(v -> showDatePicker());

        tvClearDate.setOnClickListener(v -> etExpiry.setText(""));

        btnSave.setOnClickListener(v -> saveIngredient());

        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Opens a DatePickerDialog and writes the chosen date as yyyy-MM-dd.
     */
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        // If a date is already in the field, start the calendar on that date
        String current = etExpiry.getText().toString().trim();
        if (current.length() == 10) {
            try {
                String[] parts = current.split("-");
                cal.set(Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]) - 1,   // months start at 0
                        Integer.parseInt(parts[2]));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // Ignore: the calendar just opens on today's date
            }
        }

        new DatePickerDialog(this,
                (view, year, month, day) -> etExpiry.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Checks every field and shows an error message on each invalid one.
     * Returns true only if everything is valid.
     */
    private boolean validateInput() {
        boolean valid = true;

        String name = etName.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();

        // Name: required, at least 2 characters, letters/spaces/hyphens/apostrophes only
        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            valid = false;
        } else if (name.length() < 2) {
            etName.setError("Name must be at least 2 characters");
            valid = false;
        } else if (!name.matches("[\\p{L} '\\-]+")) {
            etName.setError("Use letters only (no numbers or symbols)");
            valid = false;
        }

        // Quantity: required, a valid number, greater than 0, not absurdly large
        if (quantityText.isEmpty()) {
            etQuantity.setError("Quantity is required");
            valid = false;
        } else {
            try {
                double quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    etQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                } else if (quantity > 100000) {
                    etQuantity.setError("Quantity is too large");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates, then inserts (Add mode) or updates (Edit mode) the item.
     */
    private void saveIngredient() {
        if (!validateInput()) {
            return;   // errors are already shown on the fields
        }

        String name = etName.getText().toString().trim();
        double quantity = Double.parseDouble(etQuantity.getText().toString().trim());
        String unit = spUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();

        if (editItemId == -1) {
            // ADD mode
            long newId = dbHelper.addPantryItem(new PantryItem(name, quantity, unit, expiry));
            if (newId == -1) {
                Toast.makeText(this, "Could not save ingredient", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, name + " added to pantry", Toast.LENGTH_SHORT).show();
        } else {
            // EDIT mode
            int rows = dbHelper.updatePantryItem(
                    new PantryItem(editItemId, name, quantity, unit, expiry));
            if (rows == 0) {
                Toast.makeText(this, "Could not update ingredient", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, name + " updated", Toast.LENGTH_SHORT).show();
        }

        finish();   // closes this screen; the list screen's onResume() refreshes the list
    }


}