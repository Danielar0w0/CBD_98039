import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.neo4j.driver.Values.parameters;

public class RetailSystem {

    private final Driver driver;

    public RetailSystem(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public static void main(String... args) throws Exception {

        // ! Run local DBMS with a database called "northwind" and with password "northwind" !
        RetailSystem retailSystem = new RetailSystem("bolt://localhost:7687", "neo4j", "northwind");
        // File dataset = new File("C:/Users/DanielaDias/Documents/Uni/CBD/Lab-4/4_4/Dataset.txt");
        // retailSystem.insertDataset(dataset);

        // Alternative:
        //RetailSystem retailSystem = new RetailSystem("neo4j+s://demo.neo4jlabs.com", "northwind", "northwind");

        // Basic queries
        // retailSystem.getAllProducts();
        // retailSystem.getAllCategories();
        // retailSystem.getAllOrders();
        // retailSystem.getAllCostumers();
        // retailSystem.getAllSuppliers();
        // retailSystem.getRelationshipTypes();

        // Other queries

        retailSystem.query1();
        retailSystem.query2();
        retailSystem.query3(6);
        retailSystem.query4();
        retailSystem.query5("SAVEA");
        retailSystem.query6();
        retailSystem.query7(1);
        retailSystem.query9(10979);
        retailSystem.query8("Chef");
        retailSystem.query10();

        retailSystem.close();
    }

    public void insertDataset(File dataset) throws FileNotFoundException {

        Scanner sc = new Scanner(dataset);

        String command = "";
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("-")) {
                // System.out.println(command);
                try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {
                    final String thisCommand = command;
                    session.writeTransaction(tx -> {
                        tx.run(thisCommand);
                        return null;
                    });
                }
                command = "";
            } else
                command += (" " + line);
        }

        if(command != "") {
            System.out.println(command);
            try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {
                final String thisCommand = command;
                session.writeTransaction(tx -> {
                    tx.run(thisCommand);
                    return null;
                });
            }
        }
    }

    public void close() throws Exception {
        driver.close();
    }

    public void getAllProducts() {

        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Product) return p.productID as productID, p.productName as productName, " +
                        "p.unitPrice as unitPrice, p.unitsInStock as unitsInStock, p.categoryID as categoryID");

                System.out.println("All Products:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    public void getAllCategories() {

        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (c:Category) return c.categoryID as categoryID, " +
                        "c.categoryName as categoryName, c.description as description");

                System.out.println("All Categories:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    public void getAllOrders() {

        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (o:Order) return o.orderID as orderID, " +
                        "o.employeeID as employeeID, o.shipCity as shipCity, o.shipName as shipName, " +
                        "o.shipCountry as shipCountry, o.shipAddress as shipAddress, o.costumerID as costumerID, " +
                        "o.orderDate as orderDate, o.shippedDate as shippedDate");

                System.out.println("All Orders:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    public void getAllCostumers() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (c:Customer) return c.customerID as customerID, " +
                        "c.contactName as contactName, c.companyName as companyName, c.contactTitle as contactTitle, " +
                        "c.country as country, c.address as address, c.phone as phone");

                System.out.println("All Costumers:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    public void getAllSuppliers() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (s:Supplier) return s.supplierID as supplierID, " +
                        "s.contactName as contactName, s.contactTitle as contactTitle, s.companyName as companyName, " +
                        "s.country as country, s.address as address, s.city as city, s.phone as phone");

                System.out.println("All Costumers:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    public void getRelationshipTypes() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match ()-[r]-() return distinct type(r)");

                System.out.println("All Relationship Types:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.get("type(r)"));
                }
                return null;
            });
        }
    }

    // Get list of products which stock is less than the quantity on order
    public void query1() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Product) where p.discontinued = false and p.unitsInStock < p.unitsOnOrder " +
                        "return p.productID as productID, p.productName as productName, p.unitsInStock as inStock, p.unitsOnOrder as onOrder");

                System.out.println("Query 1:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get most expensive product
    public void query2() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Product) return p.productID as productID, p.productName as productName, " +
                        "p.unitPrice as unitPrice order by unitPrice desc limit 1");

                System.out.println("Query 2:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get all products supplied by a specific supplier (supplierID)
    public void query3(int supplierID) {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (:Supplier {supplierID: $supplierID})-[:SUPPLIES]-(p:Product) " +
                        "return p.productID as productID, p.productName as productName", parameters("supplierID", Integer.toString(supplierID)));

                System.out.println("Query 3:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get number of orders of each costumer
    public void query4() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (c:Customer)-[:PURCHASED]-(o:Order) with c, count(o) as nOrders " +
                        "return c.customerID as costumerID, nOrders");

                System.out.println("Query 4:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get the country that a specific costumer (costumerID) ordered most from
    public void query5(String costumerID) {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (c:Customer {customerID: $costumerID})-[:PURCHASED]-(o:Order)-[:ORDERS]-(p:Product)-[:SUPPLIES]-(s:Supplier) " +
                        "with c.customerID as customerID, s.country as country, count(s.country) as n_times return customerID, country, n_times order by " +
                        "n_times desc limit 1", parameters("costumerID", costumerID));

                System.out.println("Query 5:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get all the suppliers that have a homepage which contains numbers
    public void query6() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (s:Supplier) where s.homePage <> \"NULL\" and s.homePage =~ \".*[1-9].*\" " +
                        "return s.supplierID as supplierID, s.contactName as contactName, s.homePage as homePage");

                System.out.println("Query 6:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get a product category
    public void query7(int productID) {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Product {productID: $productID})-[:PART_OF]-(c:Category) return p.productID as productID, " +
                                "c.categoryID as categoryID, c.categoryName as categoryName, c.description as description",
                        parameters("productID", Integer.toString(productID)));

                System.out.println("Query 7:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get all products that contain a specific word
    public void query8(String word) {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (p:Product) where p.productName contains $word " +
                        "return p.productID, p.productName", parameters("word", word));

                System.out.println("Query 8:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get the total price of an order
    public void query9(int orderID) {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (o:Order {orderID: $orderID})-[r:ORDERS]-(:Product) with o.orderID " +
                                "as orderID, sum(r.quantity * toInteger(r.unitPrice)) as totalPrice return orderID, totalPrice",
                        parameters("orderID", Integer.toString(orderID)));

                System.out.println("Query 9:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }

    // Get number of costumers per country
    public void query10() {
        try (Session session = driver.session(SessionConfig.forDatabase("northwind"))) {

            session.writeTransaction(tx -> {
                Result result = tx.run("match (c:Customer) with c.country as country, " +
                        "count(c.country) as nCostumers return country, nCostumers");

                System.out.println("Query 10:");
                for (Result it = result; it.hasNext(); ) {
                    Record record = it.next();
                    System.out.println(record.asMap());
                }
                return null;
            });
        }
    }
}
