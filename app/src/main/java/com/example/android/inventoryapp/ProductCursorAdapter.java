package com.example.android.inventoryapp;

/**
 * Created by luisp on 23/07/2017.
 */



import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static android.R.attr.button;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    /** Quantity Variable */
    private int mQuantity;

    /** Variable for Sale Button and constant to decrease quantity by one */
    private Button mSaleButton;
    private static final int ACTION_DECREASE = -1;


    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of product attributes that we're interested in
        final int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        final int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        final int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Find the Button "SALE" which will deduct one unit from quantity
        mSaleButton = (Button) view.findViewById(R.id.button_sale);

        // Get the position for the current product and store it in the tag
        mSaleButton.setTag(cursor.getPosition());

        // Sale button listener
        mSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the position saved previously with setTag()
                int position = (int) v.getTag();

                // Move the cursor to the selected item position
                cursor.moveToPosition(position);

                // Retrieve quantity from cursor and update only if greater than zero
                int quantity = cursor.getInt(quantityColumnIndex);
                if (quantity <= 0) {
                    Toast.makeText(context, R.string.error_no_product_available, Toast
                            .LENGTH_SHORT).show();
                } else {
                    long id = cursor.getInt(idColumnIndex);
                    Uri currentItemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);

                    int rowsAffected = context.getContentResolver().update(currentItemUri, values, null, null);
                    if (rowsAffected != 1) {
                        Toast.makeText(context, R.string.error_quantity_update, Toast
                                .LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.quantity_updated_successfully, Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        // If the product quantity is empty string or null, then use some default text
        // that says "Unknown price", so the TextView isn't blank.
        if (TextUtils.isEmpty(productPrice)) {
            productPrice = context.getString(R.string.unknown_price);
        }

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
    }
}
