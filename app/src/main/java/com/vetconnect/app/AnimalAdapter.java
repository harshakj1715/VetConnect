package com.vetconnect.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animalList;
    private OnAnimalClickListener listener;

    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    public AnimalAdapter(List<Animal> animalList, OnAnimalClickListener listener) {
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

        holder.tvAnimalType.setText(
                animal.getAnimalType()
                        + " • "
                        + animal.getAge()
                        + " Years • "
                        + animal.getGender()
        );

        holder.tvBreed.setText(
                animal.getBreed()
        );

        holder.itemView.setOnClickListener(v ->
                listener.onAnimalClick(animal)
        );
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {

        TextView tvAnimalName;
        TextView tvAnimalType;
        TextView tvBreed;
        TextView tvAge;
        TextView tvGender;

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