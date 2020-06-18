package com.example.dalnec.prueba_blue;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;

public class GraficosActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "GraficosActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private  Sensor sensors;
    LineChart graficaLinea;

    private Thread thread;
    private boolean plotData = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graficos);
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<sensors.size(); i++){
            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        //CREAMOS LISTA CON VALORES DE ENTRADAS
        graficaLinea = new LineChart(this);
        //agregamos el maianlayout
        graficaLinea = findViewById(R.id.gradficaLinea);
        //mChart = findViewById(R.id.mainLayout);
        //personalizamos el LineChart
        graficaLinea.setDescription("");
        graficaLinea.setNoDataTextDescription("No Datos");
        //habilita el Valor de los HighLigthing
        //mChart.setHighlightPerDragEnabled(true);
        //mChart.setHighlightPerTapEnabled(true);
        //habilita touch gestures
        graficaLinea.setTouchEnabled(true);
        //habilita scaling and dragging
        graficaLinea.setScaleEnabled(true);
        graficaLinea.setDragEnabled(true);
        graficaLinea.setDrawGridBackground(false);
        //habilita pinch zoom para evitar scaling X y Y axis por separado
        graficaLinea.setPinchZoom(true);
        //Color alternativo de fondo
        graficaLinea.setBackgroundColor(Color.DKGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        //Agrega los datos al LineChart
        graficaLinea.setData(data);
        //Optenemos la Leyenda objeto
        Legend l =  graficaLinea.getLegend();
        //personalizamos la leyenda
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);
        l.setEnabled(false);

        XAxis x1 = graficaLinea.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(true);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1 = graficaLinea.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(100f);
        y1.setDrawGridLines(true);

        YAxis y12 = graficaLinea.getAxisRight();
        y12.setTextColor(Color.WHITE);
        y12.setAxisMaxValue(100f);
    }
    private void addEntry(SensorEvent event){
        LineData data = graficaLinea.getData();
        if (data != null){
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null){
                set = createSet();
                data.addDataSet(set);
            }
            //valores aleatorios
            //data.addXValue("");
            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
            //notificar cabio de datos
            graficaLinea.notifyDataSetChanged();
            //limita el numero limite de entrada
            graficaLinea.setVisibleXRange(12f,10f);//datos en el eje X
            //scroll to the las entry
            graficaLinea.moveViewToX(data.getEntryCount());
        }
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    //metodo para crear set
    private LineDataSet createSet(){
        LineDataSet set =  new LineDataSet(null, "");
        //set.setDrawCubic(false);//Tiene efecto negativo en el rendimiento, hace grafica con curva
        //set.setCubicIntensity(1f);
        /*set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.getCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(1.5f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 11));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);*/
        set.enableDashedLine(10f, 5f, 0f);
        set.enableDashedHighlightLine(10f, 5f, 0f);
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        return set;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(plotData){
            addEntry(event);
            plotData = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(GraficosActivity.this);
        thread.interrupt();
        super.onDestroy();
    }
}
