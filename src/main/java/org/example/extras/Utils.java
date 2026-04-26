package org.example.extras;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Random;
import java.util.Set;

public class Utils {
    private static final Random RANDOM = new Random();

    public static <T> T RandomElementSet(Set<T> set) {
        if (set == null || set.isEmpty()) return null;
        return set.stream()
                .skip(RANDOM.nextInt(set.size()))
                .findFirst()
                .orElse(null);
    }

    public static String uploadPack(Path packFile) throws Exception {
        final String TOKEN = "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";
        final String OWNER = "violetker13";
        final String REPO = "pack";
        final String TAG = "server-pack";

        HttpClient client = HttpClient.newHttpClient();

        // 1. Проверяем старый релиз
        HttpRequest getRelease = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/tags/" + TAG))
                .header("Authorization", "Bearer " + TOKEN)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRelease, HttpResponse.BodyHandlers.ofString());
        System.out.println("[Pack] Проверка релиза: " + getResponse.statusCode());

        if (getResponse.statusCode() == 200) {
            String oldId = getResponse.body().split("\"id\":")[1].split(",")[0].trim();
            System.out.println("[Pack] Удаляем старый релиз id=" + oldId);

            client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/" + oldId))
                    .header("Authorization", "Bearer " + TOKEN)
                    .DELETE().build(), HttpResponse.BodyHandlers.ofString());

            client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/git/refs/tags/" + TAG))
                    .header("Authorization", "Bearer " + TOKEN)
                    .DELETE().build(), HttpResponse.BodyHandlers.ofString());

            System.out.println("[Pack] Старый релиз удалён");
        }

        // 2. Создаём релиз
        HttpRequest createRelease = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases"))
                .header("Authorization", "Bearer " + TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                {
                    "tag_name": "%s",
                    "name": "Server Resource Pack",
                    "body": "Auto upload"
                }
                """.formatted(TAG)))
                .build();

        HttpResponse<String> releaseResponse = client.send(createRelease, HttpResponse.BodyHandlers.ofString());
        System.out.println("[Pack] Создание релиза: " + releaseResponse.statusCode());
        System.out.println("[Pack] Ответ: " + releaseResponse.body());

        if (!releaseResponse.body().contains("\"id\":")) {
            throw new RuntimeException("[Pack] Не удалось создать релиз: " + releaseResponse.body());
        }

        String releaseId = releaseResponse.body().split("\"id\":")[1].split(",")[0].trim();
        System.out.println("[Pack] Релиз создан id=" + releaseId);

        // 3. Загружаем файл
        if (!Files.exists(packFile)) {
            throw new RuntimeException("[Pack] Файл не найден: " + packFile.toAbsolutePath());
        }

        System.out.println("[Pack] Загружаем файл: " + packFile.toAbsolutePath());

        HttpRequest uploadAsset = HttpRequest.newBuilder()
                .uri(URI.create("https://uploads.github.com/repos/" + OWNER + "/" + REPO +
                        "/releases/" + releaseId + "/assets?name=pack.zip"))
                .header("Authorization", "Bearer " + TOKEN)
                .header("Content-Type", "application/zip")
                .POST(HttpRequest.BodyPublishers.ofByteArray(Files.readAllBytes(packFile)))
                .build();

        HttpResponse<String> uploadResponse = client.send(uploadAsset, HttpResponse.BodyHandlers.ofString());
        System.out.println("[Pack] Загрузка файла: " + uploadResponse.statusCode());
        System.out.println("[Pack] Ответ: " + uploadResponse.body());

        if (!uploadResponse.body().contains("browser_download_url")) {
            throw new RuntimeException("[Pack] Не удалось загрузить файл: " + uploadResponse.body());
        }

        String downloadUrl = uploadResponse.body()
                .split("\"browser_download_url\":\"")[1]
                .split("\"")[0];

        System.out.println("[Pack] Готово: " + downloadUrl);
        return downloadUrl;
    }
    public static String sha1Hash(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] bytes = Files.readAllBytes(file);
        byte[] hash = digest.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
