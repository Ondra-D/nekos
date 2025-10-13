package com.ondrad.nekos;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GetRequest {
    private final OkHttpClient client;
    private final Gson gson;

    @Inject
    public GetRequest(OkHttpClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    public List<BufferedImage> GETRequest(String endpoint) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        Request apiRequest = new Request.Builder()
                .url(endpoint)
                .get()
                .build();

        try (Response apiResponse = client.newCall(apiRequest).execute()) {
            if (!apiResponse.isSuccessful()) {
                log.warn("API request failed: {}", apiResponse);
                return images;
            }

            ResponseBody body = apiResponse.body();
            if (body == null) {
                return images;
            }

            String jsonResponse = body.string();
            jsonResponse = jsonResponse.trim();

            try {
                JsonObject obj = gson.fromJson(jsonResponse, JsonObject.class);
                String imageUrl = obj.has("url") ? obj.get("url").getAsString() : null;
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    BufferedImage img = fetchImage(imageUrl);
                    if (img != null) {
                        images.add(img);
                    }
                }
            } catch (Exception e) {
                log.warn("Could not parse JSON or extract image URL: {}", jsonResponse, e);
            }
        }
        return images;
    }

    private BufferedImage fetchImage(String imageUrl) throws IOException {
        Request imageRequest = new Request.Builder()
                .url(imageUrl)
                .build();
        try (Response imageResponse = client.newCall(imageRequest).execute()) {
            if (!imageResponse.isSuccessful()) {
                log.debug("Image request failed: {}", imageResponse);
                return null;
            }

            ResponseBody body = imageResponse.body();
            if (body == null) {
                return null;
            }

            try (InputStream inputStream = body.byteStream()) {
                try {
                    return ImageIO.read(inputStream);
                } catch (Exception e) {
                    log.debug("Failed to decode image: {} -> {}", imageUrl, e.getMessage());
                    return null;
                }
            }
        }
    }
}