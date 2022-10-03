package pt.ua.deti.cbd.lab1.messageSystem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        UserDB db = new UserDB();
        Scanner sc = new Scanner(System.in);
        int input = -1;

        while (input != 3) {
            System.out.println("Choose a option (1-3):");
            System.out.println("1. Add a new user");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            input = sc.nextInt(); sc.nextLine();

            if (input == 1) {
                String username = "";
                System.out.println("Insert your username:");
                username = sc.nextLine().toLowerCase();

                if (username.isBlank()) 
                    continue;

                if (db.saveUser(username))
                    System.out.println(username +": User was added successfully!");
                else
                    System.out.println("User already exists!");
            }
            else if (input == 2) {
                String username = "";
                System.out.println("Insert your username:");
                username = sc.nextLine().toLowerCase();

                if (username.isEmpty()) 
                    continue;

                if (db.getUser(username)) {

                    while (input != 3) {

                        System.out.println("Choose a option (1-3):");
                        System.out.println("0. Follow another user");
                        System.out.println("1. Get messages");
                        System.out.println("2. Send message");
                        System.out.println("3. Logout");

                        input = sc.nextInt(); sc.nextLine();
                        
                        if (input == 0) {
                            String otherUser = "";
                            System.out.println("Insert the user you want to follow:");
                            otherUser = sc.nextLine().toLowerCase();
    
                            if (otherUser.isBlank()) 
                                continue;
                            
                            if (db.followUser(username, otherUser))
                                System.out.println("Successfully followed!");
                            else 
                                System.out.println("User doesn't exist!");  
                        }
                        else if (input == 1) {
                            System.out.println(db.getMessages(username));
                        }
                        else if (input == 2) {
                            System.out.println("Write your message:");
                            String message = sc.nextLine();
                            db.storeMessage(username, message);
    
                            System.out.println("Your message was sent!");
                        }
                    }
                    
                    // Logout
                    input = -1;
                }
                else {
                    System.out.println("User doesn't exist!");
                }
            }
        }

        sc.close();
        db.close();
    }
}
