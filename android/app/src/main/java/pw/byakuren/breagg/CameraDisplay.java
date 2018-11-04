package pw.byakuren.breagg;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class CameraDisplay extends SurfaceView implements SurfaceHolder.Callback{

    final int picres = 196;
    Camera camera;
    SurfaceHolder holder;

    public CameraDisplay(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Camera.Parameters params = camera.getParameters();

        /*
         * Change the orientation of the camera. This first block handles when it's in portrait mode,
         * the second block handles it in landscape.
         */

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            params.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);

        } else {
            params.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO); //enable continuous autofocus
        params.setVideoStabilization(true); //enable video stabilization
        params.setPreviewFrameRate(60);
        camera.setParameters(params);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Begin previewing the camera feed.
        camera.startPreview();
    }
}
