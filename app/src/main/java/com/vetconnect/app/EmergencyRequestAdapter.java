package com.vetconnect.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class EmergencyRequestAdapter
        extends RecyclerView.Adapter<EmergencyRequestAdapter.RequestViewHolder> {

    public interface OnAcceptClickListener {
        void onAccept(EmergencyRequestModel request);
    }

    private final ArrayList<EmergencyRequestModel> requests;
    private final OnAcceptClickListener listener;

    public EmergencyRequestAdapter(
            ArrayList<EmergencyRequestModel> requests,
            OnAcceptClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_request, parent, false);

        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RequestViewHolder holder, int position) {

        EmergencyRequestModel request = requests.get(position);

        String animalName = request.getAnimalName();
        String animalType = request.getAnimalType();
        String description = request.getDescription();
        String status = request.getStatus();

        holder.tvAnimalName.setText(
                animalName == null || animalName.trim().isEmpty()
                        ? "Animal name not provided" : animalName);

        holder.tvAnimalType.setText(
                "Animal type: " +
                        (animalType == null || animalType.trim().isEmpty()
                                ? "Not specified" : animalType));

        holder.tvDescription.setText(
                "Problem: " +
                        (description == null || description.trim().isEmpty()
                                ? "No description provided" : description));

        String currentStatus =
                status == null || status.trim().isEmpty()
                        ? "pending" : status;

        holder.tvStatus.setText(
                "Status: " + currentStatus.toUpperCase(Locale.ROOT));

        if ("pending".equalsIgnoreCase(currentStatus)) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnAccept.setEnabled(true);
            holder.btnAccept.setText("Accept Request");

            holder.btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    holder.btnAccept.setEnabled(false);
                    holder.btnAccept.setText("Accepting...");
                    listener.onAccept(request);
                }
            });
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnAccept.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView tvAnimalName;
        TextView tvAnimalType;
        TextView tvDescription;
        TextView tvStatus;
        Button btnAccept;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAnimalName = itemView.findViewById(R.id.tvRequestAnimalName);
            tvAnimalType = itemView.findViewById(R.id.tvRequestAnimalType);
            tvDescription = itemView.findViewById(R.id.tvRequestDescription);
            tvStatus = itemView.findViewById(R.id.tvRequestStatus);
            btnAccept = itemView.findViewById(R.id.btnAcceptRequest);
        }
    }
}