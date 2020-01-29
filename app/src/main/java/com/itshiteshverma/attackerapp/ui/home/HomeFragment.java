package com.itshiteshverma.attackerapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itshiteshverma.attackerapp.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private HomeViewModel homeViewModel;
    View view;
    private ScatterChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        view = inflater.inflate(R.layout.fragment_home, container, false);
        loadChart();
        return view;
    }


    private void loadChart() {
        tvX = view.findViewById(R.id.tvXMax);
        tvY = view.findViewById(R.id.tvYMax);

        seekBarX = view.findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);

        seekBarY = view.findViewById(R.id.seekBar2);
        seekBarY.setOnSeekBarChangeListener(this);

        chart = view.findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setMaxHighlightDistance(50f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
//        chart.setScaleYEnabled(false);
//        chart.setScaleXEnabled(false);

        chart.setMaxVisibleValueCount(200);
        chart.setPinchZoom(true);

        seekBarX.setProgress(45);
        seekBarY.setProgress(100);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        // l.setTypeface(tfLight);
        l.setXOffset(5f);

        YAxis yl = chart.getAxisLeft();
        // yl.setTypeface(tfLight);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);

        XAxis xl = chart.getXAxis();
        // xl.setTypeface(tfLight);
        xl.setDrawGridLines(false);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        ArrayList<Entry> values1 = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();
        ArrayList<Entry> values3 = new ArrayList<>();

        //values2.add(new Entry(0,0));
        for (int i = 0; i <= 20; i++) {
            values2.add(new Entry(0, i));

        }
        //  values2.add(new Entry(90,90));

//        for (int i = 0; i < seekBarX.getProgress(); i++) {
//            float val = (float) (Math.random() * seekBarY.getProgress()) + 3;
//            values1.add(new Entry(i, val));
//        }
//
//        for (int i = 0; i < seekBarX.getProgress(); i++) {
//            float val = (float) (Math.random() * seekBarY.getProgress()) + 3;
//            values2.add(new Entry(i+0.33f, val));
//        }
//
//        for (int i = 0; i < seekBarX.getProgress(); i++) {
//            float val = (float) (Math.random() * seekBarY.getProgress()) + 3;
//            values3.add(new Entry(i+0.66f, val));
//        }

        // create a dataset and give it a type
        ScatterDataSet set1 = new ScatterDataSet(values1, "DS 1");
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        ScatterDataSet set2 = new ScatterDataSet(values2, "DS 2");
        set2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set2.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[3]);
        set2.setScatterShapeHoleRadius(5f);
        set2.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        ScatterDataSet set3 = new ScatterDataSet(values3, "DS 3");
        //set3.setShapeRenderer(new CustomScatterShapeRenderer());
        set3.setColor(ColorTemplate.COLORFUL_COLORS[2]);

        set1.setScatterShapeSize(8f);
        set2.setScatterShapeSize(8f);
        set3.setScatterShapeSize(8f);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        dataSets.add(set2);
        dataSets.add(set3);

        // create a data object with the data sets
        ScatterData data = new ScatterData(dataSets);
        //  data.setValueTypeface(tfLight);

        chart.setData(data);
        chart.invalidate();
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}