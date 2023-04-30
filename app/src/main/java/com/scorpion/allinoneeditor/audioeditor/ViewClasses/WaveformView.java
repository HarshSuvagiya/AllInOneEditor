package com.scorpion.allinoneeditor.audioeditor.ViewClasses;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.scorpion.allinoneeditor.R;
import com.scorpion.allinoneeditor.audioeditor.SoundFiles.CheapSoundFile;


public class WaveformView extends View {
    public interface WaveformListener {
        public void waveformTouchStart(float x);
        public void waveformTouchMove(float x);
        public void waveformTouchEnd();
        public void waveformFling(float x);
        public void waveformDraw();
//        public void waveformZoomIn();
//        public void waveformZoomOut();
    };

    // Colors
    private Paint mGridPaint;
    private Paint mSelectedLinePaint;
    private Paint mUnselectedLinePaint;
    private Paint mUnselectedBkgndLinePaint;
    private Paint mBorderLinePaint;
    private Paint mPlaybackLinePaint;
    private Paint mTimecodePaint;

    private CheapSoundFile mSoundFile;
    private int[] mLenByZoomLevel;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int[] mHeightsAtThisZoomLevel;
    private int mZoomLevel;
    private int mNumZoomLevels;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private int mOffset;
    private int mSelectionStart;
    private int mSelectionEnd;
    private int mPlaybackPos;
    private float mDensity;
    private float mInitialScaleSpan;
    private WaveformListener mListener;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mInitialized;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We don't want keys, the markers get these
        setFocusable(false);

        Resources res = getResources();
        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(false);
        mGridPaint.setColor(res.getColor(R.color.grid_line));
        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(res.getColor(R.color.waveform_selected));
        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(res.getColor(R.color.waveform_unselected));
        mUnselectedBkgndLinePaint = new Paint();
        mUnselectedBkgndLinePaint.setAntiAlias(false);
        mUnselectedBkgndLinePaint.setColor(res.getColor(R.color.waveform_unselected_bkgnd_overlay));
        mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(1.5f);
        mBorderLinePaint.setPathEffect(new DashPathEffect(new float[] { 3.0f, 2.0f }, 0.0f));
        mBorderLinePaint.setColor(res.getColor(R.color.selection_border));
        mPlaybackLinePaint = new Paint();
        mPlaybackLinePaint.setAntiAlias(false);
        mPlaybackLinePaint.setColor(res.getColor(R.color.playback_indicator));
        mTimecodePaint = new Paint();
        mTimecodePaint.setTextSize(12);
        mTimecodePaint.setAntiAlias(true);
        mTimecodePaint.setColor(res.getColor(R.color.timecode));
        mTimecodePaint.setShadowLayer(2, 1, 1, res.getColor(R.color.timecode_shadow));

