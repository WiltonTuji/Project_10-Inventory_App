package com.example.android.project10_wiltontuji.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Adailto on 08/06/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "deposit.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PHOTO + " BLOB, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_NAME + " TEXT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_EMAIL + " TEXT);";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
