package com.denwehrle.kitbib.data.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Dennis Wehrle
 */
public class Contract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.
    public static final String CONTENT_AUTHORITY = "com.denwehrle.kitbib";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's) For instance, content://com.spotaneous.app/venues/ is a valid path for looking at venue data.
    public static final String PATH_BIB_SUMMARY = "bibSummary";
    public static final String PATH_BIB_FEE = "bibFee";
    public static final String PATH_LEARNING_PLACE_OCCUPATION = "learningPlaceOccupation";
    public static final String PATH_LEARNING_PLACE = "learningPlace";

    /* Inner class that defines the table content of the table */
    public static final class BibSummaryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BIB_SUMMARY).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_BIB_SUMMARY;

        // Table name
        public static final String TABLE_NAME = "bibSummary";

        // Column names
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_INDEX = "sortIndex";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ORIGIN_BIB = "originBib";
        public static final String COLUMN_SIGNATURE = "signature";
        public static final String COLUMN_ORDER_INFO = "orderInfo";
        public static final String COLUMN_DUE_DATE = "dueDate";
        public static final String COLUMN_FLAGGED = "flagged";
        public static final String COLUMN_BOOK_COVER = "bookCover";
    }

    /* Inner class that defines the table content of the table */
    public static final class BibFeeEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BIB_FEE).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_BIB_SUMMARY;

        // Table name
        public static final String TABLE_NAME = "bibFee";

        // Column names
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_FEE = "fee";
        public static final String COLUMN_FEE_TITLE = "feeTitle";
        public static final String COLUMN_ARTICLE = "article";
        public static final String COLUMN_SIGNATURE = "signature";
        public static final String COLUMN_DUE_DATE = "dueDate";
    }

    /* Inner class that defines the table contents of the learningPlaceOccupation table */
    public static final class LearningPlaceOccupationEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LEARNING_PLACE_OCCUPATION).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LEARNING_PLACE_OCCUPATION;

        // Table name
        public static final String TABLE_NAME = "learningPlaceOccupation";

        // Column names
        public static final String COLUMN_FREE = "freeSeats";
        public static final String COLUMN_OCCUPIED = "occupiedSeats";
        public static final String COLUMN_UPDATEDAT = "updatedAt";
        public static final String COLUMN_LOCATION = "location";
    }

    /* Inner class that defines the table contents of the learningPlace table */
    public static final class LearningPlaceEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LEARNING_PLACE).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LEARNING_PLACE;

        // Table name
        public static final String TABLE_NAME = "learningPlace";

        // Column names
        public static final String COLUMN_LONG_NAME = "longName";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_COORDINATES = "coordinates";
        public static final String COLUMN_SEATS = "seats";
        public static final String COLUMN_INDEX = "sortIndex";
    }
}