package com.parental.control.panjacreation.kiddo.util;

public class Constants {
    // MobileFaceNet
    public static final int TF_OD_API_INPUT_SIZE = 112;
    public static final boolean TF_OD_API_IS_QUANTIZED = false;
    public static final String TF_OD_API_MODEL_FILE = "mobile_face_net.tflite";
    public static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    public static final int NOTIFICATION_ID_BACKGROUND = 1;
    public static final String STORED_REC_KEY = "faces";
    public static final String PREFERENCE_FILE_KEY = "kiddo";

    public static String CURRENT_USER = "current_user";
    public static final String IS_PARENT = "is_parent";
    public static Boolean isParent = null;
}
