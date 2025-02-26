package com.ondrad.nekos;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class nekoOverlay extends Overlay {
    private static class ImageState {
        final BufferedImage image;
        final boolean loaded;

        ImageState(BufferedImage image, boolean loaded) {
            this.image = image;
            this.loaded = loaded;
        }
    }

    private volatile ImageState imageState = new ImageState(null, false);
    @Inject
    private nekoConfig config;

    @Inject
    nekoOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        final ImageState currentState = imageState;

        if (currentState.loaded && currentState.image != null) {

            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) config.opacity() / 100));


            graphics.drawImage(
                    currentState.image,
                    config.xpos(),
                    config.ypos(),
                    config.dimension().width,
                    config.dimension().height,
                    null
            );

        }


        return new Dimension(1, 1);
    }

    public void updateImage(BufferedImage newImage) {
        imageState = new ImageState(newImage, newImage != null);
    }
}