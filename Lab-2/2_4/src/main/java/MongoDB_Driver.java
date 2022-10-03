import com.mongodb.*;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.*;
import netscape.javascript.JSObject;
import org.bson.Document;

import java.util.*;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;


public class MongoDB_Driver {

    private MongoClient mongoClient;
    private MongoDatabase database;
    MongoCollection<Document> collection;

    public MongoDB_Driver() {
        this.mongoClient = new MongoClient();
        this.database = mongoClient.getDatabase("cbd");
        this.collection = database.getCollection("restaurants");
    }

    public static void main(String[] args) {
        MongoDB_Driver driver = new MongoDB_Driver();

        // driver.findFirst();
        // driver.findAll();

        // Document doc = Document.parse("{\"address\": {\"building\": \"1007\", \"coord\": [-73.856077, 40.848447], \"rua\": \"Morris Park Ave\", \"zipcode\": \"10462\"}, \"localidade\": \"Aveiro\", \"gastronomia\": \"OvosMoles\", \"grades\": [{\"date\": {\"$date\": 1393804800000}, \"grade\": \"A\", \"score\": 2}, {\"date\": {\"$date\": 1378857600000}, \"grade\": \"A\", \"score\": 6}, {\"date\": {\"$date\": 1358985600000}, \"grade\": \"A\", \"score\": 10}, {\"date\": {\"\": 1322006400000}, \"grade\": \"A\", \"score\": 9}, {\"date\": {\"$date\": 1299715200000}, \"grade\": \"B\", \"score\": 14}], \"nome\": \"Morris Park Bake Shop\", \"restaurant_id\": \"30075445\"}");
        // driver.insert(doc);

        //driver.findEqual("localidade", "Aveiro");
        // driver.findAllEqual("localidade", "Bronx");

        // driver.update("localidade", "Aveiro", "Porto");
        // driver.findEqual("localidade", "Aveiro");
        // driver.findEqual("localidade", "Porto");

        // driver.findAllEqual("localidade", "Bronx");
        // driver.createIndexes();
        // driver.findAllEqual("localidade", "Bronx");

        // driver.function5();

        int nLocalidades = driver.countLocalidades();
        System.out.printf("Número de localidades distintas: %d\n", nLocalidades);
        System.out.println("---");

        Map<String, Integer> perLocalidade = driver.countRestByLocalidade();
        System.out.println("Número de restaurantes por localidade:");
        for (String localidade: perLocalidade.keySet()) {
            System.out.println(localidade + " - " + perLocalidade.get(localidade));
        }
        System.out.println("---");

        String name = "Park";
        List<String> restaurants = driver.getRestWithNameCloserTo(name);
        System.out.printf("Nome de restaurantes contendo '%s' no nome:\n", name);
        for (String restaurant: restaurants) {
            System.out.println(restaurant);
        }
    }

    public void insert(Document doc) {
        collection.insertOne(doc);
        System.out.println("Doc was added successfully!");
    }

    public void insertMany(List<Document> docs) {
        collection.insertMany(docs);
        System.out.println("Docs were added successfully!");
    }

    public void findFirst() {
        Document doc = collection.find().first();
        System.out.println(doc.toJson());
    }

