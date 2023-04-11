package com.ondrad.nekos;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class nekoOverlay extends Overlay {

    private BufferedImage image;

    @Inject
    private nekoConfig config;


    @Inject
    nekoOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }


    public void GETRequest() throws IOException {
        String urlName = "https://nekos.life/api/v2/img/neko";
        URL urlForGetReq = new URL(urlName);
        String read;
        HttpURLConnection connection = (HttpURLConnection) urlForGetReq.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cache-Control", "no-cache");
        int codeResponse = connection.getResponseCode();
        if (codeResponse == HttpURLConnection.HTTP_OK) {
            InputStreamReader isrObj = new InputStreamReader(connection.getInputStream());
            BufferedReader bf = new BufferedReader(isrObj);
            StringBuilder responseStr = new StringBuilder();
            while ((read = bf.readLine()) != null) {
                responseStr.append(read);
            }
            bf.close();
            connection.disconnect();

            //IDK how to make this better
            String responseStrString = responseStr.toString();
            String replacedresponse = responseStrString.replace("{\"url\":\"", "");
            String replacedresponse2 = replacedresponse.replace("\"}", "");

            URL imageUrl = new URL(replacedresponse2);

            synchronized (ImageIO.class)
            {
                image = ImageIO.read(imageUrl);
            }

        } else {
            System.out.println("GET Request did not work");
        }

    }




    @Override
    public Dimension render(Graphics2D graphics)
    {
        graphics.drawImage(image, config.xpos(), config.ypos(), config.dimension().width, config.dimension().height, null);
        return null;
    }
}
