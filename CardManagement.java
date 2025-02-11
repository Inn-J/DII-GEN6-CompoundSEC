import java.util.Scanner;
import java.util.ArrayList;
public class CardManagement {
    private static ArrayList<Card> cards = new ArrayList<>();
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nCard Management System");
            System.out.println("1. Add Card");
            System.out.println("2. Display Card");
            System.out.println("3. Edit Card");
            System.out.println("4. Remove Card");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    Card.addCard(cards, input);
                    break;
                case 2:
                    Card.displayCard(cards, input);
                    break;
                case 3:
                    Card.editCard(cards, input);
                    break;
                case 4:
                    Card.removeCard(cards, input);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}

