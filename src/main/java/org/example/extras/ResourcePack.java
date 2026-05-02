package org.example.extras;

import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.minestom.server.entity.Player;
import org.example.Main;
import org.reflections.vfs.ZipDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePack {
    static HttpServer httpServer;
    public static String packUrl = null;
    public static String packHash = null;

    private static String hostPack(Path packFile, int localPort, String publicAddress) throws Exception {
        if (!Files.exists(packFile)) {
            throw new RuntimeException("[Pack] Файл не найден: " + packFile.toAbsolutePath());
        }

        if (httpServer != null) {
            httpServer.stop(1);
            httpServer = null;
            Thread.sleep(500);
        }

        byte[] packBytes = Files.readAllBytes(packFile);
        InetSocketAddress address = new InetSocketAddress(localPort);
        httpServer = HttpServer.create(address, 0);
        httpServer.createContext("/pack.zip", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/zip");
            exchange.sendResponseHeaders(200, packBytes.length);
            try (var os = exchange.getResponseBody()) {
                os.write(packBytes);
            }
        });
        httpServer.setExecutor(Executors.newSingleThreadExecutor());
        httpServer.start();
        String url = "http://" + publicAddress + "/pack.zip";
        System.out.println("[Pack] HTTP-сервер запущен: " + url);
        return url;
    }
    private static String sha1Hash(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] bytes = Files.readAllBytes(file);
        byte[] hash = digest.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public static void LoadResourcePack(Player player) {
        if (packUrl != null) {player.sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs(ResourcePackInfo.resourcePackInfo().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).uri(URI.create(packUrl)).hash(packHash).build()).required(false).build());
        }
    }
    public static void init() {
        try {
            Path zipPath = Path.of("resourcepack/pack.zip");
            zipDirectory(Path.of("resourcepack/pack"), zipPath);

            packUrl = hostPack(zipPath, 8181, "minecraft.zerodev.playit.plus:1043");
            packHash = sha1Hash(zipPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void zipDirectory(Path sourceDir, Path zipPath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

}
