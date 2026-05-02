package org.example.extras;

import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
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
import java.util.concurrent.Executors;

public class Utils {
    private static final Random RANDOM = new Random();
    private static HttpServer httpServer;
    private static String packDownloadUrl;

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










    public static Component ColorizeText(String text) {
        if (text == null || text.isEmpty()) return Component.empty();

        Component result = Component.empty();
        String[] parts = text.split("&");

        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                if (!parts[i].isEmpty()) result = result.append(Component.text(parts[i]));
                continue;
            }
            if (parts[i].isEmpty()) continue;

            String rest;
            TextColor color = null;

            if (parts[i].startsWith("#") && parts[i].length() >= 7) {
                String hexCode = parts[i].substring(0, 7); // Извлекаем #FFFFFF
                color = TextColor.fromHexString(hexCode);
                rest = parts[i].substring(7); // Остаток текста после HEX
            } else {
                // Стандартные коды (&0-&f)
                char code = parts[i].charAt(0);
                rest = parts[i].substring(1);
                color = switch (code) {
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
            }

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