    public void findAll() {
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    // String value
    public void update(String field, String value, String newValue) {
        UpdateResult updateResult = collection.updateOne(eq(field, value), new Document("$set", new Document(field, newValue)));
        if (updateResult.getModifiedCount() != 0)
            System.out.println("Doc was updated successfully!");
    }

    public void updateMany(String field, String value, String newValue) {
        UpdateResult updateResult = collection.updateMany(lt(field, value), new Document("$set", new Document(field, newValue)));
        System.out.print("Docs were updated successfully! Total: ");
        System.out.println(updateResult.getModifiedCount());
    }

    public void findEqual(String field, String value) {
        Document doc = collection.find(eq(field, value)).first();
        if (doc != null)
            System.out.println(doc.toJson());
    }

    public void findAllEqual(String field, String value) {
        MongoCursor<Document> cursor = collection.find(eq(field, value)).iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    // Integer value
    public void update(String field, int value, int newValue) {
        UpdateResult updateResult = collection.updateOne(eq(field, value), new Document("$set", new Document(field, newValue)));
        if (updateResult.getModifiedCount() != 0)
            System.out.println("Doc was updated successfully!");
    }

    public void updateMany(String field, int value, int newValue) {
        UpdateResult updateResult = collection.updateMany(lt(field, value), new Document("$set", new Document(field, newValue)));
        System.out.print("Docs were updated successfully! Total: ");
        System.out.println(updateResult.getModifiedCount());
    }

    public void findEqual(String field, int value) {
        Document doc = collection.find(eq(field, value)).first();
        System.out.println(doc.toJson());
    }

    public void findAllEqual(String field, int value) {
        MongoCursor<Document> cursor = collection.find(eq(field, value)).iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public void createIndex(String field) {
        collection.createIndex(Indexes.ascending(field));
    }

    public void createIndexes() {
        collection.createIndex(Indexes.ascending("localidade"));
        collection.createIndex(Indexes.ascending("gastronomia"));
        collection.createIndex(Indexes.text("nome"));
    }

    public void getIndexes() {
        for (Document index : collection.listIndexes()) {
            System.out.println(index.toJson());
        }
    }

    // Apresente os campos restaurant_id, nome, localidade e gastronomia para todos os documentos da coleção
    public void function1() {
        MongoCursor<Document> cursor = collection.find()
                .projection(new Document("nome", 1)
                .append("restaurant_id", 1)
                .append("localidade", 1)
                .append("gastronomia",1)
                .append("_id", 0))
                .iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    // Indique o total de restaurantes localizados no Bronx
    public void function2() {
        long count = collection.countDocuments(eq("localidade", "Bronx"));
        System.out.printf("Número total de restaurantes localizados no Bronx: %d\n", count);
    }

    // Apresente os primeiros 15 restaurantes localizados no Bronx, ordenados por ordem crescente de nome
    public void function3() {
        MongoCursor<Document> cursor = collection.find(eq("localidade", "Bronx"))
                .sort(Sorts.descending("nome"))
                .limit(15)
                .iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    // Indique os restaurantes com latitude inferior a -95,7.
    public void function4() {
        MongoCursor<Document> cursor = collection.find(lt("address.coord.0", -95.7)).iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    // Liste nome, localidade, grade e gastronomia de todos os restaurantes localizados em Brooklyn que não incluem gastronomia "American"
    // E obtiveram uma classificação (grade) "A" - Deve apresentá-los por ordem decrescente de gastronomia
    public void function5() {
        Bson filter = Filters.and(Filters.eq("localidade", "Brooklyn"), Filters.regex("gastronomia", "^(?!American)"), Filters.eq("grades.grade", "A"));
        Bson projection = Projections.fields(Projections.include("nome", "localidade", "grades.grade", "gastronomia"), Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(filter).projection(projection).sort(Sorts.descending("gastronomia")).iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public int countLocalidades() {

        MongoCursor<String> cursor = collection.distinct("localidade", String.class).iterator();

        int count = 0;
        try {
            while (cursor.hasNext()) {
                count++;
                cursor.next();
            }
        } finally {
            cursor.close();
        }
        return count;
    }

    Map<String, Integer> countRestByLocalidade() {

        Map<String, Integer> perLocalidades = new HashMap<>();
        MongoCursor<Document> cursor = collection.aggregate(Arrays.asList(
                Aggregates.group("$localidade",
                Accumulators.sum("qty", 1)
                ))).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                perLocalidades.put(String.valueOf(doc.get("_id")), (int) doc.get("qty"));
            }
        } finally {
            cursor.close();
        }

        return perLocalidades;
    }

    List<String> getRestWithNameCloserTo(String name) {
        List<String> restaurants = new ArrayList<>();

        String regex = "^.*" + name + ".*";
        Bson filter = Filters.regex("nome", regex);
        Bson projection = Projections.fields(Projections.include("nome"), Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(filter).projection(projection).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                restaurants.add(String.valueOf(doc.get("nome")));
            }
        } finally {
            cursor.close();
        }

        return restaurants;
    }

}
