import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Zapis {
    private static final String FILE_NAME = "highscores.txt";

    public static void zapiszHighscore(String playerName, int highscore, int defeatedEnemies) {
        String entry = playerName + " Twój wynik: " + highscore + " Pokonani przeciwnicy: " + defeatedEnemies;

        try (Stream<String> lines = Files.lines(Paths.get(FILE_NAME))) {
            String updatedContent = Stream.concat(lines, Stream.of(entry))
                    .sorted(Comparator.comparingInt(Zapis::extractHighscore).reversed())
                    .collect(Collectors.joining(System.lineSeparator()));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write(updatedContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int extractHighscore(String line) {
        String[] parts = line.split(" ");
        int index = parts[3].lastIndexOf(":");
        return Integer.parseInt(parts[3].substring(index + 1));
    }
}