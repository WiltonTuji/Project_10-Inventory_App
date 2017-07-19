package com.example.android.project10_wiltontuji;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.project10_wiltontuji.data.ProductContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static android.content.Intent.ACTION_SENDTO;
import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.View.GONE;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    private static final int CAMERA_REQUEST = 1;
    private Uri mCurrentProductUri;
    private ImageView mPhotoImageView;
    private Bitmap mProductPhoto;
    private byte[] mPhotoInByte;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mDescriptionEditText;
    private EditText mSellerNameEditText;
    private EditText mSellerEmailEditText;
    private Button mRequestProductButton;
    private Button mRestockProductButton;
    private Button mSaleProductButton;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(R.string.add_new_product_title);
            invalidateOptionsMenu();
            mRequestProductButton = (Button) findViewById(R.id.request_product_button);
            mRequestProductButton.setVisibility(GONE);
            mRestockProductButton = (Button) findViewById(R.id.restock_product_button);
            mRestockProductButton.setVisibility(GONE);
            mSaleProductButton = (Button) findViewById(R.id.sale_product_button);
            mSaleProductButton.setVisibility(GONE);
        } else {
            setTitle(R.string.edit_product_title);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mPhotoImageView = (ImageView) findViewById(R.id.take_photo_image_view);
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mPriceEditText = (EditText) findViewById(R.id.price_edit_text);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        mDescriptionEditText = (EditText) findViewById(R.id.description_edit_text);
        mSellerNameEditText = (EditText) findViewById(R.id.seller_name_edit_text);
        mSellerEmailEditText = (EditText) findViewById(R.id.seller_email_edit_text);

        mPhotoImageView.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mSellerNameEditText.setOnTouchListener(mTouchListener);
        mSellerEmailEditText.setOnTouchListener(mTouchListener);

        mRequestProductButton = (Button) findViewById(R.id.request_product_button);
        mRequestProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestConfirmationDialog();
            }
        });

        mRestockProductButton = (Button) findViewById(R.id.restock_product_button);
        mRestockProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRestockProductConfirmationDialog();
            }
        });

        mSaleProductButton = (Button) findViewById(R.id.sale_product_button);
        mSaleProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaleConfirmationDialog();
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
                if (TextUtils.isEmpty(mNameEditText.getText()) || TextUtils.isEmpty(mPriceEditText.getText()) || TextUtils.isEmpty(mQuantityEditText.getText())
                        || TextUtils.isEmpty(mDescriptionEditText.getText()) || TextUtils.isEmpty(mSellerNameEditText.getText()) || TextUtils.isEmpty(mSellerEmailEditText.getText())) {
                    return;
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            mProductPhoto = (Bitmap) data.getExtras().get("data");
            mPhotoImageView.setImageBitmap(mProductPhoto);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_product);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_product:
                saveProduct();
                if (TextUtils.isEmpty(mNameEditText.getText()) || TextUtils.isEmpty(mPriceEditText.getText()) || TextUtils.isEmpty(mQuantityEditText.getText())
                        || TextUtils.isEmpty(mDescriptionEditText.getText()) || TextUtils.isEmpty(mSellerNameEditText.getText()) || TextUtils.isEmpty(mSellerEmailEditText.getText())) {
                } else {
                    finish();
                }
                return true;

            case R.id.delete_product:
                alertDeleteProduct();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void saveProduct() {


        String productName = mNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productName)) {
            alertNoName();
            return;
        }

        String productPriceString = mPriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productPriceString)) {
            alertNoPrice();
            return;
        }
        float productPriceFloat = Float.parseFloat(productPriceString);

        String productQuantityString = mQuantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productQuantityString)) {
            alertNoQuantity();
            return;
        }
        int productQuantityInt = Integer.parseInt(productQuantityString);

        String productDescription = mDescriptionEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productDescription)) {
            alertNoDescription();
            return;
        }

        String productSellerName = mSellerNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productSellerName)) {
            alertNoSellerName();
            return;
        }

        String productSellerEmail = mSellerEmailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(productSellerEmail)) {
            alertNoSellerEmail();
            return;
        }

        if (mPhotoInByte == null && mProductPhoto == null) {
            mProductPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mProductPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            mPhotoInByte = byteArrayOutputStream.toByteArray();
        }
        if (mPhotoInByte != null && mProductPhoto != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mProductPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            mPhotoInByte = byteArrayOutputStream.toByteArray();
        }
        if (mPhotoInByte == null && mProductPhoto != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mProductPhoto.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            mPhotoInByte = byteArrayOutputStream.toByteArray();
        }

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PHOTO, mPhotoInByte);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPriceFloat);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantityInt);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION, productDescription);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_NAME, productSellerName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_EMAIL, productSellerEmail);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, R.string.error_saving_product, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.saving_product_succesful, Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.error_updating_product, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.updating_product_succesful, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void alertNoName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_product_name);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertNoPrice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_product_price);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertNoQuantity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_product_quantity);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertNoDescription() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_product_descrption);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertNoSellerName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_seller_name);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertNoSellerEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_seller_email);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertDeleteProduct(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
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

    private void deleteProduct() {

        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.error_deleting_product, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.deleting_product_succesful, Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

    private void showRequestConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_order_quantity);
        final EditText input = new EditText(this);
        input.setInputType(TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton(R.string.send_order, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String requestQuantityString = input.getText().toString();
                String productName = mNameEditText.getText().toString();
                String productSellerEmail = mSellerEmailEditText.getText().toString();

                Intent intent = new Intent(ACTION_SENDTO);
                intent.setType("message/rfc822");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{productSellerEmail});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.new_order));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text_part1_of_2) + " " + requestQuantityString + " " + getString(R.string.email_text_part2_of_2) + " " + productName + ".");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void showRestockProductConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.restock);
        final EditText input = new EditText(this);
        input.setInputType(TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton(R.string.send_order, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String requestQuantityString = input.getText().toString();
                int requestQuantityInt = Integer.parseInt(requestQuantityString);
                int productQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity + requestQuantityInt);
                getContentResolver().update(mCurrentProductUri, values, null, null);
                getApplicationContext().getContentResolver().notifyChange(mCurrentProductUri, null);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void showSaleConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.insert_sale_quantity);
        final EditText input = new EditText(this);
        input.setInputType(TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton(R.string.sale, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int saleQuantity = Integer.parseInt(input.getText().toString());
                ContentValues values = new ContentValues();
                int productQuantity = Integer.parseInt(mQuantityEditText.getText().toString());
                if (saleQuantity > productQuantity) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
                    builder.setMessage(R.string.requesting_more_than_available);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    int newProductQuantity = productQuantity - saleQuantity;
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newProductQuantity);
                    getContentResolver().update(mCurrentProductUri, values, null, null);
                    getApplicationContext().getContentResolver().notifyChange(mCurrentProductUri, null);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.have_unsaved_changes);
        builder.setPositiveButton(R.string.yes, discardButtonClickListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PHOTO,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_EMAIL};

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int photoColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PHOTO);
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int descriptionColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION);
            int sellerNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_NAME);
            int sellerEmailColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SELLER_EMAIL);

            mPhotoInByte = cursor.getBlob(photoColumnIndex);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mPhotoInByte);
            Bitmap productPhoto = BitmapFactory.decodeStream(byteArrayInputStream);
            String productName = cursor.getString(nameColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            float productPrice = cursor.getFloat(priceColumnIndex);
            String productDescription = cursor.getString(descriptionColumnIndex);
            String productSellerName = cursor.getString(sellerNameColumnIndex);
            String productSellerEmail = cursor.getString(sellerEmailColumnIndex);

            mPhotoImageView.setImageBitmap(productPhoto);
            mNameEditText.setText(productName);
            mQuantityEditText.setText(Integer.toString(productQuantity));
            mPriceEditText.setText(Float.toString(productPrice));
            mDescriptionEditText.setText(productDescription);
            mSellerNameEditText.setText(productSellerName);
            mSellerEmailEditText.setText(productSellerEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mDescriptionEditText.setText("");
        mSellerNameEditText.setText("");
        mSellerEmailEditText.setText("");
    }
}
