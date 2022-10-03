
------------------------------

NOTES

Create a Maven project:
mvn archetype:generate -DgroupId=pt.ua.deti.something -DartifactId=Something -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false

[WINDOWS] mvn archetype:generate -D"groupId"=pt.ua.deti.something -D"artifactId"=WeatherForecast -D"archetypeArtifactId"=maven-archetype-quickstart -D"archetypeVersion"=1.4 -D"interactiveMode"=false

---

Compile and run Maven project:

mvn clean package #get dependencies, compiles the project and creates the jar
mvn exec:java -Dexec.mainClass="pt.ua.deti.something.Something"

---

Material:

https://redis.com/redis-enterprise/data-structures/
https://www.digitalocean.com/community/cheatsheets/how-to-manage-sorted-sets-in-redis


------------------------------

EXERCISE 1_5

Main.java
Class where all user interactions happen. These interactions are possible with a menu with the following options: Add a new user, Login, and Exit.
After logging in (with a username), you can follow other users, get messages from the users you're following, send messages and finally logout.

UserDB.java
Class used for storing and keeping track of all users, followers, and messages sent by each user.
By commenting the line "db.close()", you can keep users/messages from previous sessions.

OUTPUT (example)

Choose a option (1-3):
1. Add a new user
2. Login
3. Exit
1
Insert your username:
a
a: User was added successfully!
Choose a option (1-3):
1. Add a new user
2. Login
3. Exit
1
Insert your username:
a
User already exists!
Choose a option (1-3):
1. Add a new user
2. Login
3. Exit
1
Insert your username:
b
b: User was added successfully!
Choose a option (1-3):
1. Add a new user
2. Login
3. Exit
2
Insert your username:
a
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
Write your message:
Hello!
Your message was sent!
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
0
Insert the user you want to follow:
c
User doesn't exist!
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
0
Insert the user you want to follow:
b
Successfully followed!
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
1
No new messages!
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
3
Choose a option (1-3):
1. Add a new user
2. Login
3. Exit
2
Insert your username:
b
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
2
Write your message:
Hello!
Your message was sent!
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
3
Choose a option (1-3):
1. Add a new user
2. Login
3. Exit
2
Insert your username:
a
Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
1
Hello!

Choose a option (1-3):
0. Follow another user
1. Get messages
2. Send message
3. Logout
3
Choose a option (1-3):
1. Add a new user
2. Login
3. Exit
3