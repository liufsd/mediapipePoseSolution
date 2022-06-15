// Copyright 2021 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.myapplication;

import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.mediapipe.formats.proto.LandmarkProto;

public class DetectionResultImageView extends AppCompatImageView {
  private static final String TAG = "FaceDetectionResultImageView";

  private static final int KEYPOINT_COLOR = Color.RED;
  private static final int KEYPOINT_RADIUS = 8; // Pixels
  private static final int BBOX_COLOR = Color.GREEN;
  private static final int BBOX_THICKNESS = 5; // Pixels
  private Bitmap latest;

  public DetectionResultImageView(Context context) {
    super(context);
    setScaleType(ScaleType.FIT_CENTER);
  }

  public void setResult(PoseResult result) {
    if (result == null) {
      return;
    }
    Bitmap bmInput = result.inputBitmap();
    int width = bmInput.getWidth();
    int height = bmInput.getHeight();
    latest = Bitmap.createBitmap(width, height, bmInput.getConfig());
    Canvas canvas = new Canvas(latest);

    canvas.drawBitmap(bmInput, new Matrix(), null);
    int numDetected = result.poseLandmarks().getLandmarkCount();
    for (int i = 0; i < numDetected; ++i) {
      drawDetectionOnCanvas(result.poseLandmarks().getLandmarkList().get(i), canvas, width, height);
    }
  }

  public void update() {
    postInvalidate();
    if (latest != null) {
      setImageBitmap(latest);
    }
  }

  private void drawDetectionOnCanvas(LandmarkProto.NormalizedLandmark detection, Canvas canvas, int width, int height) {
    // Draw keypoints.
    Paint keypointPaint = new Paint();
    keypointPaint.setColor(KEYPOINT_COLOR);
    int xPixel =
            min(
                    (int) (detection.getX() * width),
                    width - 1);
    int yPixel =
            min(
                    (int) (detection.getY() * height),
                    height - 1);
    canvas.drawCircle(xPixel, yPixel, KEYPOINT_RADIUS, keypointPaint);
  }
}
