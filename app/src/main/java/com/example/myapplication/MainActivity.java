package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.ResultListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Long time = System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frameLayout = findViewById(R.id.layout);
        DetectionResultImageView imageView = new DetectionResultImageView(this);
        frameLayout.removeAllViewsInLayout();
        imageView.setImageDrawable(null);
        frameLayout.addView(imageView);
        imageView.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pose pose = new Pose(MainActivity.this, new PoseOptions(true));
                pose.setResultListener(new ResultListener<PoseResult>() {
                    @Override
                    public void run(PoseResult result) {
                        Log.v("Pose", "[TS:" + ( SystemClock.elapsedRealtime() - time)+ "] " + getPoseLandmarksDebugString(result.poseLandmarks()));
                        imageView.setResult(result);
                        imageView.update();
                    }
                });
//                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//                bmp = Bitmap.createScaledBitmap(bmp,320,480,true);
//                time = SystemClock.elapsedRealtime();
//                pose.send(bmp, time);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//                        bmp = Bitmap.createScaledBitmap(bmp,320,480,true);
//                        time = SystemClock.elapsedRealtime();
//                        pose.send(bmp,time);
//
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//                                bmp = Bitmap.createScaledBitmap(bmp,320,480,true);
//                                time = SystemClock.elapsedRealtime();
//                                pose.send(bmp,time);
//
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
//                                        bmp = Bitmap.createScaledBitmap(bmp,320,480,true);
//                                        time = SystemClock.elapsedRealtime();
//                                        pose.send(bmp,time);
//                                    }
//                                }, 1);
//                            }
//                        }, 1);
//                    }
//                }, 1);
            }
        }).start();
    }

    //?????? ???????????? landmark??? ????????? ???????????? ??? ??????.
    //[0.0 , 1.0] ?????? normazlized ??? coordinate -> image width, height
    private static String getPoseLandmarksDebugString(LandmarkProto.NormalizedLandmarkList poseLandmarks) {
        String poseLandmarkStr = "Pose landmarks: " + poseLandmarks.getLandmarkCount() + "\n";
        ArrayList<PoseLandMark> poseMarkers= new ArrayList<PoseLandMark>();
        int landmarkIndex = 0;
        for (LandmarkProto.NormalizedLandmark landmark : poseLandmarks.getLandmarkList()) {
            PoseLandMark marker = new PoseLandMark(landmark.getX(),landmark.getY(),landmark.getVisibility());
//          poseLandmarkStr += "\tLandmark ["+ landmarkIndex+ "]: ("+ (landmark.getX()*720)+ ", "+ (landmark.getY()*1280)+ ", "+ landmark.getVisibility()+ ")\n";
            ++landmarkIndex;
            poseMarkers.add(marker);
        }
        // Get Angle of Positions
        double rightAngle = getAngle(poseMarkers.get(16),poseMarkers.get(14),poseMarkers.get(12));
        double leftAngle = getAngle(poseMarkers.get(15),poseMarkers.get(13),poseMarkers.get(11));
        double rightKnee = getAngle(poseMarkers.get(24),poseMarkers.get(26),poseMarkers.get(28));
        double leftKnee = getAngle(poseMarkers.get(23),poseMarkers.get(25),poseMarkers.get(27));
        double rightShoulder = getAngle(poseMarkers.get(14),poseMarkers.get(12),poseMarkers.get(24));
        double leftShoulder = getAngle(poseMarkers.get(13),poseMarkers.get(11),poseMarkers.get(23));
        Log.v("Pose","======Degree Of Position]======\n"+
                "rightAngle :"+rightAngle+"\n"+
                "leftAngle :"+leftAngle+"\n"+
                "rightHip :"+rightKnee+"\n"+
                "leftHip :"+leftKnee+"\n"+
                "rightShoulder :"+rightShoulder+"\n"+
                "leftShoulder :"+leftShoulder+"\n");
        return poseLandmarkStr;
        /*
           16 ?????? ?????? 14 ?????? ????????? 12 ?????? ?????? --> ????????? ??????
           15 ?????? ?????? 13 ?????? ????????? 11 ?????? ?????? --> ???  ??? ??????
           24 ?????? ?????? 26 ?????? ??????   28 ?????? ?????? --> ???????????? ??????
           23 ?????? ?????? 25 ?????? ??????   27 ?????? ?????? --> ??? ?????? ??????
           14 ?????? ?????? 12 ?????? ??????   24 ?????? ?????? --> ?????? ???????????? ??????
           13 ???   ?????? 11 ???  ??????   23  ???  ?????? --> ?????? ???????????? ??????
        */
    }
    static double getAngle(PoseLandMark firstPoint, PoseLandMark midPoint, PoseLandMark lastPoint) {
        double result =
                Math.toDegrees(
                        Math.atan2(lastPoint.getY() - midPoint.getY(),lastPoint.getX() - midPoint.getX())
                                - Math.atan2(firstPoint.getY() - midPoint.getY(),firstPoint.getX() - midPoint.getX()));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }
}