package edu.cnm.deepdive.codebreaker.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ResultsView extends View {

  private static final float TEXT_SIZE_PERCENT = 0.4f;
  private static final int GREEN = Color.GREEN;
  private static final int ORANGE = Color.rgb(255, 165, 0);

  private final Paint paint;
  private final Paint textPaint;

  private int exactMatches;
  private int nearMatches;

  public ResultsView(Context context) {
    this(context, null);
  }

  public ResultsView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ResultsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.BLACK);
    paint.setStrokeWidth(2f);
    
    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setTextAlign(Paint.Align.LEFT);
  }

  public void setMatches(int exactMatches, int nearMatches) {
    this.exactMatches = exactMatches;
    this.nearMatches = nearMatches;
    invalidate();
  }

  @Override
  protected void onDraw(@NonNull Canvas canvas) {
    super.onDraw(canvas);
    int width = getWidth();
    int height = getHeight();

    // Draw diagonal line from bottom-left to top-right
    canvas.drawLine(0, height, width, 0, paint);

    textPaint.setTextSize(height * TEXT_SIZE_PERCENT);

    // Draw exact matches (top-left, green)
    textPaint.setColor(GREEN);
    String exactStr = String.valueOf(exactMatches);
    canvas.drawText(exactStr, 0, -textPaint.ascent(), textPaint);

    // Draw near matches (bottom-right, orange)
    textPaint.setColor(ORANGE);
    String nearStr = String.valueOf(nearMatches);
    float nearWidth = textPaint.measureText(nearStr);
    canvas.drawText(nearStr, width - nearWidth, height - textPaint.descent(), textPaint);
  }
}
