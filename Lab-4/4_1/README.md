
# Browser Editor

Browser commands begin with ':'

Execute current command	Ctrl-Return
Previous command in history<Ctrl-Up-Arrow
Next command in history	Ctrl-Down-Arrow

You can clear the stream of result frames by running the :clear
You can bring up the history of the executed commands and queries by running the :history command

# Graph Database

A graph database can store any kind of data using a few basic concepts:

- Nodes - represent entities of a domain.
- Labels - shape the domain by grouping nodes into sets.
- Relationships - connect two nodes.
- Properties - named values that add qualities to nodes and relationships.

# Node

- Nodes often represents entities or discrete objects that can be classified with zero or more labels.
- Data is stored as properties of the nodes.
- Properties are simple key-value pairs.

# Cypher Language

Neo4j's Cypher language is purpose-built for working with graph data.

- Uses patterns to describe graph data.
- Familiar SQL-like clauses.
- Declarative, describing what to find, not how to find it.

--- CREATE

- CREATE creates the node.
- () indicates the node.
- ee:Person – ee is the node variable and Person is the node label.
- {} contains the properties that describe the node.

CREATE (ee:Person {name: 'Emil', from: 'Sweden', kloutScore: 99})

The CREATE clause can create many nodes and relationships at once.

--- MATCH

MATCH (ee:Person) WHERE ee.name = 'Emil' RETURN ee;

- MATCH specifies a pattern of nodes and relationships.
- (ee:Person) is a single node pattern with label Person. It assigns matches to the variable ee.
- WHERE filters the query.
- ee.name = 'Emil' compares name property to the value Emil.
- RETURN returns particular results.

MATCH (ee:Person)-[:KNOWS]-(friends)
WHERE ee.name = 'Emil' RETURN ee, friends

- MATCH describes what nodes will be retrieved based upon the pattern.
- (ee) is the node reference that will be returned based upon the WHERE clause.
- -[:KNOWS]- matches the KNOWS relationships (in either direction) from ee.
- (friends) represents the nodes that are Emil's friends.
- RETURN returns the node, referenced here by (ee), and the related (friends) nodes found.

--- CONSTRAINT 

Create unique node property constraints to ensure that property values are unique for all nodes with a specific label. Adding the unique constraint, implicitly adds an index on that property.

CREATE CONSTRAINT ON (n:Movie) ASSERT (n.title) IS UNIQUE

--- INDEX NODES

Create indexes on one or more properties for all nodes that have a given label. Indexes are used to increase search performance.

CREATE INDEX FOR (m:Movie) ON (m.released)

--- DELETE

Delete all nodes:
MATCH (n) DETACH DELETE n

-------

match (n)
return labels(n) as labels, keys(n) as keys, count(*) as total
order by total desc;

- match () seleciona qualquer nó
- match (n) seleciona qualquer nó e atribui-o à variável n
- a função labels(n) devolve uma lista com o tipo (label) de cada nó n
- a função keys(n) retorna a lista de propriedades de cada nó n
- a função count(n) é uma função de agregação, neste caso conta cada grupo labels,
keys (o GROUP BY é implícito em Cypher)


