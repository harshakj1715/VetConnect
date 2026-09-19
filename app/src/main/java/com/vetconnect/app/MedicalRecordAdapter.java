package com.vetconnect.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicalRecordAdapter
        extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    List<MedicalRecordModel> recordList;

    OnMedicalRecordClickListener listener;


    public interface OnMedicalRecordClickListener {

        void onDeleteClick(
                MedicalRecordModel record
        );

        void onEditClick(
                MedicalRecordModel record
        );
    }


    public MedicalRecordAdapter(
            List<MedicalRecordModel> recordList,
            OnMedicalRecordClickListener listener) {

        this.recordList = recordList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_medical_record,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        MedicalRecordModel record =
                recordList.get(position);


        // ======================================
        // DATE
        // ======================================

        String date =
                record.getDate();

        String[] formattedDate =
                formatDate(date);


        holder.tvTimelineDate.setText(
                formattedDate[0]
        );

        holder.tvTimelineYear.setText(
                formattedDate[1]
        );


        // ======================================
        // DOCTOR
        // ======================================

        if (record.getDoctorName() == null ||
                record.getDoctorName().isEmpty()) {

            holder.tvDoctor.setText(
                    "Not Available"
            );

        } else {

            holder.tvDoctor.setText(
                    record.getDoctorName()
            );
        }


        // ======================================
        // DISEASE
        // ======================================

        holder.tvDisease.setText(
                safeValue(record.getDisease())
        );


        // ======================================
        // MEDICINE
        // ======================================

        holder.tvMedicine.setText(
                safeValue(record.getMedicine())
        );


        // ======================================
        // VACCINATION
        // ======================================

        holder.tvVaccination.setText(
                safeValue(record.getVaccination())
        );


        // ======================================
        // NOTES
        // ======================================

        holder.tvNotes.setText(
                safeValue(record.getNotes())
        );


        // ======================================
        // RECORD TITLE
        // ======================================

        holder.tvRecordTitle.setText(
                getRecordTitle(record)
        );


        // ======================================
        // STATUS
        // ======================================

        holder.tvStatus.setText(
                "Completed"
        );
    }


    private String safeValue(String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "Not Available";
        }

        return value;
    }


    // ==========================================
    // RECORD TITLE
    // ==========================================

    private String getRecordTitle(
            MedicalRecordModel record) {

        String disease =
                record.getDisease();

        if (disease == null) {

            return "Health Checkup";
        }


        String lower =
                disease.toLowerCase(
                        Locale.getDefault()
                );


        if (lower.contains("worm") ||
                lower.contains("deworm")) {

            return "Deworming";
        }


        if (lower.contains("fever")) {

            return "Fever Treatment";
        }


        if (lower.contains("vaccin")) {

            return "Vaccination";
        }


        return "Health Checkup";
    }


    // ==========================================
    // FORMAT DATE
    // ==========================================

    private String[] formatDate(
            String dateString) {

        if (dateString == null ||
                dateString.trim().isEmpty()) {

            return new String[]{
                    "DATE",
                    ""
            };
        }


        String[] formats = {

                "dd/MM/yyyy",

                "dd-MM-yyyy",

                "yyyy-MM-dd",

                "MM/dd/yyyy"
        };


        for (String format : formats) {

            try {

                SimpleDateFormat input =
                        new SimpleDateFormat(
                                format,
                                Locale.getDefault()
                        );

                input.setLenient(false);


                Date date =
                        input.parse(dateString);


                if (date != null) {

                    SimpleDateFormat dayMonth =
                            new SimpleDateFormat(
                                    "dd MMM",
                                    Locale.ENGLISH
                            );


                    SimpleDateFormat year =
                            new SimpleDateFormat(
                                    "yyyy",
                                    Locale.ENGLISH
                            );


                    return new String[]{

                            dayMonth.format(date)
                                    .toUpperCase(
                                    Locale.ENGLISH
                            ),

                            year.format(date)
                    };
                }

            } catch (ParseException ignored) {
            }
        }


        return new String[]{
                dateString,
                ""
        };
    }


    @Override
    public int getItemCount() {

        return recordList.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTimelineDate;
        TextView tvTimelineYear;

        TextView tvRecordTitle;
        TextView tvStatus;

        TextView tvDoctor;

        TextView tvDisease;
        TextView tvMedicine;
        TextView tvVaccination;
        TextView tvNotes;


        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            tvTimelineDate =
                    itemView.findViewById(
                            R.id.tvTimelineDate
                    );


            tvTimelineYear =
                    itemView.findViewById(
                            R.id.tvTimelineYear
                    );


            tvRecordTitle =
                    itemView.findViewById(
                            R.id.tvRecordTitle
                    );


            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );


            tvDoctor =
                    itemView.findViewById(
                            R.id.tvDoctor
                    );


            tvDisease =
                    itemView.findViewById(
                            R.id.tvDisease
                    );


            tvMedicine =
                    itemView.findViewById(
                            R.id.tvMedicine
                    );


            tvVaccination =
                    itemView.findViewById(
                            R.id.tvVaccination
                    );


            tvNotes =
                    itemView.findViewById(
                            R.id.tvNotes
                    );
        }
    }
}