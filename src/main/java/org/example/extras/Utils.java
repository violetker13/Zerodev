package org.example.extras;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Utils {
    private static final Random RANDOM = new Random();

    private static int last = -1;
    private static final Set<String> Citates = Set.of(
            "&7 - Я это не трогал... оно само сломалось",
            "&7 - Работает? Не трогай. Серьёзно, просто не трогай",
            "&7 - Я потом разберусь — классическая ложь самому себе",
            "&7 - Если не понимаешь код — значит писал его ты вчера",
            "&7 - Всё работало до того, как я решил улучшить",
            "&7 - Быстро фиксится только то, что не сломано",
            "&7 - Почему работает — никто не знает, но лучше не трогать",
            "&7 - Я просто поменял одну строчку…",
            "&7 - Этот баг жил тут дольше, чем я в этом проекте",
            "&7 - Документация устарела в момент создания",
            "&7 - Я думал это будет просто",
            "&7 - Логов нет, значит проблема не существует (нет)",
            "&7 - Сначала работает, потом начинает мстить",
            "&7 - Почему оно сломалось именно сейчас?",
            "&7 - Это не костыль, это временное архитектурное решение",
            "&7 - Я это фиксить не буду, оно как-то само держится"
    );

    public static String uploadPack(Path packFile) throws Exception {
        final String OWNER = "violetker13";
        final String REPO = "pack";
        final String TAG = "server-pack";
        HttpClient client = HttpClient.newHttpClient();

        // 1. Проверяем старый релиз
        HttpRequest getRelease = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/tags/" + TAG))
                .header("Authorization", "Bearer " + "TOKEN")
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRelease, HttpResponse.BodyHandlers.ofString());
        System.out.println("[Pack] Проверка релиза: " + getResponse.statusCode());

        if (getResponse.statusCode() == 200) {
            String oldId = getResponse.body().split("\"id\":")[1].split(",")[0].trim();
            System.out.println("[Pack] Удаляем старый релиз id=" + oldId);

            client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases/" + oldId))
                    .header("Authorization", "Bearer " + "TOKEN")
                    .DELETE().build(), HttpResponse.BodyHandlers.ofString());

            client.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/git/refs/tags/" + TAG))
                    .header("Authorization", "Bearer " + "TOKEN")
                    .DELETE().build(), HttpResponse.BodyHandlers.ofString());

            System.out.println("[Pack] Старый релиз удалён");
        }

        // 2. Создаём релиз
        HttpRequest createRelease = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/" + OWNER + "/" + REPO + "/releases"))
                .header("Authorization", "Bearer " + "TOKEN")
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
                .header("Authorization", "Bearer " + "TOKEN")
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
    public static Component ColorizeText(String text) {
        Component result = Component.empty();
        String[] parts = text.split("&");

        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                result = result.append(Component.text(parts[i]));
                continue;
            }
            if (parts[i].isEmpty()) continue;

            char code = parts[i].charAt(0);
            String rest = parts[i].substring(1);
            TextColor color = switch (code) {
                case '0' -> NamedTextColor.BLACK;
                case '1' -> NamedTextColor.DARK_BLUE;
                case '2' -> NamedTextColor.DARK_GREEN;
                case '3' -> NamedTextColor.DARK_AQUA;
                case '4' -> NamedTextColor.DARK_RED;
                case '5' -> NamedTextColor.DARK_PURPLE;
                case '6' -> NamedTextColor.GOLD;
                case '7' -> NamedTextColor.GRAY;
                case '8' -> NamedTextColor.DARK_GRAY;
                case '9' -> NamedTextColor.BLUE;
                case 'a' -> NamedTextColor.GREEN;
                case 'b' -> NamedTextColor.AQUA;
                case 'c' -> NamedTextColor.RED;
                case 'd' -> NamedTextColor.LIGHT_PURPLE;
                case 'e' -> NamedTextColor.YELLOW;
                case 'f' -> NamedTextColor.WHITE;
                default -> null;
            };

            if (color != null) {
                result = result.append(Component.text(rest, color));
            } else {
                result = result.append(Component.text("&" + parts[i]));
            }
        }
        return result;
    }

    public static <T> T getRandomSetElement(Set<T> set) {
        List<T> list = new ArrayList<>(set);
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static String fetchCitate(){
        return getRandomSetElement(Citates);
    }

    public static byte[] getIcon() {
        int next;

        do {
            next = RANDOM.nextInt(16);
        } while (next == last);

        last = next;

        String path = "/home/mihail/IdeaProjects/minestome/icons/sprite_" + next + ".png";

        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
