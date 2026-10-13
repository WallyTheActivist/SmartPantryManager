package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;

/**
 * Adapter that turns a List of PantryItem objects into rows
 * (item_pantry.xml) shown inside the RecyclerView.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /**
     * Callback interface: the adapter does not decide what happens on a click,
     * it just tells the Activity, which then acts.
     */
    public interface OnPantryItemActionListener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private List<PantryItem> items;
    private final OnPantryItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnPantryItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /** Replaces the data and refreshes the list on screen. */
    public void setItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // 1. Creates an empty row (inflates item_pantry.xml)
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    // 2. Fills a row with the data of the item at this position
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvItemQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

        String expiry = item.getExpiryDate();
        if (expiry == null || expiry.trim().isEmpty()) {
            holder.tvItemExpiry.setText("No expiry date");
        } else {
            holder.tvItemExpiry.setText("Expires: " + expiry);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    // 3. Tells the RecyclerView how many rows there are
    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Shows 3.0 as "3" but keeps 2.5 as "2.5". */
    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    /**
     * Holds references to the views of ONE row so findViewById
     * is not repeated every time the row is recycled.
     */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName;
        TextView tvItemQuantity;
        TextView tvItemExpiry;
        ImageButton btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemExpiry = itemView.findViewById(R.id.tvItemExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}