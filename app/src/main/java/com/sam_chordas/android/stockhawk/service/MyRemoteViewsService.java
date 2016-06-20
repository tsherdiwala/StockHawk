package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by knoxpo on 20/06/16.
 */
public class MyRemoteViewsService  extends RemoteViewsService {

    private static final String TAG = MyRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {


        return new RemoteViewsFactory() {
            private Cursor stockData = null;


            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {

                Log.d(TAG, "onDataSetChanged: Service called");

                if (stockData != null) {
                    stockData.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                stockData = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{
                                QuoteColumns._ID,
                                QuoteColumns.SYMBOL,
                                QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE,
                                QuoteColumns.CHANGE,
                                QuoteColumns.ISUP
                        },
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},                  // get the current value for the widget
                        null
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (stockData != null) {
                    stockData.close();
                    stockData = null;
                }
            }

            @Override
            public int getCount() {
                return stockData == null ? 0 : stockData.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        stockData == null || !stockData.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);
                String symbol = stockData.getString(stockData.getColumnIndex(QuoteColumns.SYMBOL));

                views.setTextViewText(R.id.stock_symbol, symbol);
                views.setTextViewText(R.id.bid_price, stockData.getString(stockData.getColumnIndex(QuoteColumns.BIDPRICE)));
                views.setTextViewText(R.id.change, stockData.getString(stockData.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));


                if (stockData.getInt(stockData.getColumnIndex(QuoteColumns.ISUP)) == 1) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (stockData.moveToPosition(position))
                    return stockData.getLong(stockData.getColumnIndexOrThrow(QuoteColumns._ID));
                return -1; //invalid entry
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
