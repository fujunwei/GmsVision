/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;
import java.util.ArrayList;
import java.util.List;

/**
 * View which displays a bitmap containing a face along with overlay graphics that identify the
 * locations of detected facial landmarks.
 */
public class FaceView extends View {
    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    void setContent(Bitmap bitmap, SparseArray<Face> faces) {
        mBitmap = bitmap;
        mFaces = faces;
        invalidate();
    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceAnnotations(canvas, scale);
        }
    }

    /**
     * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
     * positioning the facial landmark graphics.
     */
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }


    private RectF getFaceBounds(boolean useGetWidth, boolean useBottomWidth, Canvas canvas, Paint paint, float scale) {
        RectF boundingBox = new RectF();
        for (int i = 0; i < mFaces.size(); i++) {
            final Face face = mFaces.valueAt(i);

            final List<Landmark> landmarks = face.getLandmarks();

            int leftEyeIndex = -1;
            int rightEyeIndex = -1;
            int bottomMouthIndex = -1;
            for (int j = 0; j < landmarks.size(); j++) {
                final Landmark landmark = landmarks.get(j);
                final int landmarkType = landmark.getType();
                if (landmarkType == Landmark.LEFT_EYE || landmarkType == Landmark.RIGHT_EYE
                        || landmarkType == Landmark.BOTTOM_MOUTH) {
                    Log.d("fujunwei", " ========= mark position " + landmark.getPosition().x + " " + landmark.getPosition().y + " " + Landmark.RIGHT_EYE + " " + landmarkType);

                    if (landmarkType == Landmark.LEFT_EYE) {
                        leftEyeIndex = j;
                    } else if (landmarkType == Landmark.RIGHT_EYE) {
                        rightEyeIndex = j;
                    } else {
                        assert landmarkType == Landmark.BOTTOM_MOUTH;
                        bottomMouthIndex = j;
                    }

                    canvas.drawCircle(landmark.getPosition().x * scale, landmark.getPosition().y * scale, 5 * scale, paint);

                    Log.d("fujunwei", " ========= found mark type " + Landmark.LEFT_EYE + " " + Landmark.RIGHT_EYE + " " + Landmark.BOTTOM_MOUTH + " " + j);
                }
            }

            final PointF corner = face.getPosition();
            if (leftEyeIndex != -1 && rightEyeIndex != -1 && !useGetWidth) {
                final PointF leftEyePoint = landmarks.get(leftEyeIndex).getPosition();
                final float eyesDistance = leftEyePoint.x - landmarks.get(rightEyeIndex).getPosition().x;
                final float eyeMouthDistance = bottomMouthIndex != -1 && useBottomWidth ?
                        landmarks.get(bottomMouthIndex).getPosition().y - leftEyePoint.y : -1;
                final PointF midPoint = new PointF(corner.x + face.getWidth() / 2, leftEyePoint.y);
                boundingBox.left = midPoint.x - eyesDistance;
                boundingBox.top = midPoint.y - eyesDistance;
                boundingBox.right = boundingBox.left + 2 * eyesDistance;
                float height = eyeMouthDistance > eyesDistance ? eyeMouthDistance + eyesDistance : 2 * eyesDistance;
                boundingBox.bottom = boundingBox.top + height;
            } else {
                boundingBox.left = corner.x;
                boundingBox.top = corner.y;
                boundingBox.right = boundingBox.left + face.getWidth();
                boundingBox.bottom = boundingBox.top + face.getHeight();
            }
        }
        return boundingBox;
    }

    /**
     * Draws a small circle for each detected landmark, centered at the detected landmark position.
     * <p>
     *
     * Note that eye landmarks are defined to be the midpoint between the detected eye corner
     * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
     * pupil position.
     */
    private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        float d = (float) scale;
        RectF boundingBox = getFaceBounds(true, false, canvas, paint, d);
        paint.setColor(Color.RED);
        canvas.drawRect(boundingBox.left * d , boundingBox.top * d, boundingBox.right * d  , boundingBox.bottom * d , paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(24 * d, 20 * d, 64 * d, 60 * d, paint);

        boundingBox = getFaceBounds(false, false, canvas, paint, d);
        d = (float) scale;
        paint.setColor(Color.GREEN);
        canvas.drawRect(boundingBox.left * d , boundingBox.top * d, boundingBox.right * d  , boundingBox.bottom * d , paint);

        boundingBox = getFaceBounds(false, true, canvas, paint, d);
        d = (float) scale;
        paint.setColor(Color.YELLOW);
        canvas.drawRect(boundingBox.left * d , boundingBox.top * d, boundingBox.right * d  , boundingBox.bottom * d , paint);
    }
}
