import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class MongoDB_Driver {

    private MongoClient mongoClient;
    private MongoDatabase database;
    MongoCollection<Document> collection;

    public MongoDB_Driver() {
        this.mongoClient = new MongoClient();
        this.database = mongoClient.getDatabase("cbd");
        this.collection = database.getCollection("people");
    }

    public static void main(String[] args) {
        MongoDB_Driver driver = new MongoDB_Driver();

        // driver.findFirst();
        // driver.findAll();

        // ---
        // Find

        // driver.findEqual("name", "Guy Flores");
        // driver.findAllEqual("gender", "male");

        // String name = "Blanca Duran";
        // driver.hasFriend(name);

        // int limit_under = 1400;
        // driver.balanceLessThan(limit_under);

        // int limit_above = 3600;
        // driver.balanceGreaterThan(limit_above);

        // driver.phoneWithAreaCode(926);

        // driver.companyStartsWith("APEX");

        // driver.oldestPerson();

        // ---
        // Aggregate

        // driver.averageAge();

        // driver.numberOfMales();
        // driver.numberOfFemales();

        // driver.maxBalance();

        // driver.firstFriend("Guy Flores");

        // driver.smallestNameCompany();
    }

    // Basic find queries

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

    // Specific find queries

    public void hasFriend(String name) {

        Bson filter = Filters.eq("friends.name", name);
        Bson projection = Projections.fields(Projections.include("index", "name", "friends"), Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(filter).projection(projection).iterator();

        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public void balanceLessThan(int limit_under) {

        String temp = String.valueOf(limit_under);
        temp = temp.substring(0, temp.length()-3) + "," + temp.substring(temp.length()-3);

        String limit = "$"+temp+".00";

        Bson filter = Filters.lt("balance", limit);
        Bson projection = Projections.fields(Projections.include("index", "name", "balance"), Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(filter).projection(projection).iterator();

        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public void balanceGreaterThan(int limit_above) {

        String temp = String.valueOf(limit_above);
        temp = temp.substring(0, temp.length()-3) + "," + temp.substring(temp.length()-3);

        String limit = "$"+temp+".00";

        Bson filter = Filters.gt("balance", limit);
        Bson projection = Projections.fields(Projections.include("index", "name", "balance"), Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(filter).projection(projection).iterator();

        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public void phoneWithAreaCode(int code) {

        String regex = "^.*\\(" + code + "\\).*";

        Bson filter = Filters.regex("phone", regex);
        Bson projection = Projections.fields(Projections.include("index", "name", "phone"), Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(filter).projection(projection).iterator();

        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public void companyStartsWith(String name) {

        String regex = "^" + name + ".*";

        Bson filter = Filters.regex("company", regex);
        Bson projection = Projections.fields(Projections.include("index", "name", "company"), Projections.excludeId());
        MongoCursor<Document> cursor = collection.find(filter).projection(projection).iterator();

        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    public void oldestPerson() {

        Bson projection = Projections.fields(Projections.include("index", "name", "age"), Projections.excludeId());
        Document doc = collection.find().projection(projection).sort(Sorts.descending("age")).first();
        System.out.println(doc.toJson());

    }

    // Specific aggregate query

    public void averageAge() {

        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                Aggregates.group(null, Accumulators.avg("avgAge", "$age")
                )));

        Document first = aggregate.first();
        if (first != null)
            System.out.printf("Average age: %s\n", first.get("avgAge"));

    }

    public void numberOfMales() {

        Bson filter = Filters.eq("gender", "male");
        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                Aggregates.match(filter),
                Aggregates.group(null, Accumulators.sum("nMales", 1))
        ));

        Document first = aggregate.first();
        if (first != null)
            System.out.printf("Number of males: %s\n", first.get("nMales"));
    }

    public void numberOfFemales() {

        Bson filter = Filters.eq("gender", "female");
        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                Aggregates.match(filter),
                Aggregates.group(null, Accumulators.sum("nFemales", 1))
        ));

        Document first = aggregate.first();
        if (first != null)
            System.out.printf("Number of females: %s\n", first.get("nFemales"));
    }

    public void maxBalance() {

        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                Aggregates.group(null, Accumulators.max("maxBalance", "$balance"))
        ));

        Document first = aggregate.first();
        if (first != null)
            System.out.printf("Max balance: %s\n", first.get("maxBalance"));

    }

    public void firstFriend(String person) {

        Bson filter = Filters.eq("name", person);
        AggregateIterable<Document> aggregate = collection.aggregate(
                Arrays.asList(
                        Aggregates.match(filter),
                        Aggregates.project(
                                Projections.fields(
                                        Projections.excludeId(),
                                        Projections.include("name"),
                                        Projections.computed(
                                                "firstFriend",
                                                new Document("$arrayElemAt", Arrays.asList("$friends", 0))
                                        )))));

        Document first = aggregate.first();
        if (first != null)
            System.out.printf("%s's first friend: %s\n", person, first.get("firstFriend"));
    }

    public void smallestNameCompany() {



        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                Aggregates.project(
                        Projections.fields(
                                Projections.excludeId(),
                                Projections.include("name"),
                                Projections.computed(
                                        "companyLength",
                                        Document.parse("{ $strLenCP: \"$company\" }, ")
                                ))),
                Aggregates.group(null, Accumulators.min("smallestCompany", "$companyLength"))
        ));

        Document first = aggregate.first();
        if (first != null)
            System.out.printf("Smallest company name: %s letters\n", first.get("smallestCompany"));

    }



}
