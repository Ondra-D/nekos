package com.ondrad.nekos;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest {
    public static BufferedImage GETRequest(String endpoint) throws IOException, InterruptedException {
        URL urlForGetReq = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) urlForGetReq.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cache-Control", "no-cache");
        int codeResponse = connection.getResponseCode();
        if (codeResponse == HttpURLConnection.HTTP_OK) {
            try (InputStreamReader isrObj = new InputStreamReader(connection.getInputStream());
                 BufferedReader bf = new BufferedReader(isrObj)) {
                StringBuilder responseStr = new StringBuilder();
                String read;
                while ((read = bf.readLine()) != null) {
                    responseStr.append(read);
                }

                String responseStrString = responseStr.toString();
                String replacedresponse = responseStrString.replace("{\"url\":\"", "");
                String replacedresponse2 = replacedresponse.replace("\"}", "");

                URL imageUrl = new URL(replacedresponse2);
                return ImageIO.read(imageUrl);
            } finally {
                connection.disconnect();
            }
        }
        return null;
    }
}
