package pt.ua.deti.cbd.lab1.jedisExercise;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

public class Forum {
    private Jedis jedis;

    public static String LETTERS = "letters";

    public Forum() {
        this.jedis = new Jedis();
        // System.out.println(jedis.info());
    }

    public void setCount(String letter, int count) {
        jedis.zadd(LETTERS, count, letter);
    }

    public Set<Tuple> getLettersWithCount() {
        return jedis.zrevrangeByScoreWithScores(LETTERS, 1000, 0);
    }
    public static void main(String[] args) throws FileNotFoundException {
        Forum forum = new Forum();

        Scanner sc = new Scanner(new File("C:/Users/DanielaDias/Documents/Uni/CBD/Lab-1/1_3-1_4/JedisExercise/names.txt"));
        Map<String, Integer> allCount = new HashMap<String, Integer>();

        String letter = "";
        while (sc.hasNextLine()) {
            letter = Character.toString(sc.nextLine().charAt(0));

            Integer count = allCount.get(letter);
            if (count != null)
                allCount.put(letter, count + 1);
            else
                allCount.put(letter, 1);
        }

        for (String l: allCount.keySet())
            forum.setCount(l, allCount.get(l));

        forum.getLettersWithCount().stream().forEach(tuple -> System.out.println(tuple.getElement() + " - " + tuple.getScore()));
        sc.close();
    }
}
