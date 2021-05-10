package com.zolve;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    ArrayList<Transaction> itemsList;

    public TransactionAdapter(ArrayList<Transaction> items) {
        itemsList = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        Transaction model = itemsList.get(position);
        holder.bindView(model);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView amount;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
        }

        void bindView(Transaction view) {
            type.setText(view.type);
            amount.setText(String.valueOf(view.amount));
            date.setText(view.timestamp);
        }
    }
}
