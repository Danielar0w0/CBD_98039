import com.datastax.driver.core.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class RestaurantsCassandraDriver {

    private Cluster cluster;
    private Session session;

    public RestaurantsCassandraDriver() {
        this.cluster = Cluster.builder()
                .withClusterName("PeopleCluster")
                .addContactPoint("127.0.0.1")
                .withPort(9042)
                .build();

        // Default keyspace is set
        this.session = this.cluster.connect();
    }

    public static void main(String[] args) {
        RestaurantsCassandraDriver driver = new RestaurantsCassandraDriver();

        driver.setUpKeySpace();
        // driver.insertData();

        // c)

        // driver.query1();
        // driver.query2();
        // driver.query3();
        // driver.query4();
        // driver.query5();
        // driver.query7();
        // driver.query8();
        // driver.query12();
        // driver.query14();
        // driver.query15();
        // driver.query17();
        // driver.query19();
        // driver.query27();
        // driver.query30();
        // driver.numberOfGastronomiasPerLocalidade();
    }

    // a)

    public void setUpKeySpace() {

        // session.execute("DROP KEYSPACE IF EXISTS keyspace_restaurants");

        session.execute("CREATE KEYSPACE IF NOT EXISTS keyspace_restaurants WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };");
        session.execute("USE keyspace_restaurants");

        session.execute("CREATE TYPE IF NOT EXISTS keyspace_restaurants.address_details (" +
                "building TEXT, " +
                "coord LIST<DOUBLE>," +
                "rua TEXT," +
                "zipcode TEXT)");

        session.execute("CREATE TYPE IF NOT EXISTS keyspace_restaurants.grades_details (" +
                "date MAP<TEXT, BIGINT>," +
                "grade TEXT," +
                "score INT)"
                );

        session.execute("CREATE TABLE IF NOT EXISTS keyspace_restaurants.restaurants (" +
                "address frozen <address_details>," +
                "localidade TEXT," +
                "gastronomia TEXT," +
                "grades LIST<frozen <grades_details>>," +
                "nome TEXT," +
                "restaurant_id TEXT," +
                "PRIMARY KEY (restaurant_id, nome))"
        );
    }

    // b)

    public void insertData() {

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader fileReader = new FileReader("restaurants.json")) {
            //Read JSON file

            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();

            Object obj; JSONObject restaurant;

            while (line != null) {

                line = line.replace("'", "`");
                obj = jsonParser.parse(line);
                restaurant = (JSONObject) obj;

                // System.out.println(restaurant);
                session.execute("INSERT INTO restaurants JSON '" + restaurant + "';");

                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // c)

    public void query1() {
        ResultSet res = session.execute("SELECT JSON * FROM restaurants");
        System.out.println("--- 1. Liste todos os documentos da coleção.");
        for (Row row: res)
            System.out.println(row.getString("[json]"));
    }

    public void query2() {
        ResultSet res = session.execute("SELECT JSON restaurant_id, nome, localidade, gastronomia FROM restaurants");
        System.out.println("--- 2. Apresente os campos restaurant_id, nome, localidade e gastronomia para todos os " +
                "documentos da coleção.");
        for (Row row: res)
            System.out.println(row.getString("[json]"));
    }

    public void query3() {
        ResultSet res = session.execute("SELECT JSON restaurant_id, nome, localidade, address.zipcode AS zipcode FROM restaurants");
        System.out.println("--- 3. Apresente os campos restaurant_id, nome, localidade e código postal (zipcode), mas " +
                "exclua o campo _id de todos os documentos da coleção.");
        for (Row row: res)
            System.out.println(row.getString("[json]"));
    }

    public void query4() {
        ResultSet res = session.execute("SELECT JSON * FROM restaurants");
        System.out.println("--- 4. Indique o total de restaurantes localizados no Bronx.");

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        // Variable to count number of restaurants
        int count = 0;
        JSONObject resJson = new JSONObject();

        for (Row row: res) {
            try {
                JSONObject json = (JSONObject) jsonParser.parse(row.getString("[json]"));
                if (json.get("localidade").equals("Bronx"))
                    count++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        resJson.put("bronx_restaurants", count);
        System.out.println(resJson);
    }

    public void query5() {
        ResultSet res = session.execute("SELECT JSON * FROM restaurants");
        System.out.println("--- 5. Apresente os primeiros 15 restaurantes localizados no Bronx, ordenados por " +
                "ordem crescente de nome.");

        List<JSONObject> restaurants = convertResultSet(res);

        restaurants.stream()
                .filter(e -> e.get("localidade").equals("Bronx"))
                .sorted(Comparator.comparing(e -> e.get("nome").toString()))
                .limit(15).forEach(System.out::println);
    }

    public void query7() {
        ResultSet res = session.execute("SELECT JSON * FROM restaurants");
        System.out.println("--- 7. Encontre os restaurantes que obtiveram uma ou mais pontuações (score) entre [80 e 100].");

        List<JSONObject> restaurants = convertResultSet(res);

        JSONArray grades; JSONObject grade;
        int nGrades; boolean checksConditions;

        for (JSONObject restaurant: restaurants) {

            checksConditions = false;

            grades = (JSONArray) restaurant.get("grades");
            nGrades = (grades).size();

            for (int i = 0; i < nGrades; i++) {

                grade = (JSONObject) grades.get(i);
                if ((long) grade.get("score") >= 80 && (long) grade.get("score") <= 100)
                    checksConditions = true;
            }

            if (checksConditions)
                System.out.println(restaurant);
        }
    }

    public void query8() {
        ResultSet res = session.execute("SELECT JSON * FROM restaurants");
        System.out.println("--- 8. Indique os restaurantes com latitude inferior a -95,7.");

        List<JSONObject> restaurants = convertResultSet(res);

        restaurants.stream()
                .filter(e -> (double) ((JSONArray) ((JSONObject) e.get("address")).get("coord")).get(0) < -95.7)
                .forEach(System.out::println);
    }

    public void query12() {
        ResultSet res = session.execute("SELECT JSON restaurant_id, nome, localidade, gastronomia FROM restaurants");
        System.out.println("--- 12. Liste o restaurant_id, o nome, a localidade e a gastronomia dos restaurantes localizados em " +
                "\"Staten Island\", \"Queens\", ou \"Brooklyn\"");

        List<JSONObject> restaurants = convertResultSet(res);

        restaurants.stream()
                .filter(e -> e.get("localidade").toString().equals("Staten Island")
                        || e.get("localidade").toString().equals("Queens")
                        || e.get("localidade").toString().equals("Brooklyn"))
                .forEach(System.out::println);
    }

    public void query14() {
        ResultSet res = session.execute("SELECT JSON nome, grades FROM restaurants");
        System.out.println("--- 14. Liste o nome e as avaliações dos restaurantes que obtiveram uma avaliação com um " +
                "grade \"A\", um score 10 na data \"2014-08-11T00:00:00Z\" (ISODATE)");

        List<JSONObject> restaurants = convertResultSet(res);

        JSONArray grades; JSONObject grade;
        int nGrades; boolean checksConditions;

        long dateLong; Date date;

        Instant instant = Instant.parse("2014-08-11T00:00:00Z");
        Date desiredDate = Date.from(instant);

        for (JSONObject restaurant: restaurants) {

            checksConditions = false;

            grades = (JSONArray) restaurant.get("grades");
            nGrades = (grades).size();

            for (int i = 0; i < nGrades; i++) {

                grade = (JSONObject) grades.get(i);

                dateLong = (long) ((JSONObject) grade.get("date")).get("$date");
                date = new Date(dateLong);

                if (grade.get("grade").equals("A") && (long) grade.get("score") == 10 &&
                        date.equals(desiredDate))
                    checksConditions = true;
            }

            if (checksConditions)
                System.out.println(restaurant);
        }

    }

    public void query15() {
        ResultSet res = session.execute("SELECT JSON restaurant_id, nome, grades FROM restaurants");
        System.out.println("--- 15. Liste o restaurant_id, o nome e os score dos restaurantes nos quais a segunda " +
                "avaliação foi grade \"A\" e ocorreu em ISODATE \"2014-08-11T00:00 00Z\".");

        List<JSONObject> restaurants = convertResultSet(res);

        JSONArray grades; JSONObject grade;

        Instant instant = Instant.parse("2014-08-11T00:00:00Z");
        Date desiredDate = Date.from(instant);

        for (JSONObject restaurant: restaurants) {

            grades = (JSONArray) restaurant.get("grades");
            if (grades.size() < 2)
                continue;

            grade = (JSONObject) grades.get(1);

            Date date = new Date((long) ((JSONObject) grade.get("date")).get("$date"));

            if (grade.get("grade").equals("A") && date.equals(desiredDate))
                System.out.println(restaurant);
        }
    }

    public void query17() {
        ResultSet res = session.execute("SELECT JSON nome, gastronomia, localidade FROM restaurants");
        System.out.println("--- 17. Liste nome, gastronomia e localidade de todos os restaurantes ordenando por ordem " +
                "crescente da gastronomia e, em segundo, por ordem decrescente de localidade.");

        List<JSONObject> restaurants = convertResultSet(res);

        Collections.sort(restaurants, (e1, e2) -> {

            String gastronomia1 = e1.get("gastronomia").toString();
            String gastronomia2 = e2.get("gastronomia").toString();

            if (gastronomia1.compareTo(gastronomia2) == 0) {

                String localidade1 = e1.get("localidade").toString();
                String localidade2 = e2.get("localidade").toString();

                return localidade2.compareTo(localidade1);
            }

            return gastronomia1.compareTo(gastronomia2);
        });

        restaurants.stream().forEach(System.out::println);
    }

    public void query19() {
        ResultSet res = session.execute("SELECT JSON localidade, restaurant_id FROM restaurants");
        System.out.println("--- 19. Conte o total de restaurante existentes em cada localidade.");

        List<JSONObject> restaurants = convertResultSet(res);

        List<String> localidades = new ArrayList<>();

        String localidade;
        for (JSONObject restaurant: restaurants) {
            localidade = restaurant.get("localidade").toString();
            if (!localidades.contains(localidade)) {
                localidades.add(localidade);
            }
        }

        long count; JSONObject result;
        for (String l: localidades) {

            result = new JSONObject();
            count = restaurants.stream().filter(e -> e.get("localidade").toString().equals(l)).distinct().count();

            result.put("localidade", l);
            result.put("nRestaurants", count);

            System.out.println(result);
        }
    }

    public void query27() {
        ResultSet res = session.execute("SELECT JSON restaurant_id, grades FROM restaurants");
        System.out.println("--- 27. Apresente o restaurant_id e o score dos 5 restaurantes com score médio mais baixo.");

        List<JSONObject> restaurants = convertResultSet(res);
        long avgScore; JSONArray grades; JSONObject grade; int nGrades;

        List<JSONObject> restaurantsScore = new ArrayList<>();
        JSONObject temp;

        for (JSONObject restaurant: restaurants) {

            avgScore = 0;
            temp = new JSONObject();

            grades = (JSONArray) restaurant.get("grades");
            nGrades = (grades).size();

            for (int i = 0; i < nGrades; i++) {
                grade = (JSONObject) grades.get(i);
                avgScore += (long) grade.get("score");
            }

            avgScore = avgScore / nGrades;

            temp.put("restaurant_id", restaurant.get("restaurant_id"));
            temp.put("avgScore", avgScore);

            restaurantsScore.add(temp);
        }

        restaurantsScore.stream()
                .sorted(Comparator.comparing(e -> (long) e.get("avgScore")))
                .limit(5).forEach(System.out::println);
    }

    public void query30() {
        ResultSet res = session.execute("SELECT JSON localidade, gastronomia FROM restaurants");
        System.out.println("--- 30. Apresente o número de gastronomias diferentes na localidade \"Manhattan\".");

        List<JSONObject> restaurants = convertResultSet(res);
        Set<String> gastronomias = new HashSet<>();

        String localidade, gastronomia;
        for (JSONObject restaurant: restaurants) {
            localidade = restaurant.get("localidade").toString();
            if (localidade.equals("Manhattan")) {
                gastronomia = restaurant.get("gastronomia").toString();
                gastronomias.add(gastronomia);
            }
        }

        JSONObject result = new JSONObject();

        result.put("nome", "Manhattan");
        result.put("nGastronomias", gastronomias.size());

        System.out.println(result);
    }

    public void numberOfGastronomiasPerLocalidade() {

        ResultSet res = session.execute("SELECT JSON localidade, gastronomia FROM restaurants");
        System.out.println("--- EXTRA. Apresente o número de gastronomias diferentes por localidade.");

        List<JSONObject> restaurants = convertResultSet(res);
        List<String> localidades = new ArrayList<>();

        String localidade;
        for (JSONObject restaurant: restaurants) {
            localidade = restaurant.get("localidade").toString();
            if (!localidades.contains(localidade)) {
                localidades.add(localidade);
            }
        }

        long count; JSONObject result;
        for (String l: localidades) {

            result = new JSONObject();
            count = restaurants.stream().filter(e -> e.get("localidade").toString().equals(l)).distinct().count();

            result.put("localidade", l);
            result.put("nGastronomias", count);

            System.out.println(result);
        }

    }

    public List<JSONObject> convertResultSet(ResultSet res) {

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        List<JSONObject> restaurants = new ArrayList<>();
        for (Row row: res) {
            try {
                JSONObject json = (JSONObject) jsonParser.parse(row.getString("[json]"));
                restaurants.add(json);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return restaurants;
    }
}
