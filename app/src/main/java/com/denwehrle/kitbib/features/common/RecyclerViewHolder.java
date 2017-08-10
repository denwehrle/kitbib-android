package com.denwehrle.kitbib.features.common;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * @author Dennis Wehrle
 */
public final class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    public RecyclerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}