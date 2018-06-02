package guterman.alon.remotecontrolledcar;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private TCPClient tcpClient;
    private WriteToBTDeviceTimer writeToBTDeviceTimer;
    private guterman.alon.remotecontrolledcar.Sensor sensor;
    TextView y_Text;
    TextView x_Text; // added 29.1.16
    TextView messagesRecv;
    TextView SeekBar_TextView;
    SeekBar seekBar;
    private SeekBarClass seekBarClass;
    Button StopButton;

    //VideoView videoview;
    WebView wb;

    //String VideoURL = "http://192.168.0.155:8081/";

    // Timer //
    Timer timer = new Timer();
    TimerTask timerTask;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        y_Text = findViewById(R.id.text2);
        x_Text = findViewById(R.id.xAxis);
        messagesRecv =  findViewById(R.id.textView_MessagesRecv);
        SeekBar_TextView =  findViewById(R.id.textView_SeekBar);
        seekBar =  findViewById(R.id.SeekBar);
        //videoview = (VideoView) findViewById(R.id.videoView);
        wb=findViewById(R.id.webView);

        seekBarClass = new SeekBarClass(seekBar, 50);
        seekBar.setOnSeekBarChangeListener(seekBarClass);
        sensor = new guterman.alon.remotecontrolledcar.Sensor(this);
        addListenerOnButton();

        // connect to the server
        new connectTask().execute("");
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tcpClient.SendMessage("In Main Activity");

        writeToBTDeviceTimer = new WriteToBTDeviceTimer();
        writeToBTDeviceTimer.startTimer(tcpClient, sensor, seekBarClass);

        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 10); // 1000 seconds, just for try it works

        //wb.getSettings().setJavaScriptEnabled(true);
        //wb.getSettings().setLoadWithOverviewMode(true);
        //wb.getSettings().setUseWideViewPort(true);
        //wb.getSettings().setBuiltInZoomControls(true);
        //wb.getSettings().setPluginState(WebSettings.PluginState.ON);
        //wb.getSettings().setPluginsEnabled(true);
        //wb.setWebViewClient(new HelloWebViewClient());
        //wb.loadUrl("http://192.168.43.155:8081");

        //Uri video = Uri.parse(VideoURL);
        //videoview.setVideoURI(video);
        //videoview.requestFocus();
        //videoview.start();
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    public void addListenerOnButton(){
        StopButton =  findViewById(R.id.StopButton);
        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    tcpClient.StopTCPClient(writeToBTDeviceTimer);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /////////////////////////////////

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        y_Text.setText(Integer.toString(sensor.y_Value));
                        x_Text.setText(Integer.toString(sensor.x_Value)); // added 29.1.16
                        SeekBar_TextView.setText(Integer.toString(seekBarClass.progress));
                        messagesRecv.setText(writeToBTDeviceTimer.messageSent);
                    }
                });
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensor.register();
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensor.unregister();
    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            tcpClient = new TCPClient("192.168.43.155", 9999);

            tcpClient.Init();

            return null;
        }

    }
}
