import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class AuditLog extends JFrame {
    private static List<String> accessLogs = new ArrayList<>();
    private static List<String> loginLogs = new ArrayList<>();
    private Card loggedInUser;
    private static JTextArea logTextArea;

    public AuditLog(List<String> loginLogs, Card loggedInUser, List<String> accessLogs) {
        this.loggedInUser = loggedInUser;
        AuditLog.loginLogs = (loginLogs != null) ? loginLogs : new ArrayList<>();
        AuditLog.accessLogs = (accessLogs != null) ? accessLogs : new ArrayList<>();


        setTitle("SCGM Research Institute - Audit Log");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        ImageIcon icon = new ImageIcon(getClass().getResource("/SCGM Logo.png"));
        this.setIconImage(icon.getImage());

        // สร้าง JTextArea สำหรับแสดง Log
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // โหลด Log ลง JTextArea
        updateLogs();

        // ใส่ JScrollPane เพื่อให้เลื่อนดูได้
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ปุ่ม "Main Menu"
        JButton backButton = new JButton("Main Menu");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> backToMain());
        mainPanel.add(backButton, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
    public static void updateLogs() {
        if (logTextArea == null) return; // 🔹 ป้องกัน NullPointerException

        StringBuilder logContent = new StringBuilder();

        if (!loginLogs.isEmpty()) {
            for (String log : loginLogs) {
                logContent.append(log).append("\n");
            }
            logContent.append("\n");
        }

        if (!accessLogs.isEmpty()) {
            logContent.append("== Access Logs ==\n");
            for (String log : accessLogs) {
                logContent.append(log).append("\n");
            }
            logContent.append("\n");
        }

        logContent.append("== Audit Log File ==\n");
        logContent.append(loadLogsFromFile());

        logTextArea.setText(logContent.toString());
    }

    public static void saveLogToFile(String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("auditlog.txt", true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to audit log file: " + e.getMessage());
        }
    }
    private static String loadLogsFromFile() {
        StringBuilder fileLogs = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("auditlog.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileLogs.append(line).append("\n");
            }
        } catch (IOException e) {
            fileLogs.append("Could not load audit log file.\n");
        }
        return fileLogs.toString();
    }

    private void backToMain() {
        dispose();
        new App(loggedInUser, accessLogs, loginLogs);
    }
}
