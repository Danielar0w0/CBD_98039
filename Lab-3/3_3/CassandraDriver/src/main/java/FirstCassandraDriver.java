import com.datastax.driver.core.*;

public class FirstCassandraDriver {

    private Cluster cluster;
    private Session session;

    public FirstCassandraDriver() {
        this.cluster = Cluster.builder()
                .withClusterName("myFirstCluster")
                .addContactPoint("127.0.0.1")
                .withPort(9042)
                .build();

        // Default keyspace is set
        this.session = this.cluster.connect("keyspace_videos");
    }

    public Session getSession() {
        return session;
    }

    public static void main(String[] args) {

        FirstCassandraDriver driver = null;

        try {
            driver = new FirstCassandraDriver();

            ResultSet res = driver.getSession().execute("SELECT * FROM users;");
            for (Row row: res) {
                System.out.println(row.getString("email"));
            }
            System.out.println();

            /*
            driver.getSession().execute("INSERT INTO users (username, name, email, stamp) " +
                    "VALUES ('Cassandra', 'Cassandra Driver', 'cassandra@ua.pt', toTimeStamp(now()));");

            System.out.println("Newly added:");
            res = driver.getSession().execute("SELECT * FROM users WHERE email = 'cassandra@ua.pt';");
            System.out.println(res.one().getString("email"));

            driver.getSession().execute("UPDATE users SET username = 'Cassandra Driver' WHERE email = 'cassandra@ua.pt';");

            System.out.println("Newly updated:");
            res = driver.getSession().execute("SELECT * FROM users WHERE email = 'cassandra@ua.pt';");
            System.out.println(res.one());
            */

            // b)
            driver.firstThreeComments("Video 1");
            driver.allTags("Video 1");
            driver.avgRating("Video Today");
            driver.numberOfRatings("Video Today");
            driver.followers("XPTO");

        } finally {
            driver.close();
        }
    }

    public void close() {
        if (cluster != null) cluster.close();
    }

    public void firstThreeComments(String video_name) {

        ResultSet res = session.execute("SELECT * FROM commentsbyvideo WHERE video_name = '" + video_name + "' LIMIT 3;");
        System.out.println("First three comments (" + video_name + "):");
        for (Row row: res) {
            System.out.println(row.getString("author_email") + " - " + row.getTimestamp("stamp"));
        }
        System.out.println();
    }

    public void allTags(String video_name) {
        ResultSet res = session.execute("SELECT * FROM videos WHERE video_name = '" + video_name + "'");
        Row row = res.one();
        System.out.println("Tags (" + video_name + "):");
        for (String tag: row.getList("tag", String.class)) {
            System.out.println(tag);
        }
        System.out.println();
    }

    public void avgRating(String video_name) {
        ResultSet res = session.execute("SELECT AVG(rating) FROM ratingsbyvideo WHERE video_name = '" + video_name + "'");
        Row row = res.one();
        System.out.println("Average Rating (" + video_name + "):");
        System.out.println(row.get(0, TypeCodec.smallInt()));
        System.out.println();
    }

    public void numberOfRatings(String video_name) {
        ResultSet res = session.execute("SELECT COUNT(*) FROM ratingsbyvideo WHERE video_name = '" + video_name + "'");
        Row row = res.one();
        System.out.println("Number of Ratings (" + video_name + "):");
        System.out.println(row.get(0, TypeCodec.bigint()));
        System.out.println();
    }

    public void followers(String video_name) {
        ResultSet res = session.execute("SELECT * FROM followers WHERE video_name = '" + video_name + "'");
        Row row = res.one();
        System.out.println("Followers (" + video_name + "):");
        for (String user: row.getList("users_email", String.class)) {
            System.out.println(user);
        }
        System.out.println();
    }
}
