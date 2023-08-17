package com.parental.control.panjacreation.kiddo.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class BitmapFormater {
    public BitmapFormater() {
    }

    public Bitmap rotateBitmapToPortrait(Bitmap originalBitmap) {
        // Calculate the rotation angle needed to achieve portrait orientation
        int rotationAngle = 0; // Default angle

        // Check the current orientation of the original bitmap
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            rotationAngle = 270; // Rotate 90 degrees for landscape
        }

        // Create a rotation matrix
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);

        // Rotate the original bitmap using the matrix
        Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

        return rotatedBitmap;
    }

    public Bitmap resizeBitmap(Bitmap originalBitmap, int targetWidth, int targetHeight) {
        // Create a new bitmap with the desired resolution
        Bitmap resizedBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw the resized bitmap
        Canvas canvas = new Canvas(resizedBitmap);

        // Calculate the scaling factors for width and height
        float scaleX = (float) targetWidth / originalBitmap.getWidth();
        float scaleY = (float) targetHeight / originalBitmap.getHeight();

        // Set up a matrix to apply the scaling
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);

        // Draw the original bitmap onto the new bitmap with scaling
        canvas.drawBitmap(originalBitmap, matrix, new Paint());

        return resizedBitmap;
    }

}
