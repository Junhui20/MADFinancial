package com.uccd3223.madfinancial;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private List<Category> allCategories;
    private Category selectedCategory = null;
    private int selectedPosition = -1;

    public CategoryAdapter(List<Category> categories) {
        this.categories = new ArrayList<>(categories);
        this.allCategories = new ArrayList<>(categories);
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
        Category category = categories.get(position);
        holder.nameText.setText(category.getName());
        holder.categoryIcon.setImageResource(category.getIconResourceId());
        
        try {
            int categoryColor = Color.parseColor(category.getColor());
            int backgroundColor = ColorUtils.setAlphaComponent(categoryColor, 40); // More transparent background
            holder.cardView.setStrokeColor(categoryColor);
            holder.cardView.setCardBackgroundColor(backgroundColor);
            
            // Set text and icon color to the category color for better visibility
            holder.nameText.setTextColor(categoryColor);
            holder.categoryIcon.setColorFilter(categoryColor);

            // If selected, make background more opaque
            if (position == selectedPosition) {
                backgroundColor = ColorUtils.setAlphaComponent(categoryColor, 80);
                holder.cardView.setCardBackgroundColor(backgroundColor);
            }
        } catch (IllegalArgumentException e) {
            holder.cardView.setStrokeColor(Color.GRAY);
            holder.cardView.setCardBackgroundColor(Color.LTGRAY);
            holder.nameText.setTextColor(Color.DKGRAY);
            holder.categoryIcon.setColorFilter(Color.DKGRAY);
        }

        holder.cardView.setChecked(position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            selectedCategory = category;
            
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void filterCategories(String type) {
        categories.clear();
        for (Category category : allCategories) {
            if (category.getType().equals(type)) {
                categories.add(category);
            }
        }
        selectedCategory = null;
        selectedPosition = -1;
        notifyDataSetChanged();
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String categoryName) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(categoryName)) {
                int previousSelected = selectedPosition;
                selectedPosition = i;
                selectedCategory = categories.get(i);
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView nameText;
        ImageView categoryIcon;

        CategoryViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            nameText = itemView.findViewById(R.id.categoryName);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}
