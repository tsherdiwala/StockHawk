package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by knoxpo on 20/06/16.
 */
public class StockDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = StockDetailActivity.class.getSimpleName();

    public static final String EXTRA_STOCK_SYMBOL = TAG + ".EXTRA_STOCK_SYMBOL";
    private static final int LOADER_STOCK_DETAIL = 0;

    private String mStockSymbol;
    private LineChartView mChartView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        init();

        mStockSymbol = getIntent().getStringExtra(EXTRA_STOCK_SYMBOL);
        setTitle(mStockSymbol);


        //start the loader to fetch the details of the items stored:
        getSupportLoaderManager().initLoader(LOADER_STOCK_DETAIL, null, this);
    }

    private void init() {
        mChartView = (LineChartView) findViewById(R.id.linechart);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                QuoteProvider.Quotes.CONTENT_URI,       //Content Provider URI
                new String[]{                           // Columns to be selected
                        QuoteColumns._ID,
                        QuoteColumns.SYMBOL,
                        QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE,
                        QuoteColumns.CHANGE,
                        QuoteColumns.ISUP
                },
                QuoteColumns.SYMBOL + " =?",            //the where clause
                new String[]{                           //Where arguments
                        mStockSymbol
                },
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0)
            drawChart(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }

    private void drawChart(Cursor data) {
        LineSet lineSet = new LineSet();

        float minimumPrice = Float.MAX_VALUE;
        float maximumPrice = Float.MIN_VALUE;

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            String label = data.getString(data.getColumnIndexOrThrow(QuoteColumns.BIDPRICE));
            float price = Float.parseFloat(label);

            lineSet.addPoint(label, price);
            minimumPrice = Math.min(minimumPrice, price);
            maximumPrice = Math.max(maximumPrice, price);
        }

        lineSet.setColor(ContextCompat.getColor(this, R.color.material_blue_700))
                .setFill(ContextCompat.getColor(this, R.color.material_green_700))
                .setDotsColor(ContextCompat.getColor(this, R.color.material_red_700))
                .setThickness(4)
                .setDashed(new float[]{10f, 10f});


        mChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(ContextCompat.getColor(this, R.color.material_green_700))
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(Math.round(Math.max(0f, minimumPrice - 5f)), Math.round(maximumPrice + 5f))
                .addData(lineSet);

        if (lineSet.size() > 1){
            mChartView.show();
        }else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}
