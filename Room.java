import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Room extends JFrame {

    private Card loggedInUser;
    private List<String> accessLogs;
    private List<String> loginLogs;
    private JLabel timeLabel; // แสดงเวลา
    private static final String[][] ROOMS = {
            {"Lobby", "Canteen", "Bar", "Room 104", "Room 105", "Room 106", "Room 107", "Room 108", "Room 109", "Room 110"},
            {"Room 201", "Room 202", "Room 203", "Room 204", "Room 205", "Room 206", "Room 207", "Room 208", "Room 209", "Room 210"},
            {"Room 301", "Room 302", "Room 303", "Room 304", "Room 305", "Room 306", "Room 307", "Room 308", "Room 309", "Room 310"},
            {"Room 401", "Room 402", "Room 403", "Meeting Room", "Room 405", "Room 406", "Room 407", "Room 408", "Room 409", "Room 410"},
            {"Director's Room", "Vice Director's Room", "CEO's Room", "CIO's Room", "Server Room", "CTO's Room", "COO's Room", "CFO's Room", "Meeting Room", "VIP Lobby"}
    };

    private JButton[] floorButtons = new JButton[5];

    public Room(Card user, List<String> accessLogs, List<String> loginLogs) {
        this.loggedInUser = user;
        this.accessLogs = (accessLogs != null) ? accessLogs : new ArrayList<>();
        this.loginLogs = (loginLogs != null) ? loginLogs : new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setTitle("SCGM Research Institute - Room Access");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel menuPanel = new JPanel(new GridLayout(1, 7));
        add(menuPanel, BorderLayout.NORTH);

        timeLabel = new JLabel();
        updateTime();
        menuPanel.add(timeLabel);

        JPanel roomPanel = new JPanel(new GridLayout(2, 5));
        add(roomPanel, BorderLayout.CENTER);

        for (int i = 1; i <= 5; i++) {
            int floor = i;
            floorButtons[i - 1] = new JButton("Floor " + floor);
            floorButtons[i - 1].addActionListener(e -> selectFloor(floor, roomPanel));
            menuPanel.add(floorButtons[i - 1]);
        }

        JButton logoutButton = new JButton("Main Menu");
        logoutButton.addActionListener(e -> returnToMainMenu());
        menuPanel.add(logoutButton);

        startClock(); // เริ่มนับเวลาแบบเรียลไทม์
    }

    private void updateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeLabel.setText("Time: " + LocalTime.now().format(formatter));
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
    }

    private void selectFloor(int floor, JPanel roomPanel) {
        resetFloorButtonColors();
        floorButtons[floor - 1].setBackground(Color.GREEN);
        showRooms(floor, roomPanel);
    }

    private void resetFloorButtonColors() {
        for (JButton button : floorButtons) {
            button.setBackground(null);
        }
    }

    private void showRooms(int floor, JPanel roomPanel) {
        roomPanel.removeAll();
        String[] rooms = getRoomsByFloor(floor);

        for (String room : rooms) {
            JButton roomButton = new JButton(room);
            roomButton.addActionListener(e -> {
                boolean accessGranted = userHasAccess(floor, room);
                logRoomAccess(room, accessGranted);

                if (accessGranted) {
                    JOptionPane.showMessageDialog(this, "Room: " + room);
                } else {
                    JOptionPane.showMessageDialog(this, "Access denied");
                }
            });
            roomPanel.add(roomButton);
        }

        roomPanel.revalidate();
        roomPanel.repaint();
    }

    private static String[] getRoomsByFloor(int floor) {
        return (floor >= 1 && floor <= 5) ? ROOMS[floor - 1] : new String[]{};
    }

    private boolean userHasAccess(int floor, String room) {
        if (loggedInUser.getLevel() == 'S') {
            return true;
        } else if (floor == 5) {
            if (loggedInUser.getLevel() == 'A') {
                LocalTime now = LocalTime.now();
                return now.isAfter(LocalTime.of(8, 59)) && now.isBefore(LocalTime.of(16, 1));
            }
            return false;
        } else if (room.equals("Bar")) {
            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.of(18, 0);
            LocalTime endTime = LocalTime.of(2, 0);
            return now.isAfter(startTime) || now.isBefore(endTime); // แก้ให้ถูกต้อง
        } else if (loggedInUser.getLevel() != 'A' && floor == 4) {
            return false;
        } else {
            return true;
        }
    }

    private void logRoomAccess(String room, boolean accessGranted) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String status = accessGranted ? "granted" : "denied";
        String log = loggedInUser.getName() + " attempted to access " + room + " at " + timestamp + " - Access " + status;
        accessLogs.add(log);
        AuditLog.saveLogToFile(log);
        AuditLog.updateLogs();
    }

    private void returnToMainMenu() {
        String logoutLog = "User " + loggedInUser.getName() + " (Level: " + loggedInUser.getLevel() + ") logged out at "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        loginLogs.add(logoutLog);

        App mainMenu = new App(loggedInUser, accessLogs, loginLogs);
        mainMenu.setVisible(true);
        this.setVisible(false);
    }
}
