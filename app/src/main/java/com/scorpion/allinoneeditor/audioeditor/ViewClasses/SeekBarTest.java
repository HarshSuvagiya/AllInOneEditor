package com.scorpion.allinoneeditor.audioeditor.ViewClasses;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import androidx.recyclerview.widget.ItemTouchHelper.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Random;

public class SeekBarTest {
    public static final String PREF_SEEK_TEST_DATE = "seek_test_date";
    public static final String PREF_SEEK_TEST_RESULT = "seek_test_result";
    private static byte[] SILENCE_MP3_FRAME;
    public static long after;
    public static long before;

    static class C05961 implements OnCompletionListener {
        C05961() {
        }

        public synchronized void onCompletion(MediaPlayer arg0) {
            Log.i("Ringdroid", "Got callback");
            SeekBarTest.after = System.currentTimeMillis();
        }
    }

    public static boolean CanSeekAccurately(SharedPreferences prefs) {
        Log.i("Ringdroid", "Running CanSeekAccurately");
        boolean result = prefs.getBoolean(PREF_SEEK_TEST_RESULT, false);
        long testDate = prefs.getLong(PREF_SEEK_TEST_DATE, 0);
        long now = new Date().getTime();
        if (now - testDate < 604800000) {
            Log.i("Ringdroid", "Fast MP3 seek result cached: " + result);
            return result;
        }
        String filename = "/sdcard/silence" + new Random().nextLong() + ".mp3";
        File file = new File(filename);
        boolean ok = false;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            ok = true;
        }
        if (ok) {
            Log.i("Ringdroid", "Writing " + filename);
            try {
                file.createNewFile();
                try {
                    int i;
                    Editor prefsEditor;
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    for (i = 0; i < 80; i++) {
                        fileOutputStream.write(SILENCE_MP3_FRAME, 0, SILENCE_MP3_FRAME.length);
                    }
                    Log.i("Ringdroid", "File written, starting to play");
                    MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(3);
                    player.setDataSource(new FileInputStream(filename).getFD(), (long) (SILENCE_MP3_FRAME.length * 70), (long) (SILENCE_MP3_FRAME.length * 10));
                    Log.i("Ringdroid", "Preparing");
                    player.prepare();
                    before = 0;
                    after = 0;
                    player.setOnCompletionListener(new C05961());
                    Log.i("Ringdroid", "Starting");
                    player.start();
                    i = 0;
                    while (i < Callback.DEFAULT_DRAG_ANIMATION_DURATION && before == 0) {
                        try {
                            if (player.getCurrentPosition() > 0) {
                                Log.i("Ringdroid", "Started playing after " + (i * 10) + " ms");
                                before = System.currentTimeMillis();
                            }
                            Thread.sleep(10);
                            i++;
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            Log.i("Ringdroid", "Couldn't play: " + e2.toString());
                            Log.i("Ringdroid", "Fast MP3 seek disabled by default");
                            try {
                                file.delete();
                            } catch (Exception e3) {
                            }
                            prefsEditor = prefs.edit();
                            prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
                            prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
                            prefsEditor.commit();
                            return false;
                        }
                    }
                    if (before == 0) {
                        Log.i("Ringdroid", "Never started playing.");
                        Log.i("Ringdroid", "Fast MP3 seek disabled by default");
                        try {
                            file.delete();
                        } catch (Exception e4) {
                        }
                        prefsEditor = prefs.edit();
                        prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
                        prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
                        prefsEditor.commit();
                        return false;
                    }
                    Log.i("Ringdroid", "Sleeping");
                    for (i = 0; i < 300 && after == 0; i++) {
                        Log.i("Ringdroid", "Pos: " + player.getCurrentPosition());
                        Thread.sleep(10);
                    }
                    Log.i("Ringdroid", "Result: " + before + ", " + after);
                    if (after <= before || after >= before + 2000) {
                        Log.i("Ringdroid", "Fast MP3 seek disabled");
                    } else {
                        Log.i("Ringdroid", "Fast MP3 seek enabled: " + (after > before ? after - before : -1));
                        result = true;
                    }
                    prefsEditor = prefs.edit();
                    prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
                    prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
                    prefsEditor.commit();
                    try {
                        file.delete();
                    } catch (Exception e5) {
                    }
                    return result;
                } catch (Exception e6) {
                    Log.i("Ringdroid", "Couldn't write temp silence file");
                    try {
                        file.delete();
                    } catch (Exception e7) {
                    }
                    return false;
                }
            } catch (Exception e8) {
                Log.i("Ringdroid", "Couldn't output for writing");
                return false;
            }
        }
        Log.i("Ringdroid", "Couldn't find temporary filename");
        return false;
    }

    static {
        byte[] bArr = new byte[104];
        bArr[0] = (byte) -1;
        bArr[1] = (byte) -5;
        bArr[2] = (byte) 16;
        bArr[3] = (byte) -60;
        bArr[5] = (byte) 3;
        bArr[6] = (byte) -127;
        bArr[7] = (byte) -12;
        bArr[8] = (byte) 1;
        bArr[9] = (byte) 38;
        bArr[10] = (byte) 96;
        bArr[12] = (byte) 64;
        bArr[13] = (byte) 32;
        bArr[14] = (byte) 89;
        bArr[15] = Byte.MIN_VALUE;
        bArr[16] = (byte) 35;
        bArr[17] = (byte) 72;
        bArr[19] = (byte) 9;
        bArr[20] = (byte) 116;
        bArr[22] = (byte) 1;
        bArr[23] = (byte) 18;
        bArr[24] = (byte) 3;
        bArr[25] = (byte) -1;
        bArr[26] = (byte) -1;
        bArr[27] = (byte) -1;
        bArr[28] = (byte) -1;
        bArr[29] = (byte) -2;
        bArr[30] = (byte) -97;
        bArr[31] = (byte) 99;
        bArr[32] = (byte) -65;
        bArr[33] = (byte) -47;
        bArr[34] = (byte) 122;
        bArr[35] = (byte) 63;
        bArr[36] = (byte) 93;
        bArr[37] = (byte) 1;
        bArr[38] = (byte) -1;
        bArr[39] = (byte) -1;
        bArr[40] = (byte) -1;
        bArr[41] = (byte) -1;
        bArr[42] = (byte) -2;
        bArr[43] = (byte) -115;
        bArr[44] = (byte) -83;
        bArr[45] = (byte) 108;
        bArr[46] = (byte) 49;
        bArr[47] = (byte) 66;
        bArr[48] = (byte) -61;
        bArr[49] = (byte) 2;
        bArr[50] = (byte) -57;
        bArr[51] = (byte) 12;
        bArr[52] = (byte) 9;
        bArr[53] = (byte) -122;
        bArr[54] = (byte) -125;
        bArr[55] = (byte) -88;
        bArr[56] = (byte) 122;
        bArr[57] = (byte) 58;
        bArr[58] = (byte) 104;
        bArr[59] = (byte) 76;
        bArr[60] = (byte) 65;
        bArr[61] = (byte) 77;
        bArr[62] = (byte) 69;
        bArr[63] = (byte) 51;
        bArr[64] = (byte) 46;
        bArr[65] = (byte) 57;
        bArr[66] = (byte) 56;
        bArr[67] = (byte) 46;
        bArr[68] = (byte) 50;
        SILENCE_MP3_FRAME = bArr;
    }
}
