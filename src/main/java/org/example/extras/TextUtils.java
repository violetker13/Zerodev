package org.example.extras;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import javax.annotation.Nullable;

public class TextUtils {
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
    public static Component HoverText(Component text){
            return text.hoverEvent(HoverEvent.showText(text));
    }
    public static Component HoverText(Component text,Component hoverText){
            return text.hoverEvent(HoverEvent.showText(hoverText));

    }
}
