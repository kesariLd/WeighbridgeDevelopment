package com.weighbridge.config;

import com.weighbridge.camera.services.FrameCaptureService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Base64;

public class FrameWebSocketHandler extends TextWebSocketHandler {

    private final FrameCaptureService frameCaptureService;
    private final String rtspUrl;

    public FrameWebSocketHandler(FrameCaptureService frameCaptureService, String rtspUrl) {
        this.frameCaptureService = frameCaptureService;
        this.rtspUrl = rtspUrl;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        new Thread(() -> {
            try {
                frameCaptureService.streamFrames(rtspUrl, (frameBytes) -> {
                    try {
                        String base64Image = Base64.getEncoder().encodeToString(frameBytes);
                        session.sendMessage(new TextMessage(base64Image));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Cleanup if needed
    }
}
