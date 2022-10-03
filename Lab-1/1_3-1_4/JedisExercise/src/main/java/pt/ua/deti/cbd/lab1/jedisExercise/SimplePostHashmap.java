package pt.ua.deti.cbd.lab1.jedisExercise;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class SimplePostHashmap {
    private Jedis jedis;
    public static String USERS = "users"; // Key set for users' name

    public SimplePostHashmap() {
        this.jedis = new Jedis();
    }
    public void saveUser(String username) {
        jedis.hset(USERS, username, "My name is" + username);
    }
    public Set<String> getUser() {
        return jedis.hkeys(USERS);
    }
    public Set<String> getAllKeys() {
        return jedis.keys("*");
    }
    public void close() {
        jedis.flushAll();
    }

    public static void main(String[] args) {
        SimplePostHashmap board = new SimplePostHashmap();
        // set some users
        String[] users = { "Ana", "Pedro", "Maria", "Luis" };
        for (String user: users)
            board.saveUser(user);
            
        for (String key: board.getAllKeys())
            System.out.println(key);
        for (String user: board.getUser())
            System.out.println(user);

        board.close();
    }
}