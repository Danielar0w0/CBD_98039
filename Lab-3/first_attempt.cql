-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS store WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create a table
CREATE TABLE IF NOT EXISTS store.shopping_cart (
userid text PRIMARY KEY,
item_count int,
last_update_timestamp timestamp
);

-- Insert some data
INSERT INTO store.shopping_cart
(userid, item_count, last_update_timestamp)
VALUES ('9876', 2, toTimeStamp(now()));
INSERT INTO store.shopping_cart
(userid, item_count, last_update_timestamp)
VALUES ('1234', 5, toTimeStamp(now()));




INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Danik', 'Daniela Dias', 'ddias@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.events
(video_name, user_email, action, stamp, video_time)
VALUES ('XPTO', 'YGZ', 'play', toTimeStamp(now()), 300);
