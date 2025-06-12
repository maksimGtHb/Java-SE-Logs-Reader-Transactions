import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

public class FinalBalance {

    private static final String TR_DIR = "...\\LogReader\\src\\main\\resources\\transactions_by_users";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");

    public void appendBalance() throws IOException {
        File dir = new File(TR_DIR);
        File[] logFiles = dir.listFiles((d, n) -> n.toLowerCase().endsWith(".log"));

        //сброс если файлы не считаны
        if (logFiles == null) return;

        for (File userF : logFiles) {
            List<String> lines = Files.readAllLines(userF.toPath());

            String username = getUser(userF.getName());
            double balance = calculateBalance(lines);

            // получение текущего времени
            String currentTime = "[" + LocalDateTime.now().format(TIME_FORMAT) +"]";

            String finalBalanceLine = String.format("%s %s final balance %.2f", currentTime, username, balance);

            // добавление лога о балансе в обрабатываемый файл
            try (BufferedWriter wr = new BufferedWriter(new FileWriter(userF, true))) {
                wr.newLine();
                wr.write(finalBalanceLine);
            }

            System.out.println("Appended final balance for user " + username + " in file " + userF.getName());
        }
    }

    //метод получение имени user по имени файла
    private String getUser(String fileN) {

        return fileN.replace(".log", "");
    }

    //метод для расчета баланса
    private double calculateBalance(List<String> lines) {
        double balance = 0.0;

        Pattern inquiryPattern = Pattern.compile("balance inquiry (\\d+(\\.\\d+)?)");

        Pattern transferredPattern = Pattern.compile("transferred (\\d+(\\.\\d+)?) to user\\w+");

        Pattern receivedPattern = Pattern.compile("received (\\d+(\\.\\d+)?) from user\\w+");

        Pattern withdrewPattern = Pattern.compile("withdrew (\\d+(\\.\\d+)?)");

        for (String line : lines) {

            //равно
            Matcher mBalance = inquiryPattern.matcher(line);
            if (mBalance.find()) {
                balance = Double.parseDouble(mBalance.group(1));
                continue;
            }

            //вычитание
            Matcher mTransferred = transferredPattern.matcher(line);
            if (mTransferred.find()) {
                balance -= Double.parseDouble(mTransferred.group(1));
                continue;
            }

            //добавление
            Matcher mReceived = receivedPattern.matcher(line);
            if (mReceived.find()) {
                balance += Double.parseDouble(mReceived.group(1));
                continue;
            }

            //вычитание (снятие)
            Matcher mWithdrew = withdrewPattern.matcher(line);
            if (mWithdrew.find()) {
                balance -= Double.parseDouble(mWithdrew.group(1));

            }
        }

        return balance;
    }

    public static void main(String[] args) {
        try {
            new FinalBalance().appendBalance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
