package com.denwehrle.kitbib.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.denwehrle.kitbib.data.provider.Contract.BibFeeEntry;
import com.denwehrle.kitbib.data.provider.Contract.BibSummaryEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceEntry;
import com.denwehrle.kitbib.data.provider.Contract.LearningPlaceOccupationEntry;

/**
 * @author Dennis Wehrle
 */
public class Db extends SQLiteOpenHelper {

    private static final int DB_VERSION = 19;

    private static final String SQL_CREATE_BIB_SUMMARY_TABLE = "CREATE TABLE " + BibSummaryEntry.TABLE_NAME + " (" +
            BibSummaryEntry.COLUMN_ID + " INTEGER, " +
            BibSummaryEntry.COLUMN_INDEX + " TEXT, " +
            BibSummaryEntry.COLUMN_TITLE + " TEXT, " +
            BibSummaryEntry.COLUMN_STATE + " TEXT, " +
            BibSummaryEntry.COLUMN_ORIGIN_BIB + " TEXT, " +
            BibSummaryEntry.COLUMN_SIGNATURE + " TEXT, " +
            BibSummaryEntry.COLUMN_ORDER_INFO + " TEXT, " +
            BibSummaryEntry.COLUMN_DUE_DATE + " TEXT, " +
            BibSummaryEntry.COLUMN_FLAGGED + " TEXT, " +
            BibSummaryEntry.COLUMN_BOOK_COVER + " TEXT, " +
            "PRIMARY KEY (" + BibSummaryEntry.COLUMN_TITLE + ", " + BibSummaryEntry.COLUMN_SIGNATURE + ")" +

            " );";

    private static final String SQL_CREATE_BIB_FEE_TABLE = "CREATE TABLE " + BibFeeEntry.TABLE_NAME + " (" +
            BibFeeEntry.COLUMN_ID + " INTEGER," +
            BibFeeEntry.COLUMN_FEE + " TEXT, " +
            BibFeeEntry.COLUMN_FEE_TITLE + " TEXT, " +
            BibFeeEntry.COLUMN_ARTICLE + " TEXT, " +
            BibFeeEntry.COLUMN_SIGNATURE + " TEXT, " +
            BibFeeEntry.COLUMN_DUE_DATE + " TEXT, " +
            "PRIMARY KEY (" + BibFeeEntry.COLUMN_FEE_TITLE + ", " + BibFeeEntry.COLUMN_DUE_DATE + ")" +

            " );";

    private static final String SQL_CREATE_LEARNING_PLACE_OCCUPATION_TABLE = "CREATE TABLE " + LearningPlaceOccupationEntry.TABLE_NAME + " (" +
            LearningPlaceOccupationEntry.COLUMN_LOCATION + " TEXT PRIMARY KEY," +
            LearningPlaceOccupationEntry.COLUMN_FREE + " TEXT, " +
            LearningPlaceOccupationEntry.COLUMN_OCCUPIED + " TEXT, " +
            LearningPlaceOccupationEntry.COLUMN_UPDATEDAT + " INTEGER " +

            " );";

    private static final String SQL_CREATE_LEARNING_PLACE_TABLE = "CREATE TABLE " + LearningPlaceEntry.TABLE_NAME + " (" +
            LearningPlaceEntry.COLUMN_NAME + " TEXT PRIMARY KEY, " +
            LearningPlaceEntry.COLUMN_LONG_NAME + " TEXT, " +
            LearningPlaceEntry.COLUMN_LOCATION + " TEXT, " +
            LearningPlaceEntry.COLUMN_URL + " TEXT, " +
            LearningPlaceEntry.COLUMN_COORDINATES + " TEXT, " +
            LearningPlaceEntry.COLUMN_SEATS + " TEXT, " +
            LearningPlaceEntry.COLUMN_INDEX + " INTEGER " +

            " );";

    public Db(Context context) {
        super(context, "kitbib.db", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BIB_SUMMARY_TABLE);
        db.execSQL(SQL_CREATE_BIB_FEE_TABLE);
        db.execSQL(SQL_CREATE_LEARNING_PLACE_OCCUPATION_TABLE);
        db.execSQL(SQL_CREATE_LEARNING_PLACE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BibSummaryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BibFeeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LearningPlaceOccupationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LearningPlaceEntry.TABLE_NAME);

        onCreate(db);
    }
}