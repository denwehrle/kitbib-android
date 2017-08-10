package com.denwehrle.kitbib.features.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.denwehrle.kitbib.BR;
import com.denwehrle.kitbib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Wehrle
 */
public class DataBindingRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data, View view);
    }

    public interface OnContextMenuItemClickListener {
        void onContextMenuItemClick(int position, View view, MenuItem item);
    }

    private static final int ITEM_COUNT_INVALID = -1;

    private static final int VIEW_TYPE_NO_CONTENT = -1;
    private static final int VIEW_TYPE_DATA = 0;

    // data layout
    @LayoutRes
    private int dataLayoutId;
    private int dataBindingId;

    // no content layout
    @LayoutRes
    private int noContentLayoutId = R.layout.container_no_data;
    private int noContentBindingId = BR.charSequence;
    @StringRes
    private int noContentTextId;

    /**
     * The real number of available data.
     */
    private int dataItemCount = ITEM_COUNT_INVALID;
    /**
     * The number of views.
     * The value is at least {@link #dataItemCount}
     * but can be 1 if {@link #hasNoContentLayout()} is true
     */
    private int viewItemCount = ITEM_COUNT_INVALID;

    private List<T> data = new ArrayList<>();

    private OnItemClickListener<T> onItemClickListener = null;
    private OnContextMenuItemClickListener onContextMenuItemClickListener = null;

    /**
     * Creates an adapter without "no content" view.
     *
     * @param dataLayoutId  The layout id for a data view
     * @param dataBindingId The data binding id from the dataLayoutId
     */
    public DataBindingRecyclerAdapter(@LayoutRes int dataLayoutId, int dataBindingId, @StringRes int noContentTextId) {
        this.dataLayoutId = dataLayoutId;
        this.dataBindingId = dataBindingId;
        this.noContentTextId = noContentTextId;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @LayoutRes int layout = getDataLayout(viewType);

        ViewDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                layout,
                parent,
                false);

        if (binding == null) {
            throw new RuntimeException("The given layout for viewType " + viewType + " is probably not prepared for DataBinding");
        }

        return new RecyclerViewHolder(binding);
    }

    @LayoutRes
    private int getDataLayout(int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NO_CONTENT:
                return noContentLayoutId;
            case VIEW_TYPE_DATA:
                return dataLayoutId;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_NO_CONTENT) {
            onBindNoContent(holder);
        } else {
            onBindData(holder, position, viewType);
        }

        holder.getBinding().executePendingBindings();
    }

    private void onBindData(final RecyclerViewHolder viewHolder, final int position, int viewType) {
        final T data = getData(position);

        bindData(viewHolder, data, viewType);
        if (hasOnItemClickListener()) {
            final OnItemClickListener<T> onItemClickListener = this.onItemClickListener;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, data, viewHolder.itemView);
                    }
                }
            });
        }
        if (hasOnContextMenuItemClickListener()) {
            final OnContextMenuItemClickListener onContextMenuItemClickListener = this.onContextMenuItemClickListener;
            viewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                    MenuItem extend = menu.add("Verlängern");
                    extend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (onContextMenuItemClickListener != null) {
                                onContextMenuItemClickListener.onContextMenuItemClick(position, viewHolder.itemView, item);
                            }
                            return false;
                        }
                    });
                }
            });
        }
    }

    protected void bindData(RecyclerViewHolder holder, T data, int viewType) {
        holder.getBinding().setVariable(getBindingVariable(viewType), data);
    }

    private void onBindNoContent(RecyclerViewHolder holder) {
        if (!hasNoContentText()) return;
        Context context = holder.getBinding().getRoot().getContext();
        String string = context.getString(this.noContentTextId);
        holder.getBinding().setVariable(noContentBindingId, string);
    }

    @Override
    public int getItemCount() {
        if (viewItemCount != ITEM_COUNT_INVALID) {
            return viewItemCount;
        }

        viewItemCount = getDataItemCount();

        if (viewItemCount == 0 && hasNoContentLayout()) {
            viewItemCount = 1;
        }
        return viewItemCount;
    }

    private int getDataItemCount() {
        if (dataItemCount != ITEM_COUNT_INVALID) {
            return dataItemCount;
        }

        dataItemCount = evaluateDataItemCount();
        return dataItemCount;
    }

    protected int evaluateDataItemCount() {
        return data.size();
    }

    @StringRes
    public int getNoContentText() {
        return noContentTextId;
    }

    @LayoutRes
    public int getNoContentLayout() {
        return noContentLayoutId;
    }

    public boolean hasNoContentText() {
        return getNoContentText() != 0;
    }

    public boolean hasNoContentLayout() {
        return getNoContentLayout() != 0;
    }

    private int getBindingVariable(int viewType) {
        switch (viewType) {
            case VIEW_TYPE_DATA:
                return getDataBindingId();
            case VIEW_TYPE_NO_CONTENT:
                return getNoContentBindingId();
        }
        return 0;
    }

    public int getDataBindingId() {
        return dataBindingId;
    }

    public int getNoContentBindingId() {
        return noContentBindingId;
    }

    @Override
    public int getItemViewType(int position) {
        if (getDataItemCount() == 0) return VIEW_TYPE_NO_CONTENT;
        return VIEW_TYPE_DATA;
    }

    @Nullable
    protected T getData(int position) {
        int size = data.size();
        if (position >= size) {
            return null;
        }
        return data.get(position);
    }

    public void setData(List<T> data) {
        clearData();
        addData(data);
    }

    public void addData(List<T> data) {
        int start = this.data.size();
        this.data.addAll(data);
        resetCounter();
        notifyItemRangeInserted(start, data.size());
    }

    public void clearData() {
        this.data.clear();
        resetCounter();
    }

    protected void resetCounter() {
        this.dataItemCount = ITEM_COUNT_INVALID;
        this.viewItemCount = ITEM_COUNT_INVALID;
        notifyDataSetChanged();
    }

    public boolean hasOnItemClickListener() {
        return onItemClickListener != null;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public boolean hasOnContextMenuItemClickListener() {
        return onContextMenuItemClickListener != null;
    }

    public void setOnContextMenuItemClickListener(@Nullable OnContextMenuItemClickListener onContextMenuItemClickListener) {
        this.onContextMenuItemClickListener = onContextMenuItemClickListener;
    }
}