package pt.ua.deti.cbd.lab1.jedisExercise;

import java.util.Set;
import redis.clients.jedis.Jedis;

public class SimplePost {
    private Jedis jedis;
    public static String USERS = "users"; // Key set for users' name

    public SimplePost() {
        this.jedis = new Jedis();
    }
    public void saveUser(String username) {
        jedis.sadd(USERS, username);
    }
    public Set<String> getUser() {
        return jedis.smembers(USERS);
    }
    public Set<String> getAllKeys() {
        return jedis.keys("*");
    }
    public static void main(String[] args) {
        SimplePost board = new SimplePost();
        // set some users
        String[] users = { "Ana", "Pedro", "Maria", "Luis" };
        for (String user: users)
            board.saveUser(user);
            
        for (String key: board.getAllKeys())
            System.out.println(key);
        for (String user: board.getUser())
            System.out.println(user);
        
        // board.getAllKeys().stream().forEach(System.out::println);
        // board.getUser().stream().forEach(System.out::println);
    }
}