import com.datastax.driver.core.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PeopleCassandraDriver {

    private Cluster cluster;
    private Session session;

    public PeopleCassandraDriver() {
        this.cluster = Cluster.builder()
                .withClusterName("PeopleCluster")
                .addContactPoint("127.0.0.1")
                .withPort(9042)
                .build();

        // Default keyspace is set
        this.session = this.cluster.connect();
    }

    public static void main(String[] args) {
        PeopleCassandraDriver driver = new PeopleCassandraDriver();

        // a)
        driver.setUpKeySpace();

        // b) + c)
        // driver.insertData();
        // driver.follow("karenwarner@pharmex.com", "austinbradley@pharmex.com");

        // d)
        // driver.createSecondIndexes();

        // e)
        // Update
        // driver.updateMembers("Random Group", "lucillebrennan@pharmex.com", "['sbornmassey@pharmex.com']");
        // driver.updateFirstMember("Random Group", "lucillebrennan@pharmex.com", "karenwarner@pharmex.com");
        // driver.addAMember("Random Group", "lucillebrennan@pharmex.com", "alexandercamacho@pharmex.com");
        // driver.addATag("cassandrapollard@pharmex.com", "Leaving today", "Future");
        // driver.updateCategories("Gardening tips", "isabellivingston@pharmex.com", "{'Nature': 'Home', 'Earth': 'Flower'}");
        // driver.addACategory("Gardening tips", "isabellivingston@pharmex.com", "Garden", "Planting");

        // Delete
        // driver.undoFollow("karenwarner@pharmex.com", "alexandercamacho@pharmex.com");
        // driver.deleteTags("isabellivingston@pharmex.com", "Cassandra DML");
        // driver.deleteTagEqualTo("joycecarey@pharmex.com", "Studying Math", "Math");
        // driver.deleteAMember("Random Group", "lucillebrennan@pharmex.com", "alexandercamacho@pharmex.com");
        // driver.deleteMembers("Random Group", "lucillebrennan@pharmex.com");
        // driver.deleteCategories("Gardening tips", "isabellivingston@pharmex.com");

        // f)
        // driver.obtainAllUsers();
        // driver.obtainAllPosts();
        // driver.obtainAllFollows();
        // driver.postsByUser("cassandrapollard@pharmex.com");
        // driver.userFollowers("osbornmassey@pharmex.com");
        // driver.findUserByName("Lewis French");
        // driver.containsCategory("Math");
        // driver.numberOfUsers();
        // driver.groupContainsMember("Math Group", "joycecarey@pharmex.com", "cassandrapollard@pharmex.com");
        // driver.oldestUserAge();
        // driver.youngestUserAge();
        // driver.lastFollowTimestamp();
    }

    // d) Second Indexes
    public void createSecondIndexes() {

        /*
        session.execute("DROP INDEX IF EXISTS user_posts");
        session.execute("DROP INDEX IF EXISTS user_followers");
        session.execute("DROP INDEX IF EXISTS name");
        session.execute("DROP INDEX IF EXISTS group_categories");
         */
        session.execute("CREATE INDEX IF NOT EXISTS user_posts ON posts (email)");
        session.execute("CREATE INDEX IF NOT EXISTS user_followers ON following (following_email)");
        session.execute("CREATE INDEX IF NOT EXISTS user_name ON users (name)");
        session.execute("CREATE INDEX IF NOT EXISTS group_categories ON groups (KEYS(categories))");
        session.execute("CREATE INDEX IF NOT EXISTS group_members ON groups (members)");
    }

    // e) Update/Delete

    public void undoFollow(String user_email, String following_email) {
        ResultSet res = session.execute("SELECT * FROM following WHERE user_email = '" + user_email + "' AND following_email = '" + following_email + "'");
        Row row = res.one();
        System.out.println("--- Unfollowing:");
        System.out.println(row);

        session.execute("DELETE FROM following WHERE user_email = '" + user_email + "' AND following_email = '" + following_email + "'");
    }


    public void updateMembers(String name, String admin_email, String members) {
        session.execute("UPDATE groups SET members = " + members + " WHERE name = '" +  name + "' AND admin_email = '" + admin_email + "' IF EXISTS");
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        System.out.println("--- Added members " + members + ":");
        for (Row row: res)
            System.out.println(row);
    }

    public void updateFirstMember(String name, String admin_email, String member) {
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        Row row = res.one();
        if (row.getList("members", String.class).isEmpty())
            System.out.println("--- List of members is empty!");
        else {
            session.execute("UPDATE groups SET members[0] = '" + member + "' WHERE name = '" +  name + "' AND admin_email = '" + admin_email + "' IF EXISTS");
            res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
            System.out.println("--- Updated first member:");
            row = res.one();
            System.out.println(row);
        }
    }

    public void addAMember(String name, String admin_email, String member) {
        session.execute("UPDATE groups SET members = members + ['" + member + "'] WHERE name = '" +  name + "' AND admin_email = '" + admin_email + "' IF EXISTS");
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        System.out.println("--- Added member " + member + ":");
        for (Row row: res)
            System.out.println(row);
    }

    public void deleteAMember(String name, String admin_email, String member) {
        session.execute("UPDATE groups SET members = members - ['" + member + "'] WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        System.out.println("-- Deleted member " + member + ":");
        for (Row row: res)
            System.out.println(row);
    }

    public void deleteMembers(String name, String admin_email) {
        session.execute("UPDATE groups SET members = [] WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        System.out.println("-- Deleted members in:");
        for (Row row: res)
            System.out.println(row);
    }


    public void addATag(String email, String content, String tag) {
        session.execute("UPDATE posts SET tags = tags + {'" + tag + "'} WHERE email = '" + email + "' AND content = '" + content + "'");
        ResultSet res = session.execute("SELECT * FROM posts WHERE email = '" + email + "' AND content = '" + content + "'");
        System.out.println("-- Added tag " + tag + ":");
        for (Row row: res)
            System.out.println(row);
    }

    public void deleteATag(String email, String content, String tag) {
        session.execute("UPDATE posts SET tags = tags - {'" + tag + "'} WHERE email = '" + email + "' AND content = '" + content + "'");
        ResultSet res = session.execute("SELECT * FROM posts WHERE email = '" + email + "' AND content = '" + content + "'");
        System.out.println("-- Deleted first tag:");
        for (Row row: res)
            System.out.println(row);
    }

    public void deleteTags(String email, String content) {
        ResultSet res = session.execute("SELECT * FROM posts WHERE email = '" + email + "' AND content = '" + content + "'");
        System.out.println("-- Deleted tags in:");
        for (Row row: res)
            System.out.println(row);
        session.execute("UPDATE posts SET tags = {} WHERE email = '" + email + "' AND content = '" + content + "'");
    }


    public void updateCategories(String name, String admin_email, String categories) {
        session.execute("UPDATE groups SET categories = " + categories + " WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        System.out.println("-- Added categories " + categories + ":");
        for (Row row: res)
            System.out.println(row);
    }

    public void addACategory(String name, String admin_email, String category, String subcategory) {
        session.execute("UPDATE groups SET categories = categories + {'" + category + "': '" + subcategory + "'} WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        System.out.println("-- Added category {" + category + ": " + subcategory + "}:");
        for (Row row: res)
            System.out.println(row);
    }

    public void deleteCategories(String name, String admin_email) {
        session.execute("UPDATE groups SET categories = {} WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        ResultSet res = session.execute("SELECT * FROM groups WHERE name = '" + name + "' AND admin_email = '" + admin_email + "'");
        System.out.println("-- Deleted categories in:");
        for (Row row: res)
            System.out.println(row);
    }

    // f) Queries

    public void obtainAllUsers() {
        ResultSet res = session.execute("SELECT * FROM users");
        System.out.println("--- All Users:");
        for (Row row: res)
            System.out.println(row.getString("email"));
    }

    public void obtainAllPosts() {
        ResultSet res = session.execute("SELECT * FROM posts");
        System.out.println("--- All Posts:");
        for (Row row: res)
            System.out.println(row);
    }

    public void obtainAllFollows() {
        ResultSet res = session.execute("SELECT * FROM following");
        System.out.println("--- All Follows:");
        for (Row row: res)
            System.out.println(row);
    }

    public void postsByUser(String email) {
        ResultSet res = session.execute("SELECT * FROM posts WHERE email='" + email + "'");
        System.out.println("--- Posts (" + email + "):");
        for (Row row: res)
            System.out.println(row.getTimestamp("timestamp") + " - " + row.getString("content"));
    }

    public void userFollowers(String email) {
        ResultSet res = session.execute("SELECT * FROM following WHERE following_email = '" + email + "'");
        System.out.println("--- " + email + " followers:");
        for (Row row: res)
            System.out.println(row.getTimestamp("timestamp") + " - " + row.getString("user_email"));
    }

    public void findUserByName(String name) {
        ResultSet res = session.execute("SELECT * FROM users WHERE name = '" + name + "'");
        System.out.println("--- Users with name '" + name + "':");
        for (Row row: res)
            System.out.println(row.getString("email"));
    }

    public void containsCategory(String category) {
        ResultSet res = session.execute("SELECT * FROM groups WHERE categories CONTAINS KEY´'" +  category + "'");
        System.out.println("--- Groups that contain the category '" + category + "':");
        for (Row row: res)
            System.out.println(row.getString("name") + " - " + row.getString("admin_email"));
    }

    public void numberOfUsers() {
        ResultSet res = session.execute("SELECT COUNT(*) FROM users");
        System.out.println("--- Number of users:");
        Row row = res.one();
        System.out.println(row.get(0, TypeCodec.bigint()));
    }

    public void groupContainsMember(String name, String admin_email, String member) {
        ResultSet res = session.execute("SELECT COUNT(*) FROM groups WHERE name = '" + name + "' AND admin_email = '" +  admin_email + "' AND members CONTAINS '" + member + "'");
        System.out.println("--- Group contains member " + member + ":");
        Row row = res.one();
        if (row.get(0, TypeCodec.bigint()).intValue() == 0)
            System.out.println("False");
        else
            System.out.println("True");
    }

    public void oldestUserAge() {
        ResultSet res = session.execute("SELECT MAX(age) FROM users");
        System.out.println("--- Oldest user (age):");
        Row row = res.one();
        System.out.println(row.getInt(0));
    }

    public void youngestUserAge() {
        ResultSet res = session.execute("SELECT MIN(age) FROM users");
        System.out.println("--- Youngest user (age):");
        Row row = res.one();
        System.out.println(row.getInt(0));
    }

    public void lastFollowTimestamp() {
        ResultSet res = session.execute("SELECT MAX(timestamp) FROM following");
        System.out.println("--- Last follow timestamp:");
        Row row = res.one();
        System.out.println(row.getTimestamp(0));
    }

    // a) Keyspace with 4 tables

    public void setUpKeySpace() {
        session.execute("CREATE KEYSPACE IF NOT EXISTS keyspace_people WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };");
        session.execute("USE keyspace_people");

        /*
        session.execute("DROP TABLE IF EXISTS keyspace_people.users");
        session.execute("DROP TABLE IF EXISTS keyspace_people.posts");
        session.execute("DROP TABLE IF EXISTS keyspace_people.following");
        session.execute("DROP TABLE IF EXISTS keyspace_people.groups");
         */

        session.execute("CREATE TABLE IF NOT EXISTS keyspace_people.users (" +
                "name TEXT," +
                "email TEXT PRIMARY KEY," +
                "age INT," +
                "gender TEXT)");

        session.execute("CREATE TABLE IF NOT EXISTS keyspace_people.posts (" +
                "email TEXT," +
                "content TEXT," +
                "tags SET<TEXT>," +
                "timestamp TIMESTAMP," +
                "PRIMARY KEY ( (email, content) ) )");

        session.execute("CREATE TABLE IF NOT EXISTS keyspace_people.following (" +
                "user_email TEXT," +
                "following_email TEXT," +
                "timestamp TIMESTAMP," +
                "PRIMARY KEY ( user_email, following_email ) )");

        session.execute("CREATE TABLE IF NOT EXISTS keyspace_people.groups (" +
                "name TEXT," +
                "admin_email TEXT," +
                "categories MAP<TEXT, TEXT>," +
                "members LIST<TEXT>," +
                "PRIMARY KEY ( (name, admin_email) ) )");
    }

    // b) Insert data + c) Set, list, map

    public void insertData() {

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("people.json")) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray peopleList = (JSONArray) obj;

            for (Object userObject: peopleList) {
                JSONObject user = (JSONObject) userObject;

                session.execute("INSERT INTO users (name, email, age, gender) VALUES ('" +
                    user.get("name") + "', '" + user.get("email") + "', " + user.get("age") + ", '" + user.get("gender") + "')"
                );
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Insert into posts
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('adawise@pharmex.com', 'Hello World!', {'Random', 'Really Random'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('adawise@pharmex.com', 'My second post!', {'Random', 'Really Random'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('alexandercamacho@pharmex.com', 'Cheesecake recipe tomorrow', {'Recipe', 'Bakery'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('harveymitchell@pharmex.com', 'Cassandra tutorial', {'Cassandra', 'Tutorial'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('isabellivingston@pharmex.com', 'Cassandra DML', {'Cassandra'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('penningtonwaller@pharmex.com', 'Cassandra DDL Question', {'Cassandra', 'Question'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('penningtonwaller@pharmex.com', 'Gardening flowers', {'Gardening', 'Nature'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('cassandrapollard@pharmex.com', 'Leaving today', {'News', 'Today', 'Bye'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('cassandrapollard@pharmex.com', 'When will I be back', {'News', 'Future'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('joycecarey@pharmex.com', 'Studying Math', {'Math', 'Student', 'Life'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('joycecarey@pharmex.com', 'Math expert', {'Math', 'Expert', 'Life'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('lewisfrench@pharmex.com', 'Going for a jog', {'Exercise', 'Life'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('lewisfrench@pharmex.com', 'Watching Netflix', {'Netflix', 'Entertainment'}, toTimeStamp(now()))");
        session.execute("INSERT INTO posts (email, content, tags, timestamp) " +
                "VALUES ('cassandrapollard@pharmex.com', 'Hello everyone!', {'Random', 'Really Random'}, toTimeStamp(now()))");

        // Insert into following
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('adawise@pharmex.com', 'alexandercamacho@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('alexandercamacho@pharmex.com', 'adawise@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('karenwarner@pharmex.com', 'alexandercamacho@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('karenwarner@pharmex.com', 'isabellivingston@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('karenwarner@pharmex.com', 'osbornmassey@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('adawise@pharmex.com', 'lewisfrench@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('lewisfrench@pharmex.com', 'osbornmassey@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('osbornmassey@pharmex.com', 'isabellivingston@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('francisfields@pharmex.com', 'osbornmassey@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('penningtonwaller@pharmex.com', 'lewisfrench@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('penningtonwaller@pharmex.com', 'osbornmassey@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('cassandrapollard@pharmex.com', 'isabellivingston@pharmex.com', toTimeStamp(now()))");
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('francisfields@pharmex.com', 'cassandrapollard@pharmex.com', toTimeStamp(now()))");

        // Insert into groups
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Math Group', 'joycecarey@pharmex.com', {'Math':'Algebra', 'Math':'Geometry'}, ['cassandrapollard@pharmex.com', 'francisfields@pharmex.com'])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Study Group', 'joycecarey@pharmex.com', {'Math':'Algebra', 'Science':'Physics'}, ['francisfields@pharmex.com'])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Netflix Recommendations', 'osbornmassey@pharmex.com', {'Netflix':'Movies', 'Netflix':'Series'}, ['harveymitchell@pharmex.com', 'karenwarner@pharmex.com'])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Anime Recommendations', 'osbornmassey@pharmex.com', {'Anime':'Romance', 'Anime':'Comedy'}, ['harveymitchell@pharmex.com', 'lewisfrench@pharmex.com'])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Cassandra Discussion Group', 'penningtonwaller@pharmex.com', {'Cassandra':'DML', 'Cassandra':'DDL'}, ['isabellivingston@pharmex.com', 'harveymitchell@pharmex.com'])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Gardening tips', 'isabellivingston@pharmex.com', {'Gardening':'Flowers', 'Nature':'Flowers'}, ['cassandrapollard@pharmex.com', 'penningtonwaller@pharmex.com'])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Random Group', 'lucillebrennan@pharmex.com', {'Random':'Random'}, [])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Cooking Group', 'cassandrapollard@pharmex.com', {'Cooking':'Bakery', 'Bakery':'Cake'}, ['isabellivingston@pharmex.com', 'marciajacobs@pharmex.com'])");
        session.execute("INSERT INTO groups (name, admin_email, categories, members) " +
                "VALUES ('Book lovers', 'marciajacobs@pharmex.com', {'Hobbie':'Reading', 'Place':'Bookstore'}, ['penningtonwaller@pharmex.com'])");
    }

    public void follow(String user_email, String following_email) {
        session.execute("INSERT INTO following (user_email, following_email, timestamp) " +
                "VALUES ('" + user_email + "', '" + following_email + "', toTimeStamp(now()))");
    }
}
