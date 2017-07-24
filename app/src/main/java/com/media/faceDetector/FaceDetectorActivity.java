package com.media.faceDetector;

/**
 * Created by fujunwei on 17-7-18.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.gmsvision.R;

public class FaceDetectorActivity extends Activity {
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new myView(this));
    }

    private class myView extends View {
        float myEyesDistance;
        int numberOfFaceDetected;
        Bitmap myBitmap;
        private int imageWidth, imageHeight;
        private int numberOfFace = 5;
        private FaceDetector myFaceDetect;
        private FaceDetector.Face[] myFace;

        public myView(Context context) {
            super(context);
            BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
            BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
            myBitmap = BitmapFactory.decodeResource(getResources(),
                    R.raw.pose3, BitmapFactoryOptionsbfo);
            imageWidth = myBitmap.getWidth();
            imageHeight = myBitmap.getHeight();
            myFace = new FaceDetector.Face[numberOfFace];
            myFaceDetect = new FaceDetector(imageWidth, imageHeight,
                    numberOfFace);
            numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);
            Log.d("fujunwei", "===========numberOfFaceDetected " + numberOfFaceDetected);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            double viewWidth = canvas.getWidth();
            double viewHeight = canvas.getHeight();
            double imageWidth = myBitmap.getWidth();
            double imageHeight = myBitmap.getHeight();
            double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);
            float d = (float) scale;

            Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
            canvas.drawBitmap(myBitmap, null, destBounds, null);

            Paint myPaint = new Paint();
            myPaint.setColor(Color.GREEN);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(3);
            for (int i = 0; i < numberOfFaceDetected; i++) {
                Face face = myFace[i];
                PointF myMidPoint = new PointF();
                face.getMidPoint(myMidPoint);
                myEyesDistance = face.eyesDistance();
                canvas.drawRect((int) (myMidPoint.x - myEyesDistance) * d,
                        (int) (myMidPoint.y - myEyesDistance) * d,
                        (int) (myMidPoint.x + myEyesDistance) * d,
                        (int) (myMidPoint.y + myEyesDistance) * d, myPaint);

                float rotationX = face.pose(FaceDetector.Face.EULER_X);

                float rotationY = face.pose(FaceDetector.Face.EULER_Y);

                float rotationZ = face.pose(FaceDetector.Face.EULER_Z);
                Log.d("fujunwei", "===========face pose  " + face.confidence() + " " + rotationX + " " + rotationY + " " + rotationZ
                        + " " + myEyesDistance + " " + myMidPoint.x + " " + myMidPoint.y);

                myPaint.setColor(Color.RED);
                PointF leftPosition = new PointF(myMidPoint.x - myEyesDistance / 2, myMidPoint.y);
                canvas.drawCircle(leftPosition.x * d, leftPosition.y * d, 10 * d, myPaint);

                PointF rightPosition = new PointF(myMidPoint.x + myEyesDistance / 2, myMidPoint.y);
                canvas.drawCircle(rightPosition.x * d, rightPosition.y * d, 10 * d, myPaint);

                canvas.drawCircle(myMidPoint.x * d, myMidPoint.y * d, 10 * d, myPaint);

                myPaint.setColor(Color.BLUE);
                canvas.drawCircle(myMidPoint.x * d, myMidPoint.y * d, myEyesDistance / 2 * d, myPaint);


            }
        }

    }
}