import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class DateSorter {

    private static final String TR_PATH = "...\\LogReader\\src\\main\\resources\\transactions_by_users";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd HH:mm:ss']'");


    public void sortLogsByDate() throws IOException {
        File dir = new File(TR_PATH);

        //чтение всех файлов с расширением .log
        File[] userFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".log"));


        for (File userFile : userFiles) {
            List<String> lines = Files.readAllLines(userFile.toPath());

            // сортировка, используя Comparator из Java SE
            lines.sort(Comparator.comparing(this::extractTimestamp));


            Files.write(userFile.toPath(), lines);
            System.out.println("Sorted file: " + userFile.getName());
        }
    }

    //получение времени, по которому будет сортироваться
    private LocalDateTime extractTimestamp(String logLine) {
        try {
            String time = LogReader.extractTime(logLine);

            return LocalDateTime.parse(time, formatter);
        } catch (Exception e) {
            return LocalDateTime.MAX;
        }
    }

    public static void main(String[] args) {
        try {
            new DateSorter().sortLogsByDate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
