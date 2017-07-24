package com.example.android.inventoryapp3;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import com.example.android.inventoryapp3.data.ItemContract;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of Item data as its data source. This adapter knows
 * how to create list items for each row of Item data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {

    private Button mSellButon;
    private Button mOrderMoreButon;
    private ImageView mImageView;

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemCursorAdapter(Context context, Cursor c) {
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
     * This method binds the Item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current Item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView brandTextView = (TextView) view.findViewById(R.id.brand);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        mSellButon = (Button) view.findViewById(R.id.buttonSell);
        mOrderMoreButon = (Button) view.findViewById(R.id.buttonOrderMore);
        mImageView = (ImageView) view.findViewById(R.id.imageView);

        // Find the columns of Item attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        int brandColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_BRAND);
        int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE);

        // Read the Item attributes from the Cursor for the current Item
        final int itemId = cursor.getInt(idColumnIndex);
        final String ItemName = cursor.getString(nameColumnIndex);
        final String ItemBrand = cursor.getString(brandColumnIndex);
        final String ItemPrice = cursor.getString(priceColumnIndex);
        final int ItemQuantity = cursor.getInt(quantityColumnIndex);
        byte[] image = cursor.getBlob(imageColumnIndex);
        final int rowID = cursor.getInt(idColumnIndex);
        if (image != null && image.length > 0) {
            Bitmap itemsBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            mImageView.setImageBitmap(itemsBitmap);
        }

        // Update the TextViews with the attributes for the current Item
        nameTextView.setText(ItemName);
        brandTextView.setText(ItemBrand);
        priceTextView.setText("Price:" + ItemPrice);
        quantityTextView.setText("Quantity: " + ItemQuantity);

        mSellButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentInventoryItem = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, itemId);

                if (ItemQuantity == 0) {
                    Toast.makeText(view.getContext(), "Out of stock", Toast.LENGTH_SHORT).show();
                } else {
                    final int newQuantity = ItemQuantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, newQuantity);

                    if (context.getContentResolver().update(currentInventoryItem, values, null, null) == 1)
                        quantityTextView.setText(Integer.toString(newQuantity));
                }
            }
        });

        mOrderMoreButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create email message
                final String message = "Order request: order more items of" +
                        "\n" + ItemName + " - " + ItemBrand;

                //Send intent
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order request");
                intent.putExtra(Intent.EXTRA_TEXT, message);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditorActivity.class);

                // Form the content URI that represents the specific Item that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ItemEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.Items/Items/2"
                // if the Item with ID 2 was clicked on.
                Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, itemId);

                // Set the URI on the data field of the intent
                intent.setData(currentItemUri);

                // Launch the {@link EditorActivity} to display the data for the current Item.
                context.startActivity(intent);
            }
        });
    }
}
