package com.kesco.adk.boucingball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class BouncingBall extends TextureView implements TextureView.SurfaceTextureListener {
  private RenderThread thread;

  private final Paint circlePaint;
  private int radius = 100;
  private boolean reverse = false;

  private final Paint cometPaint;

  private final Paint cleanPaint;
  private int degree = 0;
  private boolean dReverse = false;

  public BouncingBall(Context context) {
    this(context, null);
  }

  public BouncingBall(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BouncingBall(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    setOpaque(false);

    circlePaint = new Paint();
    circlePaint.setColor(Color.parseColor("#fe10f2"));
    circlePaint.setAntiAlias(true);

    cleanPaint = new Paint();
    cleanPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    cometPaint = new Paint();
    cometPaint.setColor(Color.parseColor("#12f380"));
    cometPaint.setAntiAlias(true);

    setSurfaceTextureListener(this);
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
    if (thread == null) {
      thread = new RenderThread();
      thread.start();
    }
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
    if (thread != null) thread.interrupt();
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
  }

  private class RenderThread extends Thread {
    @Override
    public void run() {
      final float centerX = getMeasuredWidth() / 2.0f;
      final float centerY = getMeasuredHeight() / 2.0f;

      while (!isInterrupted()) {
        Canvas canvas = null;
        try {
          canvas = lockCanvas();
          if (canvas == null) continue;
          /* Clear Canvas */
          canvas.drawPaint(cleanPaint);

          /* Draw center circle */
          canvas.drawCircle(centerX, centerY, radius, circlePaint);

          canvas.save();
          canvas.rotate(degree, centerX, centerY);
          canvas.drawCircle(centerX, centerY - 180, 20, cometPaint);
          canvas.drawCircle(centerX, centerY + 180, 20, cometPaint);
          canvas.restore();

          if (reverse) {
            radius -= 10;
            if (radius < 70) reverse = false;
          } else {
            radius += 10;
            if (radius > 150) reverse = true;
          }

          if (dReverse) {
            degree -= 5;
            if (degree <= 60) dReverse = false;
          } else {
            degree += 5;
            if (degree >= 120) dReverse = true;
          }
        } finally {
          unlockCanvasAndPost(canvas);
        }

        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
