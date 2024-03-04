package com.example.pr26.ui.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.pr26.R;

public class AccelerometerFragment extends Fragment {
    private TextView xTextView, yTextView, zTextView;
    private int taps = 0;
    private long lastTime = 0;
    private float lastZAcc;
    private static final int SHAKE_THRESHOLD = 800;
    private static final float TIME_THRESHOLD = 5.0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelerometer, container, false);

        // Inicialitzem els TextViews
        xTextView = view.findViewById(R.id.xTextView);
        yTextView = view.findViewById(R.id.yTextView);
        zTextView = view.findViewById(R.id.zTextView);

        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // Valors de l'acceleròmetre en m/s^2
                float xAcc = sensorEvent.values[0];
                float yAcc = sensorEvent.values[1];
                float zAcc = sensorEvent.values[2];

                // Actualitzem els TextViews amb els valors de l'acceleròmetre
                xTextView.setText(String.valueOf(xAcc));
                yTextView.setText(String.valueOf(yAcc));
                zTextView.setText(String.valueOf(zAcc));

                // Detectem el double tap
                long curTime = System.currentTimeMillis();
                long timeDiff = curTime - lastTime;

                if (timeDiff > TIME_THRESHOLD) {
                    lastTime = curTime;
                    float speed = Math.abs(zAcc - lastZAcc) / timeDiff * 10000;
                    if (speed > SHAKE_THRESHOLD) {
                        taps++;
                        if (taps == 2) {
                            Toast.makeText(getActivity(), "You've done a Double Tap!", Toast.LENGTH_SHORT).show();
                            taps = 0;
                        }
                    }
                    lastZAcc = zAcc;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                // Es pot ignorar aquesta CB de moment
            }
        };

        // Seleccionem el tipus de sensor
        SensorManager sensorMgr = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // Registrem el Listener per capturar els events del sensor
        if (sensor != null) {
            sensorMgr.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        return view;
    }
}
