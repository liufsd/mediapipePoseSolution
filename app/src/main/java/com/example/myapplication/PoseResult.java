/*******************************************************************************
 *
 * CopyRight © BILIBILI 2022 All Rights Reserved
 * Company: 上海哔哩哔哩网络科技有限公司
 *
 ******************************************************************************/
package com.example.myapplication;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.solutioncore.ImageSolutionResult;

/**
 * @author (SK) liupeng@bilibili.com
 * @date 2022/6/15
 */
public class PoseResult extends ImageSolutionResult {
    private final LandmarkProto.NormalizedLandmarkList poseLandmarks;

    public PoseResult(LandmarkProto.NormalizedLandmarkList poseLandmarks,
                      Packet imagePacket,
                      long timestamp) {
        this.poseLandmarks = poseLandmarks;
        this.imagePacket = imagePacket;
        this.timestamp = timestamp;
    }

    public LandmarkProto.NormalizedLandmarkList poseLandmarks() {
        return poseLandmarks;
    }

    public static Builder builder() {
        return new AutoBuilder_PoseResult_Builder();
    }

    public abstract static class Builder {
        abstract Builder setPoseLandmarks(LandmarkProto.NormalizedLandmarkList value);
        abstract Builder setImagePacket(Packet value);
        abstract Builder setTimestamp(long value);

        abstract PoseResult build();
    }

    public static class AutoBuilder_PoseResult_Builder extends Builder {
        private  LandmarkProto.NormalizedLandmarkList poseLandmarks;
        private Packet imagePacket;
        private Long timestamp;

        AutoBuilder_PoseResult_Builder() {
        }


        @Override
        Builder setPoseLandmarks(LandmarkProto.NormalizedLandmarkList value) {
            this.poseLandmarks = value;
            return this;
        }

        Builder setImagePacket(Packet imagePacket) {
            if (imagePacket == null) {
                throw new NullPointerException("Null imagePacket");
            } else {
                this.imagePacket = imagePacket;
                return this;
            }
        }

        Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        PoseResult build() {
            String missing = "";
//            if (this.poseLandmarks == null) {
//                missing = missing + " multiFaceLandmarks";
//            }

            if (this.imagePacket == null) {
                missing = missing + " imagePacket";
            }

            if (this.timestamp == null) {
                missing = missing + " timestamp";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            } else {
                return new PoseResult(this.poseLandmarks, this.imagePacket, this.timestamp);
            }
        }
    }
}