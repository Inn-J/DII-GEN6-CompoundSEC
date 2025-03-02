import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Login extends JFrame {
    private static ArrayList<Card> cards = new ArrayList<>();
    private static Card loggedInUser = null;
    private List<String> accessLogs = new ArrayList<>();
    private List<String> loginLogs = new ArrayList<>();
    private JTextField idField;
    private JPasswordField passwordField;
    private JTextArea displayArea;
    private JLabel timeLabel;

    static {
        loadCardsFromFile();
    }

    private static void loadCardsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("cards.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line: " + line); // Debugging
                String[] data = line.split(",");
                if (data.length == 5) {
                    String name = data[0];
                    String id = data[1];
                    String position = data[2];
                    char level = data[3].charAt(0);
                    String password = data[4];

                    Card card;
                    switch (level) {
                        case 'S': card = new Card.Class_S(name, id, position, level, password); break;
                        case 'A': card = new Card.Class_A(name, id, position, level, password); break;
                        case 'B': card = new Card.Class_B(name, id, position, level, password); break;
                        case 'C': card = new Card.Class_C(name, id, position, level, password); break;
                        default: continue;
                    }
                    System.out.println("Loaded Card: " + card.getId());
                    cards.add(card);
                }
            }
            System.out.println("Total cards loaded: " + cards.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Login() {
        setTitle("SCGM Research Institute - Login");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        // Load icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/SCGM Logo.png"));
        this.setIconImage(icon.getImage());

        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginAction());
        inputPanel.add(loginButton);

        loginPanel.add(inputPanel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        loginPanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.add(timeLabel, BorderLayout.CENTER);

        loginPanel.add(timePanel, BorderLayout.SOUTH);

        add(loginPanel);
        setVisible(true);

        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeLabel.setText("Time: " + now.format(formatter));
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String id = idField.getText();
            String password = new String(passwordField.getPassword());

            System.out.println("Attempting login for ID: " + id); // Debugging line
            System.out.println("Total cards loaded: " + cards.size()); // Debugging line

            for (Card card : cards) {
                System.out.println("Checking card: " + card.getId()); // Debugging line
                if (card.getId().equals(id) && card.checkPassword(password)) {
                    loggedInUser = card;
                    loginLog();
                    new App(loggedInUser, accessLogs, loginLogs);
                    dispose();
                    return;
                }
            }
            displayArea.setText("Invalid ID or password. Try again.\n");
        }
    }

    private void loginLog() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String log = String.format("[%s] User %s (Level: %c) logged in", timestamp, loggedInUser.getName(), loggedInUser.getLevel());
        loginLogs.add(log);
        AuditLog.saveLogToFile(log);
        AuditLog.updateLogs();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
