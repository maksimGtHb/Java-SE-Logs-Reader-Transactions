import java.util.*;

public class LogReader {

    //инициализация лог-файла в виде строки
    String logFile;

    //конструктор класса
    public LogReader(String logFile) {
        this.logFile = logFile;
    }

    //методы для работы с текстом
    List<String> splitIntoLines(String logFile) {
        String[] linesArr = logFile.split("\\r?\\n");
        return new ArrayList<>(Arrays.asList(linesArr));
    }

    static String extractTime(String line) {
        return line.substring(line.indexOf('['), line.indexOf(']') + 1);
    }

    String extractUsername(String line) {
        return line.split(" ")[2];
    }

    public String extractReceiverUsername(String line) {
        if (line.contains("transferred")) {
            return line.split(" ")[6];
        }
        return "";
    }

    //метод составления строки о получении транзакции
    String containReceiverOperation(String operation) {
        if (operation.contains("transferred")) {
            String[] parts = operation.split(" ");
            String sender = parts[0];
            String amount = parts[2];
            String receiver = parts[4];
            return receiver + " received " + amount + " from " + sender;
        }
        return "";
    }

    String extractOperation(String line) {
        return line.substring(line.indexOf("user"));
    }

//метод, который распределяет логи по пользователям и добавляет составленные строки о полученных транзакциях
    public Map<String, List<String>> divideLogs() {
        List<String> logs = splitIntoLines(logFile);
        Map<String, List<String>> userLogs = new HashMap<>();

        for (String line : logs) {
            String user = extractUsername(line);
            userLogs.putIfAbsent(user, new ArrayList<>());
            userLogs.get(user).add(line);

            String operation = extractOperation(line);
            String receiverOperation = containReceiverOperation(operation);

            if (!receiverOperation.trim().isEmpty()) {
                String time = extractTime(line);
                String receiverLog = time + " " + receiverOperation;

                String receiverUsername = extractReceiverUsername(line);

                //сохранение полученных распределнных логов
                userLogs.putIfAbsent(receiverUsername, new ArrayList<>());
                userLogs.get(receiverUsername).add(receiverLog);
            }
        }

        return userLogs;
    }
}
