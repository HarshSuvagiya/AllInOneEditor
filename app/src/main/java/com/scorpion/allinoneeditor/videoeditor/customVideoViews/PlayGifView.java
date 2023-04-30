package com.scorpion.allinoneeditor.videoeditor.customVideoViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class PlayGifView extends View {
    private static final int DEFAULT_MOVIEW_DURATION = 1000;
    private int mCurrentAnimationTime = 0;
    private Movie mMovie;
    private int mMovieResourceId;
    private long mMovieStart = 0;

    @SuppressLint({"NewApi"})
    public PlayGifView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(1, (Paint) null);
        }
    }

    public void setImageResource(String str) {
        this.mMovie = Movie.decodeFile(str);
        requestLayout();
    }

    public void onMeasure(int i, int i2) {
        Movie movie = this.mMovie;
        if (movie != null) {
            setMeasuredDimension(movie.width(), this.mMovie.height());
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    public void onDraw(Canvas canvas) {
        if (this.mMovie != null) {
            updateAnimtionTime();
            drawGif(canvas);
            invalidate();
            return;
        }
        drawGif(canvas);
    }

    private void updateAnimtionTime() {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (this.mMovieStart == 0) {
            this.mMovieStart = uptimeMillis;
        }
        int duration = this.mMovie.duration();
        if (duration == 0) {
            duration = 1000;
        }
        this.mCurrentAnimationTime = (int) ((uptimeMillis - this.mMovieStart) % ((long) duration));
    }

    private void drawGif(Canvas canvas) {
        this.mMovie.setTime(this.mCurrentAnimationTime);
        this.mMovie.draw(canvas, 0.0f, 0.0f);
        canvas.restore();
    }
}
