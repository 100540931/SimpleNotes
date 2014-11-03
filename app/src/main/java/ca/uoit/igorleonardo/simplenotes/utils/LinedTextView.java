package ca.uoit.igorleonardo.simplenotes.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import ca.uoit.igorleonardo.simplenotes.R;

public class LinedTextView extends TextView {
    private Paint mPaint = new Paint();
    private Rect mRect = new Rect();

    public LinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setColor(getResources().getColor(R.color.default_textlinecolor_addnote));
    }

    protected void onDraw(Canvas paramCanvas) {
        int i = getHeight() / getLineHeight();
        if (getLineCount() > i)
            i = getLineCount();
        Rect localRect = this.mRect;
        Paint localPaint = this.mPaint;
        int j = getLineBounds(0, localRect);
        for (int k = 0; ; k++){
            if (k >= i){
                super.onDraw(paramCanvas);
                return;
            }
            paramCanvas.drawLine(localRect.left, j + 1, localRect.right, j + 1, localPaint);
            j += getLineHeight();
        }
    }
}
