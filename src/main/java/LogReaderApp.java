import java.io.*;
import java.util.*;

public class LogReaderApp {

    private static final String LOGS_PATH = "...\\LogReader\\src\\main\\resources\\logs";
    private static final String OUTPUT_PATH = "...\\LogReader\\src\\main\\resources\\transactions_by_users";

    public void saveUserLogsToDirectory() throws IOException {
        File logsDir = new File(LOGS_PATH);

        if (!logsDir.exists() || !logsDir.isDirectory()) {
            throw new FileNotFoundException("Logs directory not found: " + logsDir.getAbsolutePath());
        }


        StringBuilder sb = new StringBuilder();

        File[] logFiles = logsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".log"));
        //показ ошибки об отсутствии исходных файлов
        if (logFiles == null || logFiles.length == 0) {
            System.out.println("No log files found in directory: " + logsDir.getAbsolutePath());
            return;
        }

        //получение всех исходных файлов из директории
        for (File logF : logFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(logF))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
        }

        //использование класса LogReader, который распределяет логи по пользователям
        LogReader reader = new LogReader(sb.toString());
        Map<String, List<String>> userLogs = reader.divideLogs();

        //показ ошибки об отсутствии корректного путя для сохранения результата
        File outDir = new File(OUTPUT_PATH);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        // добавление логов для каждого пользователя, избегание перезаписи уже существующего результата
        for (Map.Entry<String, List<String>> entry : userLogs.entrySet()) {
            String user = entry.getKey();
            List<String> logs = entry.getValue();

            File userF = new File(outDir, user + ".log");

            Set<String> existingLines = new HashSet<>();
            if (userF.exists()) {
                try (BufferedReader rFile = new BufferedReader(new FileReader(userF))) {
                    String fLine;
                    while ((fLine = rFile.readLine()) != null) {
                        existingLines.add(fLine.trim());
                    }
                }
            }

            try (BufferedWriter wr = new BufferedWriter(new FileWriter(userF, true))) {
                for (String logLine : logs) {
                    if (!existingLines.contains(logLine.trim())) {
                        wr.write(logLine);
                        wr.newLine();
                    }
                }
            }
        }

        System.out.println("Logs appended to directory: " + outDir.getAbsolutePath());
    }

    public static void main(String[] args) {
        try {
            new LogReaderApp().saveUserLogsToDirectory();
            DateSorter.main(args);
            FinalBalance.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}