package pt.ua.deti.cbd.lab1.jedisExercise;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Set;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Autocomplete2 {
    private Jedis jedis;
    public static String USERS = "users2"; // Key set for users' name

    public Autocomplete2() {
        this.jedis = new Jedis();
    }

    public void saveUser(String input) {
        // Ignore invalid input
        if (input.split(";").length != 2) {
            System.err.println("Invalid format! Continuing...");
            // System.exit(-1);
        }
        else {
            // Add user to sorted set
            jedis.zadd(USERS, Double.parseDouble(input.split(";")[1]), input.split(";")[0]);
        }
    }

    // public Set<String> getUsers() {
    //     return jedis.zrevrangeByScore(USERS, size, 0);
    // }

    public Set<Tuple> getUsersWithScores() {
        return jedis.zrevrangeByScoreWithScores(USERS, 1000, 0);
    }

    public Set<String> getAllKeys() {
        return jedis.keys("*");
    }

    public void close() {
        jedis.flushAll();
    }


    public static void main(String[] args) throws FileNotFoundException {
        Autocomplete2 ac = new Autocomplete2();
        Scanner sc = new Scanner(new File("C:/Users/DanielaDias/Documents/Uni/CBD/Lab-1/1_3-1_4/JedisExercise/nomes-pt-2021.csv"));
        
        while (sc.hasNextLine()) {
            ac.saveUser(sc.nextLine());
        }

        sc.close(); sc = new Scanner(System.in);
        
        System.out.println("Search for ('Enter' for quit):");

        while (sc.hasNextLine()) {

            final String input = sc.nextLine().toLowerCase();

            // End search
            if (input.isBlank()) break;

            // Print users that start with the given input
            ac.getUsersWithScores().stream().filter(tuple -> (tuple.getElement().toLowerCase().startsWith(input))).forEach(
                tuple -> System.out.println(tuple.getElement() + " - " + tuple.getScore()));

            System.out.println("Search for ('Enter' for quit):");
        }

        sc.close();
        ac.close();
    }
}
