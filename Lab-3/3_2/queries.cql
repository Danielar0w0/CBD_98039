-- a) e c)

-- Permitir a pesquisa de todos os vídeos de determinado autor.

SELECT * FROM keyspace_videos.videosByAuthor WHERE author_email = 'ddias@ua.pt';

-- Permitir a pesquisa de comentários por utilizador, ordenado inversamente pela data.

SELECT * FROM keyspace_videos.commentsByAuthor WHERE author_email = 'ddias@ua.pt';

-- Permitir a pesquisa de comentários por vídeos, ordenado inversamente pela data.

SELECT * FROM keyspace_videos.commentsByVideo WHERE video_name = 'XPTO';

-- Permitir a pesquisa do rating médio de um vídeo e quantas vezes foi votado.

SELECT AVG(rating) from keyspace_videos.ratingsByVideo WHERE video_name = 'Video Today';

SELECT COUNT(*) from keyspace_videos.ratingsByVideo WHERE video_name = 'Video Today';

-- b)

SELECT * FROM keyspace_videos.users;
SELECT * FROM keyspace_videos.videos;
SELECT * FROM keyspace_videos.comments;
SELECT * FROM keyspace_videos.followers;
SELECT * FROM keyspace_videos.events;
SELECT * FROM keyspace_videos.ratings;
SELECT * FROM keyspace_videos.videosbyauthor;
SELECT * FROM keyspace_videos.commentsbyauthor;
SELECT * FROM keyspace_videos.commentsbyvideo;
SELECT * FROM keyspace_videos.ratingsbyvideo;

-- d)

-- 1. Os últimos 3 comentários introduzidos para um vídeo;

SELECT * FROM keyspace_videos.commentsbyvideo WHERE video_name = 'Video 1' LIMIT 3;

-- 2. Lista das tags de determinado vídeo;

SELECT tag FROM keyspace_videos.videos WHERE video_name = 'Gardening';

-- 3. Todos os vídeos com a tag Aveiro;

-- Individual collections are not indexed internally.
-- This means that even to access a single element of a collection, the while collection has to be read (and reading one is not paged internally).

-- This query involves data filtering and thus may have unpredictable performance.
-- We have two options to fix this: Create a secondary index for this collection, or add ALLOW FILTERING to our query and accept that it will be slow.

SELECT * FROM keyspace_videos.videos WHERE tag contains 'Aveiro' ALLOW FILTERING;

-- 4. Os últimos 5 eventos de determinado vídeo realizados por um utilizador;

-- Here we face a problem. We set the partition key as (user_email, video_name, stamp) and we're doing a query without specifying all elements of said key.
-- We have three options to fix this: Re-think the table’s primary key to get the data we want, create a secondary indez, or add ALLOW FILTERING to our query and accept that it will be slow.

SELECT * FROM keyspace_videos.events WHERE user_email = 'ddias@ua.pt' and video_name = 'Video 1' LIMIT 5 ALLOW FILTERING;

-- 5. Vídeos partilhados por determinado utilizador (maria1987, por exemplo) num determinado período de tempo (Agosto de 2017, por exemplo);

-- Here we face a problem. We are doing a query where we specify the "stamp", which is a regular column (in other words, it isn't part of the primary key).
-- Regular columns can be restricted by =, >, >=, <= and <, CONTAINS or CONTAINS KEY restrictions if the query is a secondary index query.
-- However, "stamp" isn't a secondary index.

-- We can add ALLOW FILTERING to our query and accept that it will be slow.

SELECT * FROM keyspace_videos.videosbyauthor WHERE author_email = 'ddias@ua.pt' AND stamp = '2021-12-06 20:15:52.837' ALLOW FILTERING;

-- 6. Os últimos 10 vídeos, ordenado inversamente pela data da partilhada;

-- ORDER BY clause is only supported on the clustered columns of the PRIMARY KEY and when the partition key is restricted by an Equality or an IN operator in where clause.

-- In CQL, the ORDER BY clause is only used to reverse the defined sort direction of our clustering order. 
-- As for the columns themselves, you can only specify the columns defined in your CLUSTERING ORDER BY clause at table creation time. 
-- So we cannot pick arbitrary columns to order your result set at query-time.

-- 7. Todos os seguidores (followers) de determinado vídeo;

SELECT * FROM keyspace_videos.followers WHERE video_name = 'XPTO';

-- 8. Todos os comentários (dos vídeos) que determinado utilizador está a seguir (following);

-- Here we are required to perform a join between the tables comments and followers; However, cannot perform joins in Cassandra. 
-- In this case, we’ll have to either do the work on the client side, or create a denormalized second table that represents the join results for us.

-- 9. Os 5 vídeos com maior rating;

-- ORDER BY clause is only supported on the clustered columns of the PRIMARY KEY and when the partition key is restricted by an Equality or an IN operator in WHERE clause.
-- We can't make this query, rating was not defined as a clustered column and the partion key isn't being restricted.

-- 10. Uma query que retorne todos os vídeos e que mostre claramente a forma pela qual estão ordenados;

SELECT author_email, video_name FROM keyspace_videos.videosbyauthor;
SELECT * FROM keyspace_videos.videos;

-- There isn't an explicit order in both queries.

-- 11. Lista com as Tags existentes e o número de vídeos catalogados com cada uma delas;

-- Since there is no support for Join/Sub-queries in Cassandra, we can't obtain a single list with all the different tags, neither can we obtain the number of videos for each one of them.

