package com.vetconnect.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicalAnimalAdapter extends RecyclerView.Adapter<MedicalAnimalAdapter.AnimalViewHolder> {

    private List<Animal> animalList;
    private OnAnimalClickListener listener;

    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    public MedicalAnimalAdapter(List<Animal> animalList, OnAnimalClickListener listener) {
        this.animalList = animalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);

        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {

        Animal animal = animalList.get(position);

        holder.tvAnimalName.setText(animal.getAnimalName());
        holder.tvAnimalType.setText("Type: " + animal.getAnimalType());
        holder.tvBreed.setText("Breed: " + animal.getBreed());
        holder.tvAge.setText("Age: " + animal.getAge());
        holder.tvGender.setText("Gender: " + animal.getGender());

        holder.itemView.setOnClickListener(v ->
                listener.onAnimalClick(animal));
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {

        TextView tvAnimalName, tvAnimalType, tvBreed, tvAge, tvGender;


        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAnimalName = itemView.findViewById(R.id.tvAnimalName);
            tvAnimalType = itemView.findViewById(R.id.tvAnimalType);
            tvBreed = itemView.findViewById(R.id.tvBreed);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvGender = itemView.findViewById(R.id.tvGender);

        }
    }
}