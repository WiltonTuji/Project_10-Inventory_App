package com.example.android.project10_wiltontuji.data;

import android.content.ContentResolver;
import android.media.Image;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.widget.SimpleCursorAdapter;

/**
 * Created by Adailto on 08/06/2017.
 */

public final class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.project10_wiltontuji";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    private ProductContract() {
    }

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
        public static final String COLUMN_PRODUCT_SELLER_NAME = "seller_name";
        public static final String COLUMN_PRODUCT_SELLER_EMAIL = "seller_email";
        public static final String COLUMN_PRODUCT_PHOTO = "photo";

    }

}
