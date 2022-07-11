# mediapipePoseSolution

https://github.com/google/mediapipe/issues/178

```
bazel build -c opt --linkopt="-s" \
--host_crosstool_top=@bazel_tools//tools/cpp:toolchain \
--fat_apk_cpu=arm64-v8a,armeabi-v7a //mediapipe/java/com/google/mediapipe/solutioncore:solution_core
```
