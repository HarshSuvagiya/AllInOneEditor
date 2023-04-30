package com.netcompss.ffmpeg4android;


public interface FfResponse {

    void onStart();

    void onSuccess();

    void onError(String errormsg);

    void onStop();

//        void onProgress(int prog);

}
