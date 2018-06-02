package guterman.alon.remotecontrolledcar;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Sensor implements SensorEventListener {

    private final int sensorType =  android.hardware.Sensor.TYPE_ROTATION_VECTOR;
    private SensorManager mSensorManager;
    private android.hardware.Sensor mRotationVectorSensor;
    private final float[] rotMat = new float[16];
    float[] vals = new float[3];
    public int y_Value = 10;
    public int x_Value = 10;

    @Override
    public void onAccuracyChanged(android.hardware.Sensor arg0, int arg1)
    {
        //Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == sensorType){
            SensorManager.getRotationMatrixFromVector(rotMat,event.values);
            SensorManager.remapCoordinateSystem(rotMat,SensorManager.AXIS_X, SensorManager.AXIS_Y,rotMat);
            SensorManager.getOrientation(rotMat, vals);

            // Optionally convert the result from radians to degrees
            y_Value = (int) Math.toDegrees(vals[1]);
            x_Value = (int) Math.toDegrees(vals[2]);
        }
    }

    public Sensor(Context context){
        mSensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);

        // initialize the rotation matrix to identity
        rotMat[0] = 1;
        rotMat[4] = 1;
        rotMat[8] = 1;
        rotMat[12] = 1;
    }

    public void unregister(){
        mSensorManager.unregisterListener(this);
    }

    public void register(){
        mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
    }

}
