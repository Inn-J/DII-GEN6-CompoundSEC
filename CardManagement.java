import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class CardManagement {
    public JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton createButton, editButton, deleteButton, backButton;
    private Card loggedInUser;
    private List<String> accessLogs;
    private List<String> loginLogs;
    public static ArrayList<Card> cards = new ArrayList<>();
    private JLabel timeLabel;
    private Card newCard;

    static {
        loadCardsFromFile();
    }

    public CardManagement(Card user, List<String> accessLogs, List<String> loginLogs) {
        this.loggedInUser = user;
        this.accessLogs = accessLogs;
        this.loginLogs = loginLogs;
        initialize();
    }


    private void initialize() {
        frame = new JFrame("Card Management - Access Level: " + loggedInUser.getLevel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        ImageIcon icon = new ImageIcon(getClass().getResource("/SCGM Logo.png"));
        frame.setIconImage(icon.getImage());


        timeLabel = new JLabel("Time: --:--:--", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(timeLabel, BorderLayout.CENTER);
        frame.add(timePanel, BorderLayout.NORTH); // 🔹 เปลี่ยนไปไว้ด้านบน

        // ✅ Timer สำหรับอัปเดตเวลา
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Position", "Level"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        createButton = new JButton("Create Card");
        editButton = new JButton("Edit Card");
        deleteButton = new JButton("Delete Card");
        backButton = new JButton("Back to App");


        if (loggedInUser.getLevel() == 'S') {
            createButton.addActionListener(e -> {
                createCard();
                saveCardsToFile();
                loadCardsToTable();
            });
            buttonPanel.add(createButton);
        }

        editButton.addActionListener(e -> editCard());
        deleteButton.addActionListener(e -> deleteCard());
        backButton.addActionListener(e -> backToMain());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        loadCardsToTable();
        frame.setVisible(true);
    }

    public void createCard() {
        JTextField nameField = new JTextField();
        JTextField positionField = new JTextField();
        String[] levels = {"S", "A", "B", "C"};
        JComboBox<String> levelBox = new JComboBox<>(levels);
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Name:", nameField,
                "Position:", positionField,
                "Level:", levelBox,
                "Password:", passwordField,
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Create New Card", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String position = positionField.getText().trim();
            char level = ((String) levelBox.getSelectedItem()).charAt(0);
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || position.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = generateId(level);
            Card newCard;
            switch (level) {
                case 'S' -> newCard = new Card.Class_S(name, id, position,level, password);
                case 'A' -> newCard = new Card.Class_A(name, id, position,level, password) ;
                case 'B' -> newCard = new Card.Class_B(name, id, position,level, password);
                default -> newCard = new Card.Class_C(name, id, position,level, password);
            }

            cards.add(newCard);
            saveCardsToFile();
            loadCardsToTable();
            JOptionPane.showMessageDialog(frame, "Card created successfully!");
            String logEntry = String.format("[%s] Card Created: ID=%s, Name=%s, Level=%c",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    newCard.getId(), newCard.getName(), newCard.getLevel());

            AuditLog.saveLogToFile(logEntry);
            AuditLog.updateLogs();
        }
    }



    private String generateId(char level) {
        // ตรวจสอบว่า level เป็นค่าที่ถูกต้อง
        if ("SABC".indexOf(level) == -1) {
            throw new IllegalArgumentException("Invalid level: " + level);
        }

        int prefix = switch (level) {
            case 'S' -> 1;
            case 'A' -> 2;
            case 'B' -> 3;
            case 'C' -> 4;
            default -> throw new IllegalArgumentException("Invalid level: " + level);
        };

        if (cards == null || cards.isEmpty()) {
            return String.format("%d00", prefix); // เริ่มที่ X00 ถ้ายังไม่มีข้อมูลเลย
        }

        int maxNumber = cards.stream()
                .filter(c -> c.getLevel() == level)
                .mapToInt(c -> {
                    String id = c.getId();
                    if (id == null || id.length() < 2) return -1; // ป้องกัน IndexOutOfBounds
                    try {
                        return Integer.parseInt(id.substring(1));
                    } catch (NumberFormatException e) {
                        return -1; // ถ้า id ไม่สามารถแปลงเป็นตัวเลขได้ ให้ข้ามไป
                    }
                })
                .max().orElse(-1); // ค่าเริ่มต้นเป็น -1 แทน 0

        if (maxNumber < 0) {
            return String.format("%d00", prefix);
        }
        return String.format("%d%02d", prefix, maxNumber + 1);
    }


    private void loadCardsToTable() {
        tableModel.setRowCount(0);
        for (Card card : cards) {
            tableModel.addRow(new Object[]{card.getId(), card.getName(), card.getPosition(), card.getLevel()});
        }
    }

        private static void loadCardsFromFile() {
            cards.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader("cards.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length < 5) continue;

                    String name = data[0];
                    String id = data[1];
                    String position = data[2];
                    char level = data[3].charAt(0);
                    String password = data[4];

                    Card newCard;
                    switch (level) {
                        case 'S' -> newCard = new Card.Class_S(name, id, position, level, password);
                        case 'A' -> newCard = new Card.Class_A(name, id, position, level, password);
                        case 'B' -> newCard = new Card.Class_B(name, id, position, level, password);
                        default -> newCard = new Card.Class_C(name, id, position, level, password);
                    }

                    cards.add(newCard);
                }
            } catch (IOException e) {
                System.out.println("Could not load cards: " + e.getMessage());
            }
        }

    private static void saveCardsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cards.txt"))) {
            for (Card card : cards) {
                writer.write(String.format("%s,%s,%s,%c,%s%n",
                        card.getName(), card.getId(), card.getPosition(), card.getLevel(), card.getPassword()));
            }
        } catch (IOException e) {
            System.out.println("Could not save cards: " + e.getMessage());
        }
    }

    public void editCard() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a card to edit.");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Card selectedCard = cards.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);

        if (selectedCard == null) {
            JOptionPane.showMessageDialog(frame, "Card not found.");
            return;
        }

        selectedCard.getAction().editCard(selectedCard, loggedInUser);
        String logEntry = String.format("[%s] Card Edited: ID=%s, Name: %s, Position: %s , Level: %c",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                selectedCard.getId(), selectedCard.getName(), selectedCard.getPosition(),selectedCard.getLevel());

        AuditLog.saveLogToFile(logEntry);
        AuditLog.updateLogs();
    }


    public void deleteCard() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a card to delete.");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Card selectedCard = cards.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);

        if (selectedCard == null) {
            JOptionPane.showMessageDialog(frame, "Card not found.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this card?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cards.remove(selectedCard);
            saveCardsToFile();
            loadCardsToTable();
            JOptionPane.showMessageDialog(frame, "Card deleted successfully.");
            String logEntry = String.format("[%s] Card Deleted: ID=%s, Name=%s, Level=%c",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    selectedCard.getId(), selectedCard.getName(), selectedCard.getLevel());

            AuditLog.saveLogToFile(logEntry);
            AuditLog.updateLogs();
        }
    }
    private void logAction(String action) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String userInfo = "User: " + loggedInUser.getName() + " (ID: " + loggedInUser.getId() + ")";
        String logEntry = String.format("[%s] %s %s", timestamp, userInfo, action);
        accessLogs.add(logEntry);
        saveLogToFile(logEntry);
        AuditLog.updateLogs();
    }

    private void saveLogToFile(String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("auditlog.txt", true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Could not save log: " + e.getMessage());
        }
    }



    private void backToMain() {
        frame.dispose();
        new App(loggedInUser, accessLogs, loginLogs);
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeLabel.setText("Time: " + now.format(formatter));
    }
}
