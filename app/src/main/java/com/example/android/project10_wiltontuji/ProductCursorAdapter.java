package com.example.android.project10_wiltontuji;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.project10_wiltontuji.data.ProductContract;
import com.example.android.project10_wiltontuji.data.ProductContract.ProductEntry;
import com.example.android.project10_wiltontuji.data.ProductDbHelper;

import java.io.ByteArrayInputStream;

/**
 * Created by Adailto on 08/06/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView photoImageView = (ImageView) view.findViewById(R.id.product_photo);
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);

        int productIdColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int photoColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PHOTO);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        final int productId = cursor.getInt(productIdColumnIndex);
        byte[] productPhotoByte = cursor.getBlob(photoColumnIndex);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(productPhotoByte);
        Bitmap productPhoto = BitmapFactory.decodeStream(byteArrayInputStream);
        String productName = cursor.getString(nameColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        float productPrice = cursor.getFloat(priceColumnIndex);

        photoImageView.setImageBitmap(productPhoto);
        nameTextView.setText(productName);
        quantityTextView.setText(context.getString(R.string.quantity) + ": " + Integer.toString(productQuantity));
        priceTextView.setText(context.getString(R.string.price) + ": " + String.format("%.2f", productPrice));

        ImageView buyOneButton = (ImageView) view.findViewById(R.id.buy_one_button);
        buyOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(String.valueOf(ProductEntry.CONTENT_URI));
                ProductDbHelper mDbHelper = new ProductDbHelper(context);
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                if (productQuantity > 0) {
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity - 1);
                    database.update(ProductEntry.TABLE_NAME, values, ProductEntry._ID + " = " + productId, null);
                    context.getContentResolver().notifyChange(uri, null);
                    Toast.makeText(context, R.string.bought_one, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
