package com.weighbridge.config;

import com.weighbridge.camera.services.FrameCaptureService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final FrameCaptureService frameCaptureService;

    public WebSocketConfig(FrameCaptureService frameCaptureService) {
        this.frameCaptureService = frameCaptureService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String[] rtspUrls = {
                "rtsp://admin:Techn0l0gy@172.16.20.92:554/cam/realmonitor?channel=1&subtype=0",
                "rtsp://admin:Techn0l0gy@172.16.20.93:554/cam/realmonitor?channel=1&subtype=0",
                "rtsp://admin:Techn0l0gy@172.16.20.93:554/cam/realmonitor?channel=1&subtype=0",
                "rtsp://admin:Techn0l0gy@172.16.20.93:554/cam/realmonitor?channel=1&subtype=0",
                "rtsp://admin:Techn0l0gy@172.16.20.93:554/cam/realmonitor?channel=1&subtype=0",
                "rtsp://admin:Techn0l0gy@172.16.20.93:554/cam/realmonitor?channel=1&subtype=0",
                // Add more RTSP URLs as needed
        };

        for (int i = 0; i < rtspUrls.length; i++) {
            registry.addHandler(new FrameWebSocketHandler(frameCaptureService, rtspUrls[i]), "/ws/frame" + (i + 1))
                    .setAllowedOrigins("*");
        }
    }
}

