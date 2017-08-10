package com.denwehrle.kitbib.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.denwehrle.kitbib.data.provider.Contract.BibFeeEntry;
import com.denwehrle.kitbib.data.provider.Contract.BibSummaryEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceOccupationEntry;

/**
 * @author Dennis Wehrle
 */
public class Provider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "com.denwehrle.kitbib";

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri BIB_SUMMARY_URI = BASE_URI.buildUpon().appendPath(Contract.PATH_BIB_SUMMARY).build();
    public static final Uri BIB_FEE_URI = BASE_URI.buildUpon().appendPath(Contract.PATH_BIB_FEE).build();

    private static final int BIB_SUMMARY = 100;
    private static final int BIB_FEE = 200;

    private static final int LEARNING_PLACE_OCCUPATION = 300;
    private static final int LEARNING_PLACE = 310;
    private static final int LEARNING_PLACE_INFO_AND_OCCUPATION = 320;

    private Db database;

    @Override
    public boolean onCreate() {
        database = new Db(getContext());
        return true;
    }

    private static final SQLiteQueryBuilder sAllLearningPlaceInformationQueryBuilder;

    static {
        sAllLearningPlaceInformationQueryBuilder = new SQLiteQueryBuilder();
        sAllLearningPlaceInformationQueryBuilder.setTables(
                LearningPlaceOccupationEntry.TABLE_NAME + " JOIN " +
                        LearningPlaceEntry.TABLE_NAME +
                        " ON " + LearningPlaceOccupationEntry.TABLE_NAME +
                        "." + LearningPlaceOccupationEntry.COLUMN_LOCATION +
                        " = " + LearningPlaceEntry.TABLE_NAME +
                        "." + LearningPlaceEntry.COLUMN_NAME);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        switch (URI_MATCHER.match(uri)) {
            case BIB_SUMMARY:
                cursor = database
                        .getReadableDatabase()
                        .query(BibSummaryEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case BIB_FEE:
                cursor = database
                        .getReadableDatabase()
                        .query(BibFeeEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case LEARNING_PLACE_OCCUPATION:
                cursor = database
                        .getReadableDatabase().query(
                                LearningPlaceOccupationEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case LEARNING_PLACE:
                cursor = database
                        .getReadableDatabase().query(
                                LearningPlaceEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case LEARNING_PLACE_INFO_AND_OCCUPATION:
                cursor = sAllLearningPlaceInformationQueryBuilder.query(database.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
        }

        if (cursor != null && getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case BIB_SUMMARY:
                return BibSummaryEntry.CONTENT_TYPE;
            case BIB_FEE:
                return BibFeeEntry.CONTENT_TYPE;
            case LEARNING_PLACE_OCCUPATION:
                return LearningPlaceOccupationEntry.CONTENT_TYPE;
            case LEARNING_PLACE:
                return LearningPlaceEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case BIB_SUMMARY:
                database.getWritableDatabase().insert(BibSummaryEntry.TABLE_NAME, null, values);
                break;
            case BIB_FEE:
                database.getWritableDatabase().insert(BibFeeEntry.TABLE_NAME, null, values);
                break;
            case LEARNING_PLACE_OCCUPATION:
                database.getWritableDatabase().replace(LearningPlaceOccupationEntry.TABLE_NAME, null, values);
                break;
            case LEARNING_PLACE:
                database.getWritableDatabase().replace(LearningPlaceEntry.TABLE_NAME, null, values);
                break;

        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (URI_MATCHER.match(uri)) {
            case BIB_SUMMARY:
                database.getWritableDatabase().beginTransaction();
                int bibSummaryCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.getWritableDatabase().replace(BibSummaryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            bibSummaryCount++;
                        }
                    }
                    database.getWritableDatabase().setTransactionSuccessful();
                } finally {
                    database.getWritableDatabase().endTransaction();
                }
                if (getContext() != null)
                    getContext().getContentResolver().notifyChange(uri, null);
                return bibSummaryCount;
            case BIB_FEE:
                database.getWritableDatabase().beginTransaction();
                int bibFeeCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.getWritableDatabase().replace(BibFeeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            bibFeeCount++;
                        }
                    }
                    database.getWritableDatabase().setTransactionSuccessful();
                } finally {
                    database.getWritableDatabase().endTransaction();
                }
                if (getContext() != null)
                    getContext().getContentResolver().notifyChange(uri, null);
                return bibFeeCount;
            case LEARNING_PLACE_OCCUPATION:
                database.getWritableDatabase().beginTransaction();
                int learninPlaceOccupationCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.getWritableDatabase().replace(LearningPlaceOccupationEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            learninPlaceOccupationCount++;
                        }
                    }
                    database.getWritableDatabase().setTransactionSuccessful();
                } finally {
                    database.getWritableDatabase().endTransaction();
                }
                if (getContext() != null)
                    getContext().getContentResolver().notifyChange(uri, null);
                return learninPlaceOccupationCount;
            case LEARNING_PLACE:
                database.getWritableDatabase().beginTransaction();
                int learningPlaceCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.getWritableDatabase().replace(LearningPlaceEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            learningPlaceCount++;
                        }
                    }
                    database.getWritableDatabase().setTransactionSuccessful();
                } finally {
                    database.getWritableDatabase().endTransaction();
                }
                if (getContext() != null)
                    getContext().getContentResolver().notifyChange(uri, null);
                return learningPlaceCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case BIB_SUMMARY:
                database.getWritableDatabase().delete(BibSummaryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BIB_FEE:
                database.getWritableDatabase().delete(BibFeeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LEARNING_PLACE_OCCUPATION:
                database.getWritableDatabase().delete(LearningPlaceOccupationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LEARNING_PLACE:
                database.getWritableDatabase().delete(LearningPlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case BIB_SUMMARY:
                database.getWritableDatabase().update(BibSummaryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BIB_FEE:
                database.getWritableDatabase().update(BibFeeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LEARNING_PLACE_OCCUPATION:
                database.getWritableDatabase().update(LearningPlaceOccupationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LEARNING_PLACE:
                database.getWritableDatabase().update(LearningPlaceEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_BIB_SUMMARY, BIB_SUMMARY);
        matcher.addURI(authority, Contract.PATH_BIB_FEE, BIB_FEE);
        matcher.addURI(authority, Contract.PATH_LEARNING_PLACE_OCCUPATION, LEARNING_PLACE_OCCUPATION);
        matcher.addURI(authority, Contract.PATH_LEARNING_PLACE, LEARNING_PLACE);
        matcher.addURI(authority, Contract.PATH_LEARNING_PLACE_OCCUPATION + "/" + Contract.PATH_LEARNING_PLACE, LEARNING_PLACE_INFO_AND_OCCUPATION);

        return matcher;
    }
}