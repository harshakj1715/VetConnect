package com.vetconnect.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    List<MedicalRecordModel> recordList;
    OnMedicalRecordClickListener listener;

    public interface OnMedicalRecordClickListener {
        void onDeleteClick(MedicalRecordModel record);
        void onEditClick(MedicalRecordModel record);
    }

    public MedicalRecordAdapter(List<MedicalRecordModel> recordList,
                                OnMedicalRecordClickListener listener) {
        this.recordList = recordList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medical_record, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MedicalRecordModel record = recordList.get(position);

        holder.tvDisease.setText("Disease: " + record.getDisease());
        holder.tvMedicine.setText("Medicine: " + record.getMedicine());
        holder.tvVaccination.setText("Vaccination: " + record.getVaccination());
        holder.tvNotes.setText("Notes: " + record.getNotes());
        holder.tvDate.setText("Date: " + record.getDate());
        if (record.getDoctorName() == null || record.getDoctorName().isEmpty()) {
            holder.tvDoctor.setText("Doctor: Not Available");
        } else {
            holder.tvDoctor.setText("Doctor: " + record.getDoctorName());
        }


    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDisease, tvMedicine, tvVaccination, tvNotes, tvDate, tvDoctor;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDisease = itemView.findViewById(R.id.tvDisease);
            tvMedicine = itemView.findViewById(R.id.tvMedicine);
            tvVaccination = itemView.findViewById(R.id.tvVaccination);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);

        }
    }
}