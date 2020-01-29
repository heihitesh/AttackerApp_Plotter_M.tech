package com.itshiteshverma.attackerapp.ui.gallery;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.itshiteshverma.attackerapp.DatabaseHelper;
import com.itshiteshverma.attackerapp.R;
import com.itshiteshverma.attackerapp.XYValue;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;

import static com.itshiteshverma.attackerapp.MainActivity.toastHelper;

public class GalleryFragment extends Fragment {

    public static final double MAX_Y_BOUNDS = 150;
    public static double MIN_Y_BOUNDS = 0;

    public static final double MAX_X_BOUNDS = 150;
    public static double MIN_X_BOUNDS = 0;

    private GalleryViewModel galleryViewModel;
    View view;
    DatabaseHelper dbHelper;

    private static final String TAG = "HIT_TAG";
    //add PointsGraphSeries of DataPoint type
    PointsGraphSeries<DataPoint> xySeries;
    GraphView mScatterPlot;
    //make xyValueArray global
    private ArrayList<XYValue> xyValueArray;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        initilize();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_value_menu_xml, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_value:
                final Dialog dialog = new Dialog(getActivity());

                dialog.requestWindowFeature(1);
                dialog.setContentView(R.layout.dialog_add_value_dark);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.setCancelable(true);
                dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText mX = dialog.findViewById(R.id.etXValue);
                final EditText mY = dialog.findViewById(R.id.etYValue);
                final Button btnAddPt = dialog.findViewById(R.id.bAddPoint);
                btnAddPt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!mX.getText().toString().equals("") && !mY.getText().toString().equals("")) {
                            double x = Double.parseDouble(mX.getText().toString());
                            double y = Double.parseDouble(mY.getText().toString());
                            Log.d(TAG, "onClick: Adding a new point. (x,y): (" + x + "," + y + ")");
                            xyValueArray.add(new XYValue(x, y));
                            dialog.dismiss();
                            init();

                        } else {
                            toastMessage("You must fill out both fields!");
                        }
                    }
                });

                dialog.show();
                break;

            case R.id.action_refresh:

                final Dialog dialog2 = new Dialog(getActivity());

                dialog2.requestWindowFeature(1);
                dialog2.setContentView(R.layout.dialog_starting_point_dark);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog2.setCancelable(true);
                dialog2.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });
                final EditText startX = dialog2.findViewById(R.id.etXValue);
                final EditText startY = dialog2.findViewById(R.id.etYValue);
                final Button btnStartPt = dialog2.findViewById(R.id.bAddPoint);
                btnStartPt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!startX.getText().toString().equals("") && !startY.getText().toString().equals("")) {
                            MIN_X_BOUNDS = Double.parseDouble(startX.getText().toString());
                            MIN_Y_BOUNDS = Double.parseDouble(startY.getText().toString());
                            dbHelper.getValue(xyValueArray);
                            init();
                            dialog2.dismiss();

                        } else {
                            toastMessage("You must fill out both fields!");
                        }
                    }
                });

                dialog2.show();


                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initilize() {
        dbHelper = new DatabaseHelper(getActivity());
        mScatterPlot = (GraphView) view.findViewById(R.id.scatterPlot);
        xyValueArray = new ArrayList<>();
        init();

    }

    private void init() {
        //declare the xySeries Object
        Log.d(TAG, "Inside Init");
        xySeries = new PointsGraphSeries<>();
        //little bit of exception handling for if there is no data.
        if (xyValueArray.size() != 0) {
            createScatterPlot();
        } else {
            Log.d(TAG, "onCreate: No data to plot.");
        }
    }

    private void createScatterPlot() {
        Log.d(TAG, "createScatterPlot: Creating scatter plot.");

        //sort the array of xy values
        xyValueArray = sortArray(xyValueArray);

        //add the data to the series
        for (int i = 0; i < xyValueArray.size(); i++) {
            try {
                double x = xyValueArray.get(i).getX();
                double y = xyValueArray.get(i).getY();
                xySeries.appendData(new DataPoint(x, y), true, 1000);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "createScatterPlot: IllegalArgumentException: " + e.getMessage());
            }
        }

        //set some properties
        xySeries.setShape(PointsGraphSeries.Shape.POINT);
        xySeries.setColor(Color.RED);
        xySeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "Values : X =" + dataPoint.getX() + ", Y =" + dataPoint.getY(), Toast.LENGTH_LONG).show();
            }
        });
        xySeries.setSize(10f);

        //set Scrollable and Scaleable
        mScatterPlot.getViewport().setScalable(false);
        mScatterPlot.getViewport().setScalableY(false);
        mScatterPlot.getViewport().setScrollable(false);
        mScatterPlot.getViewport().setScrollableY(false);

        //set manual x bounds
        mScatterPlot.getViewport().setYAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxY(MAX_Y_BOUNDS);
        mScatterPlot.getViewport().setMinY(MIN_Y_BOUNDS);

        //set manual y bounds
        mScatterPlot.getViewport().setXAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxX(MAX_X_BOUNDS);
        mScatterPlot.getViewport().setMinX(MIN_X_BOUNDS);

        mScatterPlot.addSeries(xySeries);
    }

    /**
     * Sorts an ArrayList<XYValue> with respect to the x values.
     *
     * @param array
     * @return
     */
    private ArrayList<XYValue> sortArray(ArrayList<XYValue> array) {
        /*
        //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
         */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(), 2))));
        int m = array.size() - 1;
        int count = 0;
        //Log.d(TAG, "sortArray: Sorting the XYArray.");


        while (true) {
            m--;
            if (m <= 0) {
                m = array.size() - 1;
            }
            //  Log.d(TAG, "sortArray: m = " + m);
            try {
                //print out the y entrys so we know what the order looks like
                //Log.d(TAG, "sortArray: Order:");
                //for(int n = 0;n < array.size();n++){
                //Log.d(TAG, "sortArray: " + array.get(n).getY());
                //}
                double tempY = array.get(m - 1).getY();
                double tempX = array.get(m - 1).getX();
                if (tempX > array.get(m).getX()) {
                    array.get(m - 1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m - 1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                } else if (tempX == array.get(m).getX()) {
                    count++;
                    //   Log.d(TAG, "sortArray: count = " + count);
                } else if (array.get(m).getX() > array.get(m - 1).getX()) {
                    count++;
                    //   Log.d(TAG, "sortArray: count = " + count);
                }
                //break when factorial is done
                if (count == factor) {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "sortArray: ArrayIndexOutOfBoundsException. Need more than 1 data point to create Plot." +
                        e.getMessage());
                break;
            }
        }
        return array;
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}