package pw.byakuren.breagg;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Camera cam;
    FrameLayout layout;
    CameraDisplay display;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Define frame layout.
        layout = findViewById(R.id.FrameLayout);

        /*
         * Yes, the Camera from android.hardware is deprecated, however
         * it should be good enough for these needs.
         *
         */
        cam = Camera.open();
        display = new CameraDisplay(this, cam);
        layout.addView(display);


        // Show the number of available cameras.

        TextView camCount = findViewById(R.id.camCountNum);
        camCount.setText(Integer.toString(Camera.getNumberOfCameras())); // using the static toString because it didn't work otherwise


        /*
         * Retrieve the bars and values that will need to be modified.
         */


        ProgressBar eggBar = findViewById(R.id.eggBar);
        ProgressBar breadBar = findViewById(R.id.breadBar);
        TextView eggPercent = findViewById(R.id.eggPercent);
        TextView breadPercent = findViewById(R.id.breadPercent);


        /*
         * Placeholder values for the percentages of the bars. Later, this will be achieved via
         * the tensorflow output.
         */

        double eggVal = 0.34;
        double breadVal = 0.16;

        /*
         * Set values of progress bars and percentage indicators.
         */


        eggBar.setProgress((int) Math.round(eggVal*100),true);
        breadBar.setProgress((int) Math.round(breadVal*100), true);
        //TODO replace this with a proper string placeholder
        eggPercent.setText("("+(int)Math.round(eggVal*100) +"%)");
        breadPercent.setText("("+(int)Math.round(breadVal*100) +"%)");


    }

    public void recaptureCamera(View v) {

        Toast.makeText(this, "Trying to recapture the camera.", Toast.LENGTH_SHORT).show();
        cam = Camera.open();
        cam.startPreview();

    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
