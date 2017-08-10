package com.denwehrle.kitbib.features.common;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * @author Dennis Wehrle
 */
public class CursorRecyclerAdapter extends DataBindingRecyclerAdapter<Cursor> {

    private Cursor cursor;

    public CursorRecyclerAdapter(@LayoutRes int dataLayoutId, int dataBindingId, @StringRes int noContentTextId) {
        super(dataLayoutId, dataBindingId, noContentTextId);
    }

    @Override
    public int evaluateDataItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    @Nullable
    @Override
    public Cursor getData(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    protected void bindData(RecyclerViewHolder holder, Cursor data, int viewType) {
        holder.getBinding().setVariable(getDataBindingId(), data);
    }

    public void swapCursor(Cursor cursor) {
        synchronized (this) {
            this.cursor = cursor;
        }
        resetCounter();
        notifyDataSetChanged();
    }
}