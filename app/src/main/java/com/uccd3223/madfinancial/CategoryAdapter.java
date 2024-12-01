package com.uccd3223.madfinancial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<Category> allCategories;
    private final List<Category> displayedCategories;
    private final OnCategorySelectedListener listener;
    private int selectedPosition = -1;

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategorySelectedListener listener) {
        this.allCategories = new ArrayList<>(categories);
        this.displayedCategories = new ArrayList<>(categories);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = displayedCategories.get(position);
        holder.bind(category, position == selectedPosition);
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });

        // Update selection state
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        cardView.setChecked(position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return displayedCategories.size();
    }

    public void filterCategories(String type) {
        displayedCategories.clear();
        for (Category category : allCategories) {
            if (category.getType().equals(type)) {
                displayedCategories.add(category);
            }
        }
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView nameView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.categoryIcon);
            nameView = itemView.findViewById(R.id.categoryName);
        }

        void bind(Category category, boolean isSelected) {
            iconView.setImageResource(category.getIconResource());
            nameView.setText(category.getName());
            itemView.setSelected(isSelected);
        }
    }
}