        mGestureDetector = new GestureDetector(
                context,
                new SimpleOnGestureListener() {
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                        mListener.waveformFling(vx);
                        return true;
                    }
                }
        );

        mScaleGestureDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    public boolean onScaleBegin(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleBegin " + d.getCurrentSpanX());
                        mInitialScaleSpan = Math.abs(d.getCurrentSpanX());
                        return true;
                    }
                    public boolean onScale(ScaleGestureDetector d) {
                        float scale = Math.abs(d.getCurrentSpanX());
                        Log.v("Ringdroid", "Scale " + (scale - mInitialScaleSpan));
                        if (scale - mInitialScaleSpan > 40) {
//                            mListener.waveformZoomIn();
                            mInitialScaleSpan = scale;
                        }
                        if (scale - mInitialScaleSpan < -40) {
//                            mListener.waveformZoomOut();
                            mInitialScaleSpan = scale;
                        }
                        return true;
                    }
                    public void onScaleEnd(ScaleGestureDetector d) {
                        Log.v("Ringdroid", "ScaleEnd " + d.getCurrentSpanX());
                    }
                }
        );

        mSoundFile = null;
        mLenByZoomLevel = null;
        mValuesByZoomLevel = null;
        mHeightsAtThisZoomLevel = null;
        mOffset = 0;
        mPlaybackPos = -1;
        mSelectionStart = 0;
        mSelectionEnd = 0;
        mDensity = 1.0f;
        mInitialized = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mListener.waveformTouchStart(event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                mListener.waveformTouchMove(event.getX());
                break;
            case MotionEvent.ACTION_UP:
                mListener.waveformTouchEnd();
                break;
        }
        return true;
    }

    public boolean hasSoundFile() {
        return mSoundFile != null;
    }

    public void setSoundFile(CheapSoundFile soundFile) {
        mSoundFile = soundFile;
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
        mHeightsAtThisZoomLevel = null;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public int getZoomLevel() {
        return mZoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        while (mZoomLevel > zoomLevel) {
            zoomIn();
        }
        while (mZoomLevel < zoomLevel) {
            zoomOut();
        }
    }

    public boolean canZoomIn() {
        return (mZoomLevel > 0);
    }

    public void zoomIn() {
        if (canZoomIn()) {
            mZoomLevel--;
            mSelectionStart *= 2;
            mSelectionEnd *= 2;
            mHeightsAtThisZoomLevel = null;
            int offsetCenter = mOffset + getMeasuredWidth() / 2;
            offsetCenter *= 2;
            mOffset = offsetCenter - getMeasuredWidth() / 2;
            if (mOffset < 0)
                mOffset = 0;
            invalidate();
        }
    }

    public boolean canZoomOut() {
        return (mZoomLevel < mNumZoomLevels - 1);
    }

    public void zoomOut() {
        if (canZoomOut()) {
            mZoomLevel++;
            mSelectionStart /= 2;
            mSelectionEnd /= 2;
            int offsetCenter = mOffset + getMeasuredWidth() / 2;
            offsetCenter /= 2;
            mOffset = offsetCenter - getMeasuredWidth() / 2;
            if (mOffset < 0)
                mOffset = 0;
            mHeightsAtThisZoomLevel = null;
            invalidate();
        }
    }

    public int maxPos() {
        return mLenByZoomLevel[mZoomLevel];
    }

    public int secondsToFrames(double seconds) {
        return (int)(1.0 * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public int secondsToPixels(double seconds) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int)(z * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public double pixelsToSeconds(int pixels) {
        try {
            double z = mZoomFactorByZoomLevel[mZoomLevel];
            return (pixels * (double) mSamplesPerFrame / (mSampleRate * z));
        }
        catch (NullPointerException e)
        {
            return 0;
        }
    }

    public int millisecsToPixels(int msecs) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int)((msecs * 1.0 * mSampleRate * z) /
                (1000.0 * mSamplesPerFrame) + 0.5);
    }

    public int pixelsToMillisecs(int pixels) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int)(pixels * (1000.0 * mSamplesPerFrame) /
                (mSampleRate * z) + 0.5);
    }

    public void setParameters(int start, int end, int offset) {
        mSelectionStart = start;
        mSelectionEnd = end;
        mOffset = offset;
    }

    public int getStart() {
        return mSelectionStart;
    }

    public int getEnd() {
        return mSelectionEnd;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setPlayback(int pos) {
        mPlaybackPos = pos;
    }

    public void setListener(WaveformListener listener) {
        mListener = listener;
    }

    public void recomputeHeights(float density) {
        mHeightsAtThisZoomLevel = null;
        mDensity = density;
        mTimecodePaint.setTextSize((int)(12 * density));

        invalidate();
    }

    protected void drawWaveformLine(Canvas canvas,
                                    int x, int y0, int y1,
                                    Paint paint) {
        canvas.drawLine(x, y0, x, y1, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSoundFile == null)
            return;

        if (mHeightsAtThisZoomLevel == null)
            computeIntsForThisZoomLevel();

        // Draw waveform
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int start = mOffset;
        int width = mHeightsAtThisZoomLevel.length - start;
        int ctr = measuredHeight / 2;

        if (width > measuredWidth)
            width = measuredWidth;

        // Draw grid
        double onePixelInSecs = pixelsToSeconds(1);
        boolean onlyEveryFiveSecs = (onePixelInSecs > 1.0 / 50.0);
        double fractionalSecs = mOffset * onePixelInSecs;
        int integerSecs = (int) fractionalSecs;
        int i = 0;
        while (i < width) {
            i++;
            fractionalSecs += onePixelInSecs;
            int integerSecsNew = (int) fractionalSecs;
            if (integerSecsNew != integerSecs) {
                integerSecs = integerSecsNew;
                if (!onlyEveryFiveSecs || 0 == (integerSecs % 5)) {
                    canvas.drawLine(i, 0, i, measuredHeight, mGridPaint);
                }
            }
        }

        // Draw waveform
        for (i = 0; i < width; i++) {
            Paint paint;
            if (i + start >= mSelectionStart &&
                    i + start < mSelectionEnd) {
                paint = mSelectedLinePaint;
            } else {
                drawWaveformLine(canvas, i, 0, measuredHeight,
                        mUnselectedBkgndLinePaint);
                paint = mUnselectedLinePaint;
            }
            drawWaveformLine(
                    canvas, i,
                    ctr - mHeightsAtThisZoomLevel[start + i],
                    ctr + 1 + mHeightsAtThisZoomLevel[start + i],
                    paint);

            if (i + start == mPlaybackPos) {
                canvas.drawLine(i, 0, i, measuredHeight, mPlaybackLinePaint);
            }
        }

        // If we can see the right edge of the waveform, draw the
        // non-waveform area to the right as unselected
        for (i = width; i < measuredWidth; i++) {
            drawWaveformLine(canvas, i, 0, measuredHeight,
                    mUnselectedBkgndLinePaint);
        }

        // Draw borders
        canvas.drawLine(
                mSelectionStart - mOffset + 0.5f, 30,
                mSelectionStart - mOffset + 0.5f, measuredHeight,
                mBorderLinePaint);
        canvas.drawLine(
                mSelectionEnd - mOffset + 0.5f, 0,
                mSelectionEnd - mOffset + 0.5f, measuredHeight - 30,
                mBorderLinePaint);

        // Draw timecode
        double timecodeIntervalSecs = 1.0;
        if (timecodeIntervalSecs / onePixelInSecs < 50) {
            timecodeIntervalSecs = 5.0;
        }
        if (timecodeIntervalSecs / onePixelInSecs < 50) {
            timecodeIntervalSecs = 15.0;
        }

        // Draw grid
        fractionalSecs = mOffset * onePixelInSecs;
        int integerTimecode = (int) (fractionalSecs / timecodeIntervalSecs);
        i = 0;
        while (i < width) {
            i++;
            fractionalSecs += onePixelInSecs;
            integerSecs = (int) fractionalSecs;
            int integerTimecodeNew = (int) (fractionalSecs /
                    timecodeIntervalSecs);
            if (integerTimecodeNew != integerTimecode) {
                integerTimecode = integerTimecodeNew;

                // Turn, e.g. 67 seconds into "1:07"
                String timecodeMinutes = "" + (integerSecs / 60);
                String timecodeSeconds = "" + (integerSecs % 60);
                if ((integerSecs % 60) < 10) {
                    timecodeSeconds = "0" + timecodeSeconds;
                }
                String timecodeStr = timecodeMinutes + ":" + timecodeSeconds;
                float offset = (float) (
                        0.5 * mTimecodePaint.measureText(timecodeStr));
                canvas.drawText(timecodeStr,
                        i - offset,
                        (int)(12 * mDensity),
                        mTimecodePaint);
            }
        }

        if (mListener != null) {
            mListener.waveformDraw();
        }
    }

    /**
     * Called once when a new sound file is added
     */
    private void computeDoublesForAllZoomLevels() {
        int numFrames = mSoundFile.getNumFrames();
        int[] frameGains = mSoundFile.getFrameGains();
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (double)(
                    (frameGains[0] / 2.0) +
                            (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++) {
                smoothedGains[i] = (double)(
                        (frameGains[i - 1] / 3.0) +
                                (frameGains[i    ] / 3.0) +
                                (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double)(
                    (frameGains[numFrames - 2] / 2.0) +
                            (frameGains[numFrames - 1] / 2.0));
        }

        // Make sure the range is no more than 0 - 255
        double maxGain = 1.0;
        for (int i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int)(smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;

            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

        // Re-calibrate the min to be 5%
        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int)minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int)maxGain];
            maxGain--;
        }

        // Compute the heights
        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (int i = 0; i < numFrames; i++) {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0)
                value = 0.0;
            if (value > 1.0)
                value = 1.0;
            heights[i] = value * value;
        }

        mNumZoomLevels = 5;
        mLenByZoomLevel = new int[5];
        mZoomFactorByZoomLevel = new double[5];
        mValuesByZoomLevel = new double[5][];

        // Level 0 is doubled, with interpolated values
        mLenByZoomLevel[0] = numFrames * 2;
        mZoomFactorByZoomLevel[0] = 2.0;
        mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
        if (numFrames > 0) {
            mValuesByZoomLevel[0][0] = 0.5 * heights[0];
            mValuesByZoomLevel[0][1] = heights[0];
        }
        for (int i = 1; i < numFrames; i++) {
            mValuesByZoomLevel[0][2 * i] = 0.5 * (heights[i - 1] + heights[i]);
            mValuesByZoomLevel[0][2 * i + 1] = heights[i];
        }

        // Level 1 is normal
        mLenByZoomLevel[1] = numFrames;
        mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
        mZoomFactorByZoomLevel[1] = 1.0;
        for (int i = 0; i < mLenByZoomLevel[1]; i++) {
            mValuesByZoomLevel[1][i] = heights[i];
        }

        // 3 more levels are each halved
        for (int j = 2; j < 5; j++) {
            mLenByZoomLevel[j] = mLenByZoomLevel[j - 1] / 2;
            mValuesByZoomLevel[j] = new double[mLenByZoomLevel[j]];
            mZoomFactorByZoomLevel[j] = mZoomFactorByZoomLevel[j - 1] / 2.0;
            for (int i = 0; i < mLenByZoomLevel[j]; i++) {
                mValuesByZoomLevel[j][i] =
                        0.5 * (mValuesByZoomLevel[j - 1][2 * i] +
                                mValuesByZoomLevel[j - 1][2 * i + 1]);
            }
        }

        if (numFrames > 5000) {
            mZoomLevel = 3;
        } else if (numFrames > 1000) {
            mZoomLevel = 2;
        } else if (numFrames > 300) {
            mZoomLevel = 1;
        } else {
            mZoomLevel = 0;
        }

        mInitialized = true;
    }

    /**
     * Called the first time we need to draw when the zoom level has changed
     * or the screen is resized
     */
    private void computeIntsForThisZoomLevel() {
        int halfHeight = (getMeasuredHeight() / 2) - 1;
        mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]];
        for (int i = 0; i < mLenByZoomLevel[mZoomLevel]; i++) {
            mHeightsAtThisZoomLevel[i] =
                    (int)(mValuesByZoomLevel[mZoomLevel][i] * halfHeight);
        }
    }
}


