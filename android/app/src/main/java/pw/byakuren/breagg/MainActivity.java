package pw.byakuren.breagg;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

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
        layout = (FrameLayout)findViewById(R.id.FrameLayout);

        /*
         * Yes, the Camera from android.hardware is deprecated, however
         * it should be good enough for these needs.
         */
        cam = Camera.open();
        display = new CameraDisplay(this, cam);
        layout.addView(display);

    }





    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
