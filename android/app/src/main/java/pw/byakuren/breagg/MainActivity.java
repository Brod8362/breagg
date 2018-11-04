package pw.byakuren.breagg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "breagg";
    Camera cam;
    FrameLayout layout;
    CameraDisplay display;
    Bitmap bitmp = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
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
        recaptureCamera(null);


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


        setBreadMulti(0);
        setEggMulti(0);


        /*
         * Set values of progress bars and percentage indicators.
         */







    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Pausing camera", Toast.LENGTH_SHORT).show();
        cam.stopPreview();
        cam.release();
        cam = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cam == null) {
            recaptureCamera(null);
        }

    }


    public void recaptureCamera(View v) {
        Toast.makeText(this, "Capturing camera", Toast.LENGTH_SHORT).show();
        cam = Camera.open();
        display = new CameraDisplay(this, cam);
        layout.addView(display);
    }

    public void focusCamera(View v) {
        cam.autoFocus(null);
    }

    public void shutter(View view) {
        dispatchTakePictureIntent();


    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle extra = data.getExtras();
        if (extra == null) {
            Toast.makeText(this, "no data in intent", Toast.LENGTH_SHORT).show();
        } else {
            bitmp = (Bitmap) extra.get("data");
        }

        }


    public void debug(View v) {
        if (bitmp == null) {
            Toast.makeText(this, "Bitmap is null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Bitmap exists", Toast.LENGTH_SHORT).show();
        }
    }

    public void process(View v) {
        if (bitmp == null) {
            Toast.makeText(this, "no bitmap", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, Integer.toString(bitmp.getByteCount()), Toast.LENGTH_SHORT).show();

        ImageClassifier imgclass = null;
        try {
            imgclass = new ImageClassifier(this);
        } catch (IOException e) {
            Toast.makeText(this, "failed to make classifier", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (imgclass == null) {
            Toast.makeText(this, "classifier is null!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<NNOutput> array = imgclass.classifyFrame(bitmp);
        
        NNOutput egg = array.get(0);
        NNOutput bread = array.get(1);
        setBreadMulti((int) Math.round(bread.getValue()*100));
        setEggMulti((int) Math.round(egg.getValue()*100));

        TextView output = findViewById(R.id.output);
        output.setText(array.get(0).toString()+"\n"+array.get(1).toString());
    }

    private void setEggMulti(int n) {
        ProgressBar eggBar = findViewById(R.id.eggBar);
        TextView eggPercent = findViewById(R.id.eggPercent);
        eggBar.setProgress(n,true);
        eggPercent.setText("("+n+"%)");
    }

    private void setBreadMulti(int n) {
        ProgressBar breadBar = findViewById(R.id.breadBar);
        TextView breadPercent = findViewById(R.id.breadPercent);
        breadBar.setProgress(n, true);
        breadPercent.setText("("+n+"%)");
    }


        /**
         * A native method that is implemented by the 'native-lib' native library,
         * which is packaged with this application.
         */
    public native String stringFromJNI();


    }