//@SuppressLint({"ClickableViewAccessibility"})
//public class WaveformView extends View {
//    private Paint mBorderLinePaint;
//    private float mDensity;
//    private GestureDetector mGestureDetector;
//    private Paint mGridPaint = new Paint();
//    private int[] mHeightsAtThisZoomLevel;
//    private boolean mInitialized;
//    private int[] mLenByZoomLevel;
//    private WaveformListener mListener;
//    private int mNumZoomLevels;
//    private int mOffset;
//    private Paint mPlaybackLinePaint;
//    private int mPlaybackPos;
//    private int mSampleRate;
//    private int mSamplesPerFrame;
//    private Paint mSelectedLinePaint;
//    private int mSelectionEnd;
//    private int mSelectionStart;
//    private CheapSoundFile mSoundFile;
//    private Paint mTimecodePaint;
//    private Paint mUnselectedBkgndLinePaint;
//    private Paint mUnselectedLinePaint;
//    private double[][] mValuesByZoomLevel;
//    private double[] mZoomFactorByZoomLevel;
//    private int mZoomLevel;
//
//    class C06651 extends SimpleOnGestureListener {
//        C06651() {
//        }
//
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
//            WaveformView.this.mListener.waveformFling(vx);
//            return true;
//        }
//    }
//
//    public interface WaveformListener {
//        void waveformDraw();
//
//        void waveformFling(float f);
//
//        void waveformTouchEnd();
//
//        void waveformTouchMove(float f);
//
//        void waveformTouchStart(float f);
//    }
//
//    public WaveformView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setFocusable(false);
//        this.mGridPaint.setAntiAlias(false);
//        this.mGridPaint.setColor(getResources().getColor(R.drawable.grid_line));
//        this.mSelectedLinePaint = new Paint();
//        this.mSelectedLinePaint.setAntiAlias(false);
//        this.mSelectedLinePaint.setColor(getResources().getColor(R.drawable.waveform_selected));
//        this.mUnselectedLinePaint = new Paint();
//        this.mUnselectedLinePaint.setAntiAlias(false);
//        this.mUnselectedLinePaint.setColor(getResources().getColor(R.drawable.waveform_unselected));
//        this.mUnselectedBkgndLinePaint = new Paint();
//        this.mUnselectedBkgndLinePaint.setAntiAlias(false);
//        this.mUnselectedBkgndLinePaint.setColor(getResources().getColor(R.drawable.waveform_unselected_bkgnd_overlay));
//        this.mBorderLinePaint = new Paint();
//        this.mBorderLinePaint.setAntiAlias(true);
//        this.mBorderLinePaint.setStrokeWidth(1.5f);
//        this.mBorderLinePaint.setPathEffect(new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
//        this.mBorderLinePaint.setColor(getResources().getColor(R.drawable.selection_border));
//        this.mPlaybackLinePaint = new Paint();
//        this.mPlaybackLinePaint.setAntiAlias(false);
//        this.mPlaybackLinePaint.setColor(getResources().getColor(R.drawable.playback_indicator));
//        this.mTimecodePaint = new Paint();
//        this.mTimecodePaint.setTextSize(12.0f);
//        this.mTimecodePaint.setAntiAlias(true);
//        this.mTimecodePaint.setColor(getResources().getColor(R.drawable.timecode));
//        this.mTimecodePaint.setShadowLayer(2.0f, 1.0f, 1.0f, getResources().getColor(R.drawable.timecode_shadow));
//        this.mGestureDetector = new GestureDetector(context, new C06651());
//        this.mSoundFile = null;
//        this.mLenByZoomLevel = null;
//        this.mValuesByZoomLevel = null;
//        this.mHeightsAtThisZoomLevel = null;
//        this.mOffset = 0;
//        this.mPlaybackPos = -1;
//        this.mSelectionStart = 0;
//        this.mSelectionEnd = 0;
//        this.mDensity = 1.0f;
//        this.mInitialized = false;
//    }
//
//    public boolean onTouchEvent(MotionEvent event) {
//        if (!this.mGestureDetector.onTouchEvent(event)) {
//            switch (event.getAction()) {
//                case 0:
//                    this.mListener.waveformTouchStart(event.getX());
//                    break;
//                case 1:
//                    this.mListener.waveformTouchEnd();
//                    break;
//                case 2:
//                    this.mListener.waveformTouchMove(event.getX());
//                    break;
//                default:
//                    break;
//            }
//        }
//        return true;
//    }
//
//    public void setSoundFile(CheapSoundFile soundFile) {
//        this.mSoundFile = soundFile;
//        this.mSampleRate = this.mSoundFile.getSampleRate();
//        this.mSamplesPerFrame = this.mSoundFile.getSamplesPerFrame();
//        computeDoublesForAllZoomLevels();
//        this.mHeightsAtThisZoomLevel = null;
//    }
//
//    public boolean isInitialized() {
//        return this.mInitialized;
//    }
//
//    public int getZoomLevel() {
//        return this.mZoomLevel;
//    }
//
//    public void setZoomLevel(int zoomLevel) {
//        while (this.mZoomLevel > zoomLevel) {
//            zoomIn();
//        }
//        while (this.mZoomLevel < zoomLevel) {
//            zoomOut();
//        }
//    }
//
//    public boolean canZoomIn() {
//        return this.mZoomLevel > 0;
//    }
//
//    public void zoomIn() {
//        if (canZoomIn()) {
//            this.mZoomLevel--;
//            this.mSelectionStart *= 2;
//            this.mSelectionEnd *= 2;
//            this.mHeightsAtThisZoomLevel = null;
//            this.mOffset = ((this.mOffset + (getMeasuredWidth() / 2)) * 2) - (getMeasuredWidth() / 2);
//            if (this.mOffset < 0) {
//                this.mOffset = 0;
//            }
//            invalidate();
//        }
//    }
//
//    public boolean canZoomOut() {
//        return this.mZoomLevel < this.mNumZoomLevels + -1;
//    }
//
//    public void zoomOut() {
//        if (canZoomOut()) {
//            this.mZoomLevel++;
//            this.mSelectionStart /= 2;
//            this.mSelectionEnd /= 2;
//            this.mOffset = ((this.mOffset + (getMeasuredWidth() / 2)) / 2) - (getMeasuredWidth() / 2);
//            if (this.mOffset < 0) {
//                this.mOffset = 0;
//            }
//            this.mHeightsAtThisZoomLevel = null;
//            invalidate();
//        }
//    }
//
//    public int maxPos() {
//        return this.mLenByZoomLevel[this.mZoomLevel];
//    }
//
//    public int secondsToFrames(double seconds) {
//        return (int) ((((1.0d * seconds) * ((double) this.mSampleRate)) / ((double) this.mSamplesPerFrame)) + 0.5d);
//    }
//
//    public int secondsToPixels(double seconds) {
//        return (int) ((((this.mZoomFactorByZoomLevel[this.mZoomLevel] * seconds) * ((double) this.mSampleRate)) / ((double) this.mSamplesPerFrame)) + 0.5d);
//    }
//
//    public double pixelsToSeconds(int pixels) {
//        return (((double) pixels) * ((double) this.mSamplesPerFrame)) / (((double) this.mSampleRate) * this.mZoomFactorByZoomLevel[this.mZoomLevel]);
//    }
//
//    public int millisecsToPixels(int msecs) {
//        return (int) (((((((double) msecs) * 1.0d) * ((double) this.mSampleRate)) * this.mZoomFactorByZoomLevel[this.mZoomLevel]) / (1000.0d * ((double) this.mSamplesPerFrame))) + 0.5d);
//    }
//
//    public int pixelsToMillisecs(int pixels) {
//        return (int) (((((double) pixels) * (1000.0d * ((double) this.mSamplesPerFrame))) / (((double) this.mSampleRate) * this.mZoomFactorByZoomLevel[this.mZoomLevel])) + 0.5d);
//    }
//
//    public void setParameters(int start, int end, int offset) {
//        this.mSelectionStart = start;
//        this.mSelectionEnd = end;
//        this.mOffset = offset;
//    }
//
//    public int getStart() {
//        return this.mSelectionStart;
//    }
//
//    public int getEnd() {
//        return this.mSelectionEnd;
//    }
//
//    public int getOffset() {
//        return this.mOffset;
//    }
//
//    public void setPlayback(int pos) {
//        this.mPlaybackPos = pos;
//    }
//
//    public void setListener(WaveformListener listener) {
//        this.mListener = listener;
//    }
//
//    public void recomputeHeights(float density) {
//        this.mHeightsAtThisZoomLevel = null;
//        this.mDensity = density;
//        this.mTimecodePaint.setTextSize((float) ((int) (12.0f * density)));
//        invalidate();
//    }
//
//    protected void drawWaveformLine(Canvas canvas, int x, int y0, int y1, Paint paint) {
//        canvas.drawLine((float) x, (float) y0, (float) x, (float) y1, paint);
//    }
//
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (this.mSoundFile != null) {
//            if (this.mHeightsAtThisZoomLevel == null) {
//                computeIntsForThisZoomLevel();
//            }
//            int measuredWidth = getMeasuredWidth();
//            int measuredHeight = getMeasuredHeight();
//            int start = this.mOffset;
//            int width = this.mHeightsAtThisZoomLevel.length - start;
//            int ctr = measuredHeight / 2;
//            if (width > measuredWidth) {
//                width = measuredWidth;
//            }
//            double onePixelInSecs = pixelsToSeconds(1);
//            boolean onlyEveryFiveSecs = onePixelInSecs > 0.02d;
//            double fractionalSecs = ((double) this.mOffset) * onePixelInSecs;
//            int integerSecs = (int) fractionalSecs;
//            int i = 0;
//            while (i < width) {
//                int i2 = i + 1;
//                fractionalSecs += onePixelInSecs;
//                int integerSecsNew = (int) fractionalSecs;
//                if (integerSecsNew != integerSecs) {
//                    integerSecs = integerSecsNew;
//                    if (!onlyEveryFiveSecs || integerSecs % 5 == 0) {
//                        canvas.drawLine((float) i2, 0.0f, (float) i2, (float) measuredHeight, this.mGridPaint);
//                    }
//                }
//                i = i2;
//            }
//            i = 0;
//            while (i < width) {
//                Paint paint;
//                if (i + start < this.mSelectionStart || i + start >= this.mSelectionEnd) {
//                    drawWaveformLine(canvas, i, 0, measuredHeight, this.mUnselectedBkgndLinePaint);
//                    paint = this.mUnselectedLinePaint;
//                } else {
//                    paint = this.mSelectedLinePaint;
//                }
//                drawWaveformLine(canvas, i, ctr - this.mHeightsAtThisZoomLevel[start + i], (ctr + 1) + this.mHeightsAtThisZoomLevel[start + i], paint);
//                if (i + start == this.mPlaybackPos) {
//                    canvas.drawLine((float) i, 0.0f, (float) i, (float) measuredHeight, this.mPlaybackLinePaint);
//                }
//                i++;
//            }
//            for (i = width; i < measuredWidth; i++) {
//                drawWaveformLine(canvas, i, 0, measuredHeight, this.mUnselectedBkgndLinePaint);
//            }
//            canvas.drawLine(((float) (this.mSelectionStart - this.mOffset)) + 0.5f, 30.0f, ((float) (this.mSelectionStart - this.mOffset)) + 0.5f, (float) measuredHeight, this.mBorderLinePaint);
//            canvas.drawLine(((float) (this.mSelectionEnd - this.mOffset)) + 0.5f, 0.0f, ((float) (this.mSelectionEnd - this.mOffset)) + 0.5f, (float) (measuredHeight - 30), this.mBorderLinePaint);
//            double timecodeIntervalSecs = 1.0d;
//            if (1.0d / onePixelInSecs < 50.0d) {
//                timecodeIntervalSecs = 5.0d;
//            }
//            if (timecodeIntervalSecs / onePixelInSecs < 50.0d) {
//                timecodeIntervalSecs = 15.0d;
//            }
//            fractionalSecs = ((double) this.mOffset) * onePixelInSecs;
//            int integerTimecode = (int) (fractionalSecs / timecodeIntervalSecs);
//            i = 0;
//            while (i < width) {
//                i++;
//                fractionalSecs += onePixelInSecs;
//                integerSecs = (int) fractionalSecs;
//                int integerTimecodeNew = (int) (fractionalSecs / timecodeIntervalSecs);
//                if (integerTimecodeNew != integerTimecode) {
//                    integerTimecode = integerTimecodeNew;
//                    String timecodeMinutes = ""+(integerSecs / 60);
//                    String timecodeSeconds = ""+(integerSecs % 60);
//                    if (integerSecs % 60 < 10) {
//                        timecodeSeconds = "0" + timecodeSeconds;
//                    }
//                    String timecodeStr = new StringBuilder(String.valueOf(timecodeMinutes)).append(":").append(timecodeSeconds).toString();
//                    canvas.drawText(timecodeStr, ((float) i) - ((float) (0.5d * ((double) this.mTimecodePaint.measureText(timecodeStr)))), (float) ((int) (12.0f * this.mDensity)), this.mTimecodePaint);
//                }
//            }
//            if (this.mListener != null) {
//                this.mListener.waveformDraw();
//            }
//        }
//    }
//
//    private void computeDoublesForAllZoomLevels() {
//        int i;
//        int numFrames = this.mSoundFile.getNumFrames();
//        int[] frameGains = this.mSoundFile.getFrameGains();
//        double[] smoothedGains = new double[numFrames];
//        if (numFrames == 1) {
//            smoothedGains[0] = (double) frameGains[0];
//        } else if (numFrames == 2) {
//            smoothedGains[0] = (double) frameGains[0];
//            smoothedGains[1] = (double) frameGains[1];
//        } else if (numFrames > 2) {
//            smoothedGains[0] = (((double) frameGains[0]) / 2.0d) + (((double) frameGains[1]) / 2.0d);
//            for (i = 1; i < numFrames - 1; i++) {
//                smoothedGains[i] = ((((double) frameGains[i - 1]) / 3.0d) + (((double) frameGains[i]) / 3.0d)) + (((double) frameGains[i + 1]) / 3.0d);
//            }
//            smoothedGains[numFrames - 1] = (((double) frameGains[numFrames - 2]) / 2.0d) + (((double) frameGains[numFrames - 1]) / 2.0d);
//        }
//        double maxGain = 1.0d;
//        for (i = 0; i < numFrames; i++) {
//            if (smoothedGains[i] > maxGain) {
//                maxGain = smoothedGains[i];
//            }
//        }
//        double scaleFactor = 1.0d;
//        if (maxGain > 255.0d) {
//            scaleFactor = 255.0d / maxGain;
//        }
//        maxGain = 0.0d;
//        int[] gainHist = new int[256];
//        for (i = 0; i < numFrames; i++) {
//            int smoothedGain = (int) (smoothedGains[i] * scaleFactor);
//            if (smoothedGain < 0) {
//                smoothedGain = 0;
//            }
//            if (smoothedGain > 255) {
//                smoothedGain = 255;
//            }
//            if (((double) smoothedGain) > maxGain) {
//                maxGain = (double) smoothedGain;
//            }
//            gainHist[smoothedGain] = gainHist[smoothedGain] + 1;
//        }
//        double minGain = 0.0d;
//        int sum = 0;
//        while (minGain < 255.0d && sum < numFrames / 20) {
//            sum += gainHist[(int) minGain];
//            minGain += 1.0d;
//        }
//        sum = 0;
//        while (maxGain > 2.0d && sum < numFrames / 100) {
//            sum += gainHist[(int) maxGain];
//            maxGain -= 1.0d;
//        }
//        double[] heights = new double[numFrames];
//        double range = maxGain - minGain;
//        for (i = 0; i < numFrames; i++) {
//            double value = ((smoothedGains[i] * scaleFactor) - minGain) / range;
//            if (value < 0.0d) {
//                value = 0.0d;
//            }
//            if (value > 1.0d) {
//                value = 1.0d;
//            }
//            heights[i] = value * value;
//        }
//        this.mNumZoomLevels = 5;
//        this.mLenByZoomLevel = new int[5];
//        this.mZoomFactorByZoomLevel = new double[5];
//        this.mValuesByZoomLevel = new double[5][];
//        this.mLenByZoomLevel[0] = numFrames * 2;
//        this.mZoomFactorByZoomLevel[0] = 2.0d;
//        this.mValuesByZoomLevel[0] = new double[this.mLenByZoomLevel[0]];
//        if (numFrames > 0) {
//            this.mValuesByZoomLevel[0][0] = 0.5d * heights[0];
//            this.mValuesByZoomLevel[0][1] = heights[0];
//        }
//        for (i = 1; i < numFrames; i++) {
//            this.mValuesByZoomLevel[0][i * 2] = 0.5d * (heights[i - 1] + heights[i]);
//            this.mValuesByZoomLevel[0][(i * 2) + 1] = heights[i];
//        }
//        this.mLenByZoomLevel[1] = numFrames;
//        this.mValuesByZoomLevel[1] = new double[this.mLenByZoomLevel[1]];
//        this.mZoomFactorByZoomLevel[1] = 1.0d;
//        for (i = 0; i < this.mLenByZoomLevel[1]; i++) {
//            this.mValuesByZoomLevel[1][i] = heights[i];
//        }
//        for (int j = 2; j < 5; j++) {
//            this.mLenByZoomLevel[j] = this.mLenByZoomLevel[j - 1] / 2;
//            this.mValuesByZoomLevel[j] = new double[this.mLenByZoomLevel[j]];
//            this.mZoomFactorByZoomLevel[j] = this.mZoomFactorByZoomLevel[j - 1] / 2.0d;
//            for (i = 0; i < this.mLenByZoomLevel[j]; i++) {
//                this.mValuesByZoomLevel[j][i] = 0.5d * (this.mValuesByZoomLevel[j - 1][i * 2] + this.mValuesByZoomLevel[j - 1][(i * 2) + 1]);
//            }
//        }
//        if (numFrames > 5000) {
//            this.mZoomLevel = 3;
//        } else if (numFrames > 1000) {
//            this.mZoomLevel = 2;
//        } else if (numFrames > 300) {
//            this.mZoomLevel = 1;
//        } else {
//            this.mZoomLevel = 0;
//        }
//        this.mInitialized = true;
//    }
//
//    private void computeIntsForThisZoomLevel() {
//        int halfHeight = (getMeasuredHeight() / 2) - 1;
//        this.mHeightsAtThisZoomLevel = new int[this.mLenByZoomLevel[this.mZoomLevel]];
//        for (int i = 0; i < this.mLenByZoomLevel[this.mZoomLevel]; i++) {
//            this.mHeightsAtThisZoomLevel[i] = (int) (this.mValuesByZoomLevel[this.mZoomLevel][i] * ((double) halfHeight));
//        }
//    }
//}
