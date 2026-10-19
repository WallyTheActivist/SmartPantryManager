package com.example.smartpantrymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.util.ExpiryUtils;
import com.example.smartpantrymanager.util.FormatUtils;

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

    // Expiry-alert settings, supplied by the Activity from SettingsManager
    private boolean expiryAlertsEnabled = false;
    private int warningDays = 3;

    /**
     * Tells the adapter whether to highlight expiring items, and how soon counts as "soon".
     */
    public void setExpiryWarning(boolean enabled, int days) {
        this.expiryAlertsEnabled = enabled;
        this.warningDays = days;
    }

    public PantryAdapter(List<PantryItem> items, OnPantryItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * Replaces the data and refreshes the list on screen.
     */
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
    // 2. Fills a row with the data of the item at this position
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvItemQuantity.setText(FormatUtils.formatQuantity(item.getQuantity()) + " " + item.getUnit());

        // Expiry line: normal grey, orange if expiring soon, red if expired
        String expiry = item.getExpiryDate();
        Context context = holder.itemView.getContext();
        int color = ContextCompat.getColor(context, R.color.text_secondary);
        String text;

        if (expiry == null || expiry.trim().isEmpty()) {
            text = "No expiry date";
        } else {
            text = "Expires: " + expiry;
            int days = ExpiryUtils.daysUntilExpiry(expiry);

            if (expiryAlertsEnabled && days != ExpiryUtils.NO_EXPIRY) {
                if (days < 0) {
                    text = "Expired: " + expiry;
                    color = ContextCompat.getColor(context, R.color.expiry_expired);
                } else if (days <= warningDays) {
                    String when = (days == 0) ? "today"
                            : "in " + days + (days == 1 ? " day" : " days");
                    text = "Expires soon (" + when + "): " + expiry;
                    color = ContextCompat.getColor(context, R.color.expiry_soon);
                }
            }
        }

        holder.tvItemExpiry.setText(text);
        holder.tvItemExpiry.setTextColor(color);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    // 3. Tells the RecyclerView how many rows there are
    @Override
    public int getItemCount() {
        return items.size();
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