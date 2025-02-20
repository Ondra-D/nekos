package com.ondrad.nekos;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

public class nekoOverlay extends Overlay {

    private BufferedImage image;
    private VolatileImage volatileImage;
    private final Object imageLock = new Object();

    @Inject
    private nekoConfig config;

    @Inject
    nekoOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        synchronized (imageLock) {
            if (volatileImage != null) {
                // Apply opacity
                AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) config.opacity() / 100);
                graphics.setComposite(alphaComposite);

                // Draw the image final volatileImage
                graphics.drawImage(volatileImage, config.xpos(), config.ypos(), null);
            }
        }
        return null;
    }

    public void updateImage(BufferedImage newImage) {
        synchronized (imageLock) {
            this.image = newImage;
            // Scale the image
            Image scaledImage = image.getScaledInstance(config.dimension().width, config.dimension().height, Image.SCALE_SMOOTH);
            BufferedImage bufferedScaledImage = toBufferedImage(scaledImage);
            this.volatileImage = createVolatileImage(bufferedScaledImage);
        }
    }

    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // Create a BufferedImage
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image onto the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    private VolatileImage createVolatileImage(BufferedImage image) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        // Create a VolatileImage
        VolatileImage volatileImage = gc.createCompatibleVolatileImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);

        // Draw the BufferedImage onto the VolatileImage
        Graphics2D g2d = volatileImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return volatileImage;
    }
}