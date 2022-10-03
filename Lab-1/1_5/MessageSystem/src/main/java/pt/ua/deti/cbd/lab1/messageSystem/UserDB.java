package pt.ua.deti.cbd.lab1.messageSystem;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class UserDB {

    private Jedis jedis;
    public static String USERS = "users"; // Key set for users' name

    public UserDB() {
        this.jedis = new Jedis();
    }

    public boolean saveUser(String username) {
        if (jedis.smembers(USERS).contains(username))
            return false;

        jedis.sadd(USERS, username);
        return true;
    }

    public boolean getUser(String username) {
        Set<String> users = getUsers();
        if (users.contains(username))
            return true;
        return false;
    }

    public Set<String> getUsers() {
        return jedis.smembers(USERS);
    }

    public boolean followUser(String currentUser, String otherUser) {
        if (!getUser(otherUser))
            return false;

        jedis.sadd(currentUser+":following", otherUser);
        return true;
    }

    public void storeMessage(String currentUser, String message) {
        jedis.sadd(currentUser+":messages", message);
    }

    public String getMessages(String currentUser) {
        currentUser = currentUser + ":following";
        if (!jedis.exists(currentUser))
            return "No new messages!";

        Set<String> usersFollowing = jedis.smembers(currentUser);
        String allMessages = "";

        for (String user: usersFollowing) {
            user = user + ":messages";
            if (jedis.exists(user)) {
                for (String message: jedis.smembers(user)) {
                    allMessages += (message + "\n");
                }
            }
        }

        if (allMessages.isEmpty())
            return "No new messages!";

        return allMessages;
    }

    public void close() {
        jedis.flushAll();
    }
}
