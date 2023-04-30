package com.netcompss.ffmpeg4android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.netcompss.loader.LoadJNI;

import java.io.File;

public class CommandExecutor extends AsyncTask<Void, Void, Void> {

    private int status;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String[] cmd;
    private String logPath;
    private FfResponse ffResponse;
    private LoadJNI vk;

    public CommandExecutor(Context context, String[] cmd, FfResponse ffResponse) {

        this.context = context;
        this.cmd = cmd;
        this.ffResponse = ffResponse;

        vk = new LoadJNI();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        status = 1;
        logPath = GeneralUtils.getLogFile(context);
        GeneralUtils.deleteLicFile(context);

        if (ffResponse != null) {
            ffResponse.onStart();
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {

            vk.run(context, cmd);

            status = GeneralUtils.isOperationSuccessful(logPath);

        } catch (Exception e) {
            e.printStackTrace();
            status = 1;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        switch (status) {

            case 0:

                if (ffResponse != null) {
                    ffResponse.onSuccess();
                }

                break;

            case 1:

                String msg = GeneralUtils.getLastLinesFromLog(new File(logPath), 20);

                if (ffResponse != null) {
                    ffResponse.onError(msg);
                }

                break;

            case 2:

                if (ffResponse != null) {
                    ffResponse.onStop();
                }

                break;

        }

    }

}
