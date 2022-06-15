/*******************************************************************************
 *
 * CopyRight © BILIBILI 2022 All Rights Reserved
 * Company: 上海哔哩哔哩网络科技有限公司
 *
 ******************************************************************************/
package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.AndroidPacketGetter;
import com.google.mediapipe.framework.MediaPipeException;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.solutioncore.ImageSolutionBase;
import com.google.mediapipe.solutioncore.OutputHandler;
import com.google.mediapipe.solutioncore.ResultListener;
import com.google.mediapipe.solutioncore.SolutionInfo;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author (SK) liupeng@bilibili.com
 * @date 2022/6/15
 */
public class Pose extends ImageSolutionBase {
    private static final String GPU_GRAPH_NAME = "pose_tracking_gpu_landmark_image.binarypb";
    private static final String CPU_GRAPH_NAME = "pose_tracking_cpu_landmark_image.binarypb";
    private static final String IMAGE_INPUT_STREAM = "image";
    private static final ImmutableList<String> OUTPUT_STREAMS =
            ImmutableList.of(
                    "pose_landmarks",
                    "throttled_image");
    private static final int INDEX_POSE_LANDMARKS = 0;
    private static final int INDEX_INPUT_IMAGE = 1;
    private final OutputHandler<PoseResult> outputHandler;

    public Pose(Context context, PoseOptions options) {
        outputHandler = new OutputHandler<>();

        outputHandler.setOutputConverter(
                packets -> {
                    Log.e("Pose","setOutputConverter==>" + packets.size());
                    PoseResult.Builder poseResultBuilder = PoseResult.builder();
                    try {
//                        Packet imagePacket = packets.get(1);
//                        Bitmap bitmap = AndroidPacketGetter.getBitmapFromRgba(imagePacket);
//                        Log.e("Pose","setOutputConverter==>bitmap==>" + bitmap.getWidth()  +  " " + bitmap.getHeight());
//                        bitmap.recycle();
                        Packet packet = packets.get(INDEX_POSE_LANDMARKS);
                        byte[] landmarksRaw = PacketGetter.getProtoBytes(packet);
                        LandmarkProto.NormalizedLandmarkList poseLandmarks = LandmarkProto.NormalizedLandmarkList.parseFrom(landmarksRaw);
                        Log.e("Pose","setOutputConverter==>poseLandmarks==>" + poseLandmarks.getLandmarkCount());
//                        LandmarkProto.NormalizedLandmarkList poseLandmarks = PacketGetter.getProto(packet, LandmarkProto.NormalizedLandmarkList.class);
                        poseResultBuilder.setPoseLandmarks(poseLandmarks);
                    } catch (MediaPipeException e) {
//                        reportError("Error occurs while getting MediaPipe pose landmarks.", e);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                    return poseResultBuilder
                            .setImagePacket(packets.get(INDEX_INPUT_IMAGE))
                            .setTimestamp(
                                    staticImageMode ? Long.MIN_VALUE : packets.get(INDEX_INPUT_IMAGE).getTimestamp())
                            .build();
                });

        SolutionInfo solutionInfo =
                SolutionInfo.builder()
                        .setBinaryGraphPath(GPU_GRAPH_NAME)
                        .setImageInputStreamName(IMAGE_INPUT_STREAM)
                        .setOutputStreamNames(OUTPUT_STREAMS)
                        .setStaticImageMode(true)
                        .build();

        initialize(context, solutionInfo, outputHandler);
        Map<String, Packet> inputSidePackets = new HashMap<>();
        //inputSidePackets.put(NUM_HANDS, packetCreator.createInt32(options.maxNumHands()));
        //inputSidePackets.put(MODEL_COMPLEXITY, packetCreator.createInt32(options.modelComplexity()));
//        inputSidePackets.put(USE_PREV_LANDMARKS, packetCreator.createBool(!options.staticImageMode()));
        start(inputSidePackets);
    }

    @Override
    public void send(Bitmap inputBitmap, long timestamp) {
        Log.e("Pose","send==>" );
        super.send(inputBitmap, timestamp);
    }

    public void setResultListener(ResultListener<PoseResult> listener) {
        this.outputHandler.setResultListener(listener);
    }
}
