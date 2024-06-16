package com.parental.control.panjacreation.kiddo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import android.os.Build;


import android.os.IBinder;

import android.os.SystemClock;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.parental.control.panjacreation.kiddo.tflite.SimilarityClassifier;
import com.parental.control.panjacreation.kiddo.tflite.TFLiteObjectDetectionAPIModel;
import com.parental.control.panjacreation.kiddo.util.BitmapFormater;
import com.parental.control.panjacreation.kiddo.util.Constants;

import com.parental.control.panjacreation.kiddo.R;
import com.parental.control.panjacreation.kiddo.util.SharedPreferencesHelper;

import java.io.IOException;
import java.util.List;


public class DetectorBackgroungService extends Service {
    boolean isRunning = false;
    public static String currentUser = "";
    public static String previousUser = "";
    private FaceDetector faceDetector;
    private SimilarityClassifier detector;
    BitmapFormater bitmapFormater = new BitmapFormater();


    @Override
    public void onCreate() {
        super.onCreate();

        isRunning = true;

        // Real-time contour detection of multiple faces
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();

        faceDetector = FaceDetection.getClient(options);


        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getAssets(),
                            Constants.TF_OD_API_MODEL_FILE,
                            Constants.TF_OD_API_LABELS_FILE,
                            Constants.TF_OD_API_INPUT_SIZE,
                            Constants.TF_OD_API_IS_QUANTIZED);
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a notification for the foreground service
        Notification notification = createNotification();
        try {
            startForeground(Constants.NOTIFICATION_ID_BACKGROUND, notification);
        } catch (Exception e){
            e.printStackTrace();
        }

        takePhoto();

        return START_STICKY;
    }






    private void takePhoto() {

        Log.d("takePhoto: ","Preparing to take photo");
        Camera camera = null;

        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {

            SystemClock.sleep(500);
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                try {
                    camera = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.d( "takePhoto: ","Camera not available: " + camIdx);
                    camera = null;
                    //e.printStackTrace();
                }
            }

            try {
                if (camera == null) {
                    Log.d( "takePhoto: ","Could not get camera instance");
                } else {
                    Log.d( "takePhoto: ","Got the camera, creating the dummy surface texture");
                    startTakingPhoto(camera);
                }
            } catch (Exception e) {
                camera.release();
            }

        }
    }

    private void startTakingPhoto(Camera camera){
        try {
            camera.setPreviewTexture(new SurfaceTexture(0));
            camera.startPreview();
        } catch (Exception e) {
            Log.d( "takePhoto: ","Could not set the surface preview texture");
            e.printStackTrace();
        }
        try {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d("onPictureTaken: ","Picture taken successfully");
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                    image = bitmapFormater.rotateBitmapToPortrait(image);
                    image = bitmapFormater.resizeBitmap(image,240,320);
                    if (isRunning)
                        recogniseFace(image,camera);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            if (isRunning)
                takePhoto();
        }
    }

    private void recogniseFace(Bitmap image, Camera camera){
        Log.d("recogniseFace: ","Face recognition started");
        faceDetector.process(InputImage.fromBitmap(image, 0)).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                for (Face face : faces){
                    Log.d("FaceDetectedSuccess: ", "Face is detected");
                    Bitmap faceBmp = bitmapFormater.resizeBitmap(cropFaces(image, face),112,112);
                    try {
                        List<SimilarityClassifier.Recognition> resultsAux = detector.recognizeImage(faceBmp, false, getApplicationContext());
                        Log.d("RecognitionSuccess", "This is the result: "+resultsAux.get(0).getTitle());
                        Log.d("RecognitionSuccess", "isParent: "+resultsAux.get(0).isParent());
                        Constants.isParent = resultsAux.get(0).isParent();
                        currentUser = resultsAux.get(0).getTitle();
                        Constants.CURRENT_USER = currentUser;
                        if(!currentUser.equals(previousUser)){
                            previousUser = currentUser;
                            Toast.makeText(DetectorBackgroungService.this, "Current User: "+currentUser, Toast.LENGTH_SHORT).show();
                            //SharedPreferencesHelper.INSTANCE.saveString(getApplicationContext(), Constants.CURRENT_USER, currentUser);
                            //SharedPreferencesHelper.INSTANCE.saveBoolean(getApplicationContext(), Constants.IS_PARENT, resultsAux.get(0).isParent());
                        }
                        //Toast.makeText(getApplicationContext(), "Current user: "+resultsAux.get(0).getTitle(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("onRecognitionFailure: ","Failed to recognise\n"+e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<List<Face>>() {
            @Override
            public void onComplete(@NonNull Task<List<Face>> task) {
                if (isRunning)
                    startTakingPhoto(camera);
            }
        });

    }

    public Bitmap cropFaces(Bitmap originalBitmap, Face face) {
        try {
            Rect bounds = face.getBoundingBox();
            return Bitmap.createBitmap(originalBitmap, bounds.left, bounds.top, bounds.width(), bounds.height());
        } catch (Exception e){
            e.printStackTrace();
        }
        return originalBitmap;
    }

    private Notification createNotification() {
        // Create a notification for the foreground service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "ForegroundServiceChannel")
                .setContentTitle("Capturing Images")
                .setContentText("Capturing images from front camera in the background.")
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ForegroundServiceChannel",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}
