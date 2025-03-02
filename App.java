import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App extends JFrame {
    private Card loggedInUser;
    private JLabel timeLabel; // Label สำหรับแสดงเวลา
    private List<String> accessLogs;
    private List<String> loginLogs;

    public App(Card user, List<String> accessLogs, List<String> loginLogs) {
        this.loggedInUser = user;
        this.accessLogs = accessLogs;
        this.loginLogs = loginLogs;
        initComponents();
    }

    private void initComponents() {
        setTitle("SCGM Research Institute - Main Menu");
        setSize(700, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // โหลดไอคอน
        ImageIcon icon = new ImageIcon(getClass().getResource("/SCGM Logo.png"));
        if (icon.getImage() != null) {
            setIconImage(icon.getImage());
        }

        // Welcome label
        JLabel welcomeLabel = new JLabel(
                "Login successful! Welcome, " + loggedInUser.getName()
                        + " (Level: " + loggedInUser.getLevel() + ")",
                SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        JButton cardManagementButton = new JButton("Card Management");
        JButton roomAccessButton = new JButton("Room Access");
        JButton logoutButton = new JButton("Logout");

        buttonPanel.add(cardManagementButton);
        buttonPanel.add(roomAccessButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // กดปุ่ม "Card Management"
        cardManagementButton.addActionListener(e -> {
            dispose();
            new CardManagement(loggedInUser, accessLogs, loginLogs);
        });

        // กดปุ่ม "Room Access"
        roomAccessButton.addActionListener(e -> {
            dispose();
            new Room(loggedInUser, accessLogs, loginLogs).setVisible(true);
        });

        // กดปุ่ม "Audit Log" สำหรับผู้ใช้ระดับ "S"
        if (loggedInUser.getLevel() == 'S') {
            JButton auditLogButton = new JButton("Audit Log");
            buttonPanel.add(auditLogButton);
            auditLogButton.addActionListener(e -> {
                dispose();
                new AuditLog(loginLogs, loggedInUser, accessLogs);
            });
        }

        // กดปุ่ม "Logout"
        logoutButton.addActionListener(e -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logoutLog = "User " + loggedInUser.getName() + " (Level: " + loggedInUser.getLevel() + ") logged out at " + timestamp;

            loginLogs.add(logoutLog); // บันทึกการ logout ลงใน loginLogs
            AuditLog.saveLogToFile(logoutLog); // บันทึกลงไฟล์ auditlog.txt
            AuditLog.updateLogs(); // อัปเดตแสดงผล Log ใน AuditLog GUI

            dispose(); // ปิดหน้าต่างปัจจุบัน
            new Login(); // เปิดหน้า Login ใหม่
        });

        // สร้าง JLabel สำหรับแสดงเวลา
        timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // เพิ่มเวลาลงในส่วนใต้ของหน้าต่าง
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(timeLabel, BorderLayout.CENTER);
        mainPanel.add(timePanel, BorderLayout.SOUTH);

        // ใช้ Timer เพื่ออัปเดตเวลาอัตโนมัติ
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();

        add(mainPanel);
        setVisible(true);
    }

    // เมธอดสำหรับอัปเดตเวลา
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeLabel.setText("Time: " + now.format(formatter));
    }
}
