package guterman.alon.remotecontrolledcar;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class WriteToBTDeviceTimer extends MainActivity {

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    int finalDirection;
    int tempFinalSpeed;
    String finalSpeed;
    public String messageSent;

    public void startTimer(TCPClient tcpClient, Sensor sensor, SeekBarClass seekBarClass) {
        timer = new Timer();
        initializeTimerTask(tcpClient, sensor, seekBarClass);
        //timer.schedule(timerTask, 5000, 80); // 1 seconds, just for try it works
        timer.schedule(timerTask, 5000, 80); // 1 seconds, just for try it works
    }

    public void stoptimertask() {
        if (timer != null) {
            timerTask.cancel();
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask(final TCPClient tcpClient, final Sensor sensor, final SeekBarClass seekBarClass) {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try{
                            if (tcpClient.isReady)
                            {
                                String direction = "H";

                                finalDirection = sensor.y_Value + 45;
                                if (finalDirection >= 90) finalDirection = 89;
                                if (finalDirection <= 0) finalDirection = 1;
                                if (finalDirection >= 1 && finalDirection <= 9)
                                {
                                    direction += "0" + Integer.toString(finalDirection);
                                }
                                else
                                {
                                    direction += Integer.toString(finalDirection);
                                }

                                // Get Progress from SeekBarClass Progress Property //
                                tempFinalSpeed = sensor.x_Value;
                                if (tempFinalSpeed >= 0)
                                {
                                    direction += "U";
                                }
                                else
                                {
                                    direction += "D";
                                }

                                tempFinalSpeed = Math.abs(tempFinalSpeed);
                                if (tempFinalSpeed < 8) tempFinalSpeed = 0;
                                if (tempFinalSpeed >= 50) tempFinalSpeed = 50;
                                finalSpeed = String.format("%02d", tempFinalSpeed);

                                direction += finalSpeed;

                                messageSent = direction;
                                tcpClient.SendMessage(direction);
                            }
                        }
                        catch (Exception ex){

                        }

                    }
                });
            }
        };
    }
}
