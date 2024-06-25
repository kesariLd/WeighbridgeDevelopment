package com.weighbridge.camera.services.impl;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import com.weighbridge.camera.services.FrameCaptureService;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;
@Service
public class FrameCaptureServiceImpl implements FrameCaptureService {

    public void streamFrames(String rtspUrl, Consumer<byte[]> frameConsumer) throws Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(rtspUrl);
        
        frameGrabber.setOption("rtsp_transport", "tcp");
        frameGrabber.setOption("stimeout", "3000000");

        try {
            frameGrabber.start();
            System.out.println("Frame grabber started successfully for URL: " + rtspUrl);
        } catch (Exception e) {
            System.err.println("Failed to start frame grabber: " + e.getMessage());
            throw new Exception("Failed to start frame grabber: " + e.getMessage(), e);
        }

        Java2DFrameConverter converter = new Java2DFrameConverter();

        try {
            while (true) {
                Frame frame = frameGrabber.grab();
                if (frame == null) {
                    System.err.println("Frame is null, stream might have ended.");
                    throw new Exception("Frame is null, stream might have ended.");
                }

                BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpeg", baos);
                    frameConsumer.accept(baos.toByteArray());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                frameGrabber.stop();
                System.out.println("Frame grabber stopped for URL: " + rtspUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}