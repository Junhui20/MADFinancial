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
    private List<Category> categories;
    private List<Category> allCategories;
    private Category selectedCategory;
    private OnCategorySelectedListener listener;

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategorySelectedListener listener) {
        this.allCategories = new ArrayList<>(categories);
        this.categories = new ArrayList<>(categories);
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
        Category category = categories.get(position);
        holder.bind(category);
        holder.itemView.setOnClickListener(v -> {
            selectedCategory = category;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });

        // Update selection state
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        cardView.setChecked(category == selectedCategory);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void filterCategories(String type) {
        categories = allCategories.stream()
                .filter(category -> category.getType().equals(type))
                .collect(Collectors.toList());
        selectedCategory = null;
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

        void bind(Category category) {
            iconView.setImageResource(category.getIconResource());
            nameView.setText(category.getName());
        }
    }
}
