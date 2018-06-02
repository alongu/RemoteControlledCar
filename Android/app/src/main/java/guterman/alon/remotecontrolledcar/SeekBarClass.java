package guterman.alon.remotecontrolledcar;

import android.app.Activity;
import android.widget.SeekBar;

public class SeekBarClass extends Activity implements SeekBar.OnSeekBarChangeListener {
    public int progress;
    private int startingProgress;

    // constructor //
    public SeekBarClass(SeekBar seekBar, int startingProgress) {
        progress = 0;
        this.startingProgress = startingProgress;
        seekBar.setProgress(startingProgress);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
        progress = progressValue - startingProgress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}


