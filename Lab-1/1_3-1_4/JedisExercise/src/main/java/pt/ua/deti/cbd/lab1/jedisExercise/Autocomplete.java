package pt.ua.deti.cbd.lab1.jedisExercise;

import redis.clients.jedis.Jedis;

import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Autocomplete {
    private Jedis jedis;
    public static String USERS = "users"; // Key set for users' name

    public Autocomplete() {
        this.jedis = new Jedis();
    }

    public void saveUser(String username) {
        jedis.sadd(USERS, username);
    }

    public Set<String> getUsers() {
        return jedis.smembers(USERS);
    }

    public Set<String> getAllKeys() {
        return jedis.keys("*");
    }

    public void close() {
        jedis.flushAll();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Autocomplete ac = new Autocomplete();
        Scanner sc = new Scanner(
                new File("C:/Users/DanielaDias/Documents/Uni/CBD/Lab-1/1_3-1_4/JedisExercise/names.txt"));

        while (sc.hasNextLine()) {
            ac.saveUser(sc.nextLine());
        }

        sc.close();
        sc = new Scanner(System.in);

        String input = "";
        System.out.println("Search for ('Enter' for quit):");

        while (sc.hasNextLine()) {

            input = sc.nextLine().toLowerCase();

            // End search
            if (input.isBlank())
                break;

            // Print users that start with the given input
            for (String key : ac.getUsers()) {
                if (key.toLowerCase().startsWith(input)) {
                    System.out.println(key);
                }
            }
            System.out.println("Search for ('Enter' for quit):");
        }

        sc.close();
        ac.close();
    }
}

