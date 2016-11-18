package cm.aptoide.pt.v8engine;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by jdandrade on 14/11/2016.
 */
public class OpenGLES20Activity extends Activity {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_opengl);

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.visualizer);
    surfaceView.setZOrderOnTop(true);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
  }

  @Override protected void onPause() {
    super.onPause();
    overridePendingTransition(0, 0);
  }
}
