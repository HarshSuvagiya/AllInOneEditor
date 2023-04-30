
package com.netcompss.ffmpeg4android;

import android.content.Context;


public class LicenseCheckJNI {

    static {
        System.loadLibrary("license-jni");
    }

    public int licenseCheck(String path, Context ctx) {
        String rcStr = "-100";
        rcStr = licenseCheckComplexJNI(path);
        int rc = Integer.decode(rcStr);
        return rc;
    }

    public native String licenseCheckComplexJNI(String path);

    public native String licenseCheckSimpleJNI(String path);
}
