package com.example.android.inventoryapp3;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp3.data.ItemContract;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of Item data as its data source. This adapter knows
 * how to create list items for each row of Item data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        // Find the columns of Item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);

        // Read the Item attributes from the Cursor for the current Item
        String ItemName = cursor.getString(nameColumnIndex);
        String Itemprice = cursor.getString(priceColumnIndex);

        // Update the TextViews with the attributes for the current Item
        nameTextView.setText(ItemName);
        summaryTextView.setText(Itemprice);
    }
}
