package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
        Pose pose = new Pose(this, new PoseOptions());
        pose.setResultListener(new ResultListener<PoseResult>() {
            @Override
            public void run(PoseResult result) {
                Log.v("Pose", "[TS:" + (System.currentTimeMillis() - time)+ "] " + getPoseLandmarksDebugString(result.poseLandmarks()));
                imageView.setResult(result);
                imageView.update();
            }
        });
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        bmp = Bitmap.createScaledBitmap(bmp,480,640,true);
        time = System.currentTimeMillis();
        pose.send(bmp, time);
    }

    //해당 코드에서 landmark의 좌표를 추출해낼 수 있다.
    //[0.0 , 1.0] 으로 normazlized 된 coordinate -> image width, height
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
           16 오른 손목 14 오른 팔꿈치 12 오른 어깨 --> 오른팔 각도
           15 왼쪽 손목 13 왼쪽 팔꿈치 11 왼쪽 어깨 --> 왼  팔 각도
           24 오른 골반 26 오른 무릎   28 오른 발목 --> 오른무릎 각도
           23 왼쪽 골반 25 왼쪽 무릎   27 왼쪽 발목 --> 왼 무릎 각도
           14 오른 팔꿈 12 오른 어깨   24 오른 골반 --> 오른 겨드랑이 각도
           13 왼   팔꿈 11 왼  어깨   23  왼  골반 --> 왼쪽 겨드랑이 각도
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