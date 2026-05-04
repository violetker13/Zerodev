package org.example.api.blockbench;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;
public class ObjLoader {
    public static String ObjLoad(String sPath) {
        Path path = Path.of(sPath);
        File file = path.toFile();
        StringBuilder content = new StringBuilder();

        try (Scanner myReader = new Scanner(file)) {
            while (myReader.hasNextLine()) {
                content.append(myReader.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }

        return content.toString();
    }
}