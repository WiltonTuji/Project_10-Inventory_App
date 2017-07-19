package com.example.android.project10_wiltontuji;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.project10_wiltontuji.data.ProductContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView productListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_new_product:
                Intent intent = new Intent(this, EditorActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_all_products:
                alertDeleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDeleteAllProducts(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_products_confirmation);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAllProducts();
                    return;
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
    }

    private void deleteAllProducts() {
        getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PHOTO,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE};

        return new CursorLoader(this, ProductContract.ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
