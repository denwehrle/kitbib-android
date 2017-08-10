package com.denwehrle.kitbib.features.binding;

import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.denwehrle.kitbib.R;
import com.denwehrle.kitbib.data.model.BibSummaryItemState;
import com.denwehrle.kitbib.data.provider.Contract.BibFeeEntry;
import com.denwehrle.kitbib.data.provider.Contract.BibSummaryEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceOccupationEntry;
import com.denwehrle.kitbib.features.common.BarView;
import com.denwehrle.kitbib.utils.DateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;

/**
 * @author Dennis Wehrle
 */
public class BindingUtilCursor {

    @BindingAdapter("setId")
    public static void setId(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnId = cursor.getColumnIndex("_id");
        textView.setText(String.valueOf(cursor.getInt(columnId)));
    }

    @BindingAdapter("setTitle")
    public static void setTitle(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibSummaryEntry.COLUMN_TITLE);
        textView.setText(cursor.getString(columnText));
    }

    @BindingAdapter("setDueDate")
    public static void setDueDate(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibSummaryEntry.COLUMN_DUE_DATE);
        String stringDateTime = cursor.getString(columnText);
        textView.setText(DateUtils.SimpleDateFromTimestamp(stringDateTime));
    }

    @BindingAdapter("setOrderInfo")
    public static void setOrderInfo(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibSummaryEntry.COLUMN_ORDER_INFO);
        textView.setText(cursor.getString(columnText));
    }

    @BindingAdapter("setRemaining")
    public static void setRemaining(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibSummaryEntry.COLUMN_DUE_DATE);
        String stringDateTime = cursor.getString(columnText);
        Date date = DateUtils.dateFromTimestamp(stringDateTime);
        int diff = DateUtils.calculateTimeDifferenceInDays(date);

        String diffString = textView.getContext().getResources().getQuantityString(R.plurals.remaining_count, diff, diff);
        textView.setText(diffString);
    }

    @BindingAdapter("setSignature")
    public static void setSignature(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibSummaryEntry.COLUMN_SIGNATURE);
        textView.setText(cursor.getString(columnText));
    }

    @BindingAdapter("setState")
    public static void setState(View view, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibSummaryEntry.COLUMN_STATE);

        if (BibSummaryItemState.get(Integer.parseInt(cursor.getString(columnText))).name().equals("MoreThanTenDays")) {
            view.setBackgroundResource(R.color.colorTransparent);
        } else if (BibSummaryItemState.get(Integer.parseInt(cursor.getString(columnText))).name().equals("LessThanTenDays")) {
            view.setBackgroundResource(R.color.colorLessThanTenDays);
        } else if (BibSummaryItemState.get(Integer.parseInt(cursor.getString(columnText))).name().equals("Overdue")) {
            view.setBackgroundResource(R.color.colorOverdue);
        }
    }

    @BindingAdapter("setFee")
    public static void setFee(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibFeeEntry.COLUMN_FEE);
        textView.setText(textView.getContext().getResources().getString(R.string.fee_value, cursor.getString(columnText)));
    }

    @BindingAdapter("setFeeTitle")
    public static void setFeeTitle(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibFeeEntry.COLUMN_FEE_TITLE);
        textView.setText(cursor.getString(columnText));
    }

    @BindingAdapter("setArticle")
    public static void setArticle(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int columnText = cursor.getColumnIndex(BibFeeEntry.COLUMN_ARTICLE);
        textView.setText(cursor.getString(columnText));
    }

    @BindingAdapter("bind:setBookCover")
    public static void setBookCover(ImageView imageView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        String url = cursor.getString(cursor.getColumnIndex(BibSummaryEntry.COLUMN_BOOK_COVER));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder) // resource or drawable
                .showImageForEmptyUri(R.drawable.placeholder) // resource or drawable
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    private static int columnIndexLearningPlaceLocation = -1;
    private static int columnIndexLearningPlaceDate = -1;
    private static int columnIndexLearningPlaceOccupied = -1;
    private static int columnIndexLearningPlaceFree = -1;
    private static int columnIndexLearningPlaceName = -1;

    @BindingAdapter("bind:learningPlaceLocation")
    public static void setLocation(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        String prevLocation = null;

        // get previous item's location, for comparison
        if (cursor.getPosition() > 0 && cursor.moveToPrevious()) {
            prevLocation = getLocationFromCursor(cursor);
            cursor.moveToNext();
        }

        String location = getLocationFromCursor(cursor);

        // enable section heading if it's the first one, or different from the previous one
        if (prevLocation == null || !prevLocation.equals(location)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(location);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("bind:learningPlaceDescription")
    public static void setDescription(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int occupied = getOccupiedFromCursor(cursor);
        int free = getFreeFromCursor(cursor);

        int minutesDiff = DateUtils.calculateTimeDifferenceInMinutes(getDateFromCursor(cursor));

        int total = occupied + free;

        if (minutesDiff > 30) {
            if (minutesDiff > 120) {
                textView.setText(R.string.learning_place_no_data);
            } else {
                textView.setText(String.format("Vor %s Min.", minutesDiff));
            }
        } else {
            String string = textView.getContext().getResources().getQuantityString(R.plurals.learning_place_usage, total, free, total);
            textView.setText(string);
        }
    }

    @BindingAdapter("bind:learningPlaceBar")
    public static void setBar(BarView barView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        int occupied = getOccupiedFromCursor(cursor);
        int free = getFreeFromCursor(cursor);

        int minutesDiff = DateUtils.calculateTimeDifferenceInMinutes(getDateFromCursor(cursor));

        if (minutesDiff > 120) {
            occupied = 0;
            free = 1;
        }

        int total = occupied + free;

        barView.setMax(total);
        barView.setOccupied(occupied);
    }

    @BindingAdapter("bind:learningPlaceName")
    public static void setName(TextView textView, @Nullable Cursor cursor) {
        if (cursor == null) return;

        textView.setText(getNameFromCursor(cursor));
    }

    private static String getLocationFromCursor(@NonNull Cursor cursor) {
        if (columnIndexLearningPlaceLocation == -1) {
            columnIndexLearningPlaceLocation = cursor.getColumnIndex(LearningPlaceEntry.COLUMN_LOCATION);
        }
        return cursor.getString(columnIndexLearningPlaceLocation);
    }

    private static int getOccupiedFromCursor(@NonNull Cursor cursor) {
        if (columnIndexLearningPlaceOccupied == -1) {
            columnIndexLearningPlaceOccupied = cursor.getColumnIndex(LearningPlaceOccupationEntry.COLUMN_OCCUPIED);
        }

        return Integer.valueOf(cursor.getString(columnIndexLearningPlaceOccupied));
    }

    private static int getFreeFromCursor(@NonNull Cursor cursor) {
        if (columnIndexLearningPlaceFree == -1) {
            columnIndexLearningPlaceFree = cursor.getColumnIndex(LearningPlaceOccupationEntry.COLUMN_FREE);
        }
        return Integer.valueOf(cursor.getString(columnIndexLearningPlaceFree));
    }

    private static String getNameFromCursor(@NonNull Cursor cursor) {
        if (columnIndexLearningPlaceName == -1) {
            columnIndexLearningPlaceName = cursor.getColumnIndex(LearningPlaceEntry.COLUMN_LONG_NAME);
        }
        return cursor.getString(columnIndexLearningPlaceName);
    }

    private static Date getDateFromCursor(@NonNull Cursor cursor) {
        if (columnIndexLearningPlaceDate == -1) {
            columnIndexLearningPlaceDate = cursor.getColumnIndex(LearningPlaceOccupationEntry.COLUMN_UPDATEDAT);
        }
        return new Date(cursor.getLong(columnIndexLearningPlaceDate));
    }
}