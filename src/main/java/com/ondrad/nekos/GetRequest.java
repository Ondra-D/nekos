package com.ondrad.nekos;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GetRequest {
    private final OkHttpClient client;

    public GetRequest(OkHttpClient client) {
        this.client = client;
    }

    public BufferedImage GETRequest(String endpoint) throws IOException {
        Request apiRequest = new Request.Builder()
                .url(endpoint)
                .get()
                .build();

        try (Response apiResponse = client.newCall(apiRequest).execute()) {
            if (!apiResponse.isSuccessful()) {
                return null;
            }

            String jsonResponse = apiResponse.body().string();
            String imageUrl = jsonResponse
                    .replace("{\"url\":\"", "")
                    .replace("\"}", "");

            Request imageRequest = new Request.Builder()
                    .url(imageUrl)
                    .build();

            try (Response imageResponse = client.newCall(imageRequest).execute()) {
                if (!imageResponse.isSuccessful()) {
                    return null;
                }

                try (InputStream inputStream = imageResponse.body().byteStream()) {
                    return ImageIO.read(inputStream);
                }
            }
        }
    }
}
