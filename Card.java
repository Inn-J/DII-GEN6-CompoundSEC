import javax.swing.*;

class Card {
    private String name;
    private String id;
    private String position;
    private char level;
    private String password;

    public Card(String name, String id, String position, char level, String password) {
        this.name = name;
        this.id = id;
        this.position = position;
        this.level = level;
        this.password = password;
    }

    interface Action {
        void editCard(Card card, Card loggedInUser);
        void removeCard(Card card, Card loggedInUser);
    }

    public String getId() {
        return id;
    }

    public char getLevel() {
        return level;
    }

    public String getPosition() {
        return position;
    }

    public String getPassword() {
        return password;
    }


    public void setCard(String name, String position, char level, String password) {
        this.name = name;
        this.position = position;
        this.level = level;
        this.password = password;
    }

    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getName() {
        return name;
    }

    static class Class_S extends Card  {
        public Class_S(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
    }

    static class Class_A extends Card {
        public Class_A(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
    }

    static class Class_B extends Card {
        public Class_B(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
    }

    static class Class_C extends Card {
        public Class_C(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
    }

        public Action getAction() {
            return switch (this.level) {
                case 'S' -> new Level_S();
                case 'A' -> new Level_A();
                case 'B' -> new Level_B();
                default -> new Level_C();
            };
        }
    }

    // Role-based Implementations
    class Level_S implements Card.Action {
        @Override
        public void editCard(Card card, Card loggedInUser) {
            JTextField nameField = new JTextField(card.getName());
            JTextField positionField = new JTextField(card.getPosition());
            String[] levels = {"S", "A", "B", "C"};
            JComboBox<String> levelBox = new JComboBox<>(levels);
            levelBox.setSelectedItem(String.valueOf(card.getLevel()));
            JPasswordField passwordField = new JPasswordField(card.getPassword());

            Object[] message = {
                    "Name:", nameField,
                    "Position:", positionField,
                    "Level:", levelBox,
                    "Password:", passwordField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Edit Card", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                card.setCard(nameField.getText().trim(), positionField.getText().trim(),
                        ((String) levelBox.getSelectedItem()).charAt(0), new String(passwordField.getPassword()));
            }
        }

        @Override
        public void removeCard(Card card, Card loggedInUser) {

            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this card?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Card deleted successfully.");
            }
        }
    }

    class Level_A implements Card.Action {
        @Override
        public void editCard(Card card, Card loggedInUser) {
            if (loggedInUser.getLevel() == 'S'){
                JTextField nameField = new JTextField(card.getName());
                JTextField positionField = new JTextField(card.getPosition());
                String[] levels = {"S", "A", "B", "C"};
                JComboBox<String> levelBox = new JComboBox<>(levels);
                levelBox.setSelectedItem(String.valueOf(card.getLevel()));
                JPasswordField passwordField = new JPasswordField(card.getPassword());

                Object[] message = {
                        "Name:", nameField,
                        "Position:", positionField,
                        "Level:", levelBox,
                        "Password:", passwordField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Edit Card", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(nameField.getText().trim(), positionField.getText().trim(),
                            ((String) levelBox.getSelectedItem()).charAt(0), new String(passwordField.getPassword()));
                }
            }
            if (loggedInUser.getLevel() != 'A') {
                JOptionPane.showMessageDialog(null, "You do not have permission to edit this card.");
                return;
            }

            if (card.getId().equals(loggedInUser.getId())) {
                JPasswordField passwordField = new JPasswordField();
                Object[] message = {"New Password:", passwordField};

                int option = JOptionPane.showConfirmDialog(null, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(card.getName(), card.getPosition(), card.getLevel(), new String(passwordField.getPassword()));
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "You cannot edit this card.");
            }
        }

        @Override
        public void removeCard(Card card, Card loggedInUser) {
            if (card.getId().equals(loggedInUser.getId())) {
                JOptionPane.showMessageDialog(null, "Card deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "You cannot delete other users' cards.");
            }
        }
    }

    class Level_B implements Card.Action {
        @Override
        public void editCard(Card card, Card loggedInUser) {
            if (loggedInUser.getLevel() == 'S') {
                JTextField nameField = new JTextField(card.getName());
                JTextField positionField = new JTextField(card.getPosition());
                String[] levels = {"S", "A", "B", "C"};
                JComboBox<String> levelBox = new JComboBox<>(levels);
                levelBox.setSelectedItem(String.valueOf(card.getLevel()));
                JPasswordField passwordField = new JPasswordField(card.getPassword());

                Object[] message = {
                        "Name:", nameField,
                        "Position:", positionField,
                        "Level:", levelBox,
                        "Password:", passwordField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Edit Card", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(nameField.getText().trim(), positionField.getText().trim(),
                            ((String) levelBox.getSelectedItem()).charAt(0), new String(passwordField.getPassword()));
                }
            } else if (loggedInUser.getLevel() == 'A') {
                JTextField positionField = new JTextField(card.getPosition());
                Object[] message = {"New Position:", positionField};
                int option = JOptionPane.showConfirmDialog(null, message, "Edit Position", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(card.getName(), positionField.getText().trim(), card.getLevel(), card.getPassword());
                }
                if (!card.getId().equals(loggedInUser.getId())) {
                    JOptionPane.showMessageDialog(null, "You can only edit your own password.");
                    return;
                }

                JPasswordField passwordField = new JPasswordField();
                Object[] Bmessage = {"New Password:", passwordField};

                option = JOptionPane.showConfirmDialog(null, Bmessage, "Change Password", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(card.getName(), card.getPosition(), card.getLevel(), new String(passwordField.getPassword()));
                }
            }
        }

        @Override
        public void removeCard(Card card, Card loggedInUser) {
            if (card.getId().equals(loggedInUser.getId())) {
                JOptionPane.showMessageDialog(null, "Card deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "You cannot delete other users' cards.");
            }
        }
    }

    class Level_C implements Card.Action {
        @Override
        public void editCard(Card card, Card loggedInUser) {
            if (loggedInUser.getLevel() == 'S') {
                JTextField nameField = new JTextField(card.getName());
                JTextField positionField = new JTextField(card.getPosition());
                String[] levels = {"S", "A", "B", "C"};
                JComboBox<String> levelBox = new JComboBox<>(levels);
                levelBox.setSelectedItem(String.valueOf(card.getLevel()));
                JPasswordField passwordField = new JPasswordField(card.getPassword());

                Object[] message = {
                        "Name:", nameField,
                        "Position:", positionField,
                        "Level:", levelBox,
                        "Password:", passwordField
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Edit Card", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(nameField.getText().trim(), positionField.getText().trim(),
                            ((String) levelBox.getSelectedItem()).charAt(0), new String(passwordField.getPassword()));
                }
            } else if (loggedInUser.getLevel() == 'A') {
                JTextField positionField = new JTextField(card.getPosition());
                Object[] message = {"New Position:", positionField};
                int option = JOptionPane.showConfirmDialog(null, message, "Edit Position", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(card.getName(), positionField.getText().trim(), card.getLevel(), card.getPassword());
                }
                if (!card.getId().equals(loggedInUser.getId())) {
                    JOptionPane.showMessageDialog(null, "You can only edit your own password.");
                    return;
                }

                JPasswordField passwordField = new JPasswordField();
                Object[] Cmessage = {"New Password:", passwordField};

                option = JOptionPane.showConfirmDialog(null, Cmessage, "Change Password", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    card.setCard(card.getName(), card.getPosition(), card.getLevel(), new String(passwordField.getPassword()));
                }
            }
        }

        @Override
        public void removeCard(Card card, Card loggedInUser) {
            if (card.getId().equals(loggedInUser.getId())) {
                JOptionPane.showMessageDialog(null, "Card deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "You cannot delete other users' cards.");
            }
        }
    }
