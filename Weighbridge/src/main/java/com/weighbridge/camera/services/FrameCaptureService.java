package com.weighbridge.camera.services;

import java.util.function.Consumer;

public interface FrameCaptureService {
    void streamFrames(String rtspUrl, Consumer<byte[]> frameConsumer) throws Exception;
}
