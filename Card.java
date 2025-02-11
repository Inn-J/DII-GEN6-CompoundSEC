import java.util.ArrayList;
import java.util.Scanner;

class Card {
    private String name;
    private final String id;
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

    public String getId() {
        return id;
    }

    public void updateCard(String name, String position, char level, String password) {
        this.name = name;
        this.position = position;
        this.level = level;
        this.password = password;
    }

    public void displayCard() {
        System.out.println("\nCard");
        System.out.println("Name: " + name);
        System.out.println("ID: " + id);
        System.out.println("Position: " + position);
        System.out.println("Level: " + level);
        System.out.println("----------------------");
    }

    public static void addCard(ArrayList<Card> cards, Scanner input) {
        System.out.print("Enter Name: ");
        String name = input.nextLine();
        System.out.print("Enter ID: ");
        String id = input.nextLine();
        System.out.print("Enter Position: ");
        String position = input.nextLine();
        System.out.print("Enter Level: ");
        char level = input.next().charAt(0);
        System.out.print("Enter Password: ");
        String password = input.nextLine();
        cards.add(new Card(name, id, position, level, password));
        System.out.println("Card added successfully!");
    }

    public static void editCard(ArrayList<Card> cards, Scanner scanner) {
        System.out.print("Enter ID of the card to edit: ");
        String id = scanner.nextLine();
        for (Card card : cards) {
            if (card.getId().equals(id)) {
                System.out.print("Enter New Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter New Position: ");
                String position = scanner.nextLine();
                System.out.print("Enter New Level: ");
                char level = scanner.next().charAt(0);
                System.out.print("Enter New Password: ");
                String password = scanner.nextLine();
                card.updateCard(name, position, level, password);
                System.out.println("Card updated successfully!");
                return;
            }
        }
        System.out.println("Card not found.");
    }

    public static void removeCard(ArrayList<Card> cards, Scanner scanner) {
        System.out.print("Enter ID of the card to remove: ");
        String id = scanner.nextLine();
        for (Card card : cards) {
            if (card.getId().equals(id)) {
                cards.remove(card);
                System.out.println("Card removed successfully!");
                return;
            }
        }
        System.out.println("Card not found.");
    }

    public static void displayCard(ArrayList<Card> cards, Scanner scanner) {
        System.out.print("Enter ID of the card to display: ");
        String id = scanner.nextLine();
        for (Card card : cards) {
            if (card.getId().equals(id)) {
                card.displayCard();
                return;
            }
        }
        System.out.println("Card not found.");
    }
    class Class_S extends Card {
        public Class_S(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
}
    class Class_A extends Card {
        public Class_A(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
    }
    class Class_B extends Card {
        public Class_B(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
    }
    class Class_C extends Card {
        public Class_C(String name, String id, String position, char level, String password) {
            super(name, id, position, level, password);
        }
    }
}