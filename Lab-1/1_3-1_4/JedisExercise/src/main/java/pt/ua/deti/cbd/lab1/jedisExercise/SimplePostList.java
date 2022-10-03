package pt.ua.deti.cbd.lab1.jedisExercise;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class SimplePostList {
    private Jedis jedis;
    public static String USERS = "users"; // Key set for users' name

    public SimplePostList() {
        this.jedis = new Jedis();
    }
    public void saveUser(String username) {
        jedis.rpush(USERS, username);
    }
    public List<String> getUser() {
        return jedis.lrange(USERS, 0, -1);
    }
    public Set<String> getAllKeys() {
        return jedis.keys("*");
    }
    public static void main(String[] args) {
        SimplePostList board = new SimplePostList();
        // set some users
        String[] users = { "Ana", "Pedro", "Maria", "Luis" };
        for (String user: users)
            board.saveUser(user);
            
        for (String key: board.getAllKeys())
            System.out.println(key);
        for (String user: board.getUser())
            System.out.println(user);
    }
}
