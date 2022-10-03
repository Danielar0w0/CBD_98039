--- CQL DML

-- a)

-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS keyspace_videos WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Gestão de utilizadores: Registar o username, nome, email, selo temporal de registo na plataforma.

CREATE TABLE IF NOT EXISTS keyspace_videos.users (
    username TEXT,
    name TEXT,
    email TEXT PRIMARY KEY,
    stamp TIMESTAMP
);

-- Gestão de vídeos: Registar o autor da partilha, nome do vídeo, descrição, uma ou mais etiquetas (tag)
-- e selo temporal de upload/partilha.

CREATE TABLE IF NOT EXISTS keyspace_videos.videos (
    author_email TEXT,
    video_name TEXT,
    description TEXT,
    tag LIST<TEXT>,
    stamp TIMESTAMP,
    PRIMARY KEY ( video_name )
);

-- Gestão de comentários: realizados para um vídeo em determinado momento temporal e tem como autor um utilizador.

CREATE TABLE IF NOT EXISTS keyspace_videos.comments (
    author_email TEXT,
    video_name TEXT,
    stamp TIMESTAMP,
    PRIMARY KEY ( (author_email, video_name, stamp) )
);

-- Gestão de vídeo followers: Permitir o registo de utilizadores que seguem determinado vídeo.

CREATE TABLE IF NOT EXISTS keyspace_videos.followers (
  video_name TEXT PRIMARY KEY,
  users_email LIST<TEXT>
);

-- Registo de eventos: Por vídeo e utilizador e podem ser do tipo play/pause/stop, incluindo
-- o registo temporal do evento e o momento (temporal) em que ocorre no vídeo.

CREATE TABLE IF NOT EXISTS keyspace_videos.events (
    video_name TEXT,
    user_email TEXT,
    action TEXT,
    stamp TIMESTAMP,
    video_time INT, -- in seconds
    PRIMARY KEY ( (user_email, video_name) )
);

-- Rating de vídeos: valor inteiro de 1-5, por vídeo e não necessita de registo do autor.

CREATE TABLE IF NOT EXISTS keyspace_videos.ratings (
    user_email TEXT,
    rating SMALLINT,
    video_name TEXT,
    PRIMARY KEY ( user_email, video_name )
);

-- Permitir a pesquisa de todos os vídeos de determinado autor.

CREATE TABLE IF NOT EXISTS keyspace_videos.videosByAuthor(
    author_email TEXT,
    video_name TEXT,
    description TEXT,
    tag LIST<TEXT>,
    stamp TIMESTAMP,
    PRIMARY KEY ( author_email, video_name )
) WITH CLUSTERING ORDER BY (video_name DESC);

-- Permitir a pesquisa de comentários por utilizador, ordenado inversamente pela data.

CREATE TABLE IF NOT EXISTS keyspace_videos.commentsByAuthor (
    author_email TEXT,
    video_name TEXT,
    stamp TIMESTAMP,
    PRIMARY KEY ( (author_email), stamp )
) WITH CLUSTERING ORDER BY (stamp DESC);

-- Permitir a pesquisa de comentários por vídeos, ordenado inversamente pela data.

CREATE TABLE IF NOT EXISTS keyspace_videos.commentsByVideo (
    author_email TEXT,
    video_name TEXT,
    stamp TIMESTAMP,
    PRIMARY KEY ( (video_name), stamp )
) WITH CLUSTERING ORDER BY (stamp DESC);

-- Permitir a pesquisa do rating médio de um vídeo e quantas vezes foi votado;

CREATE TABLE IF NOT EXISTS keyspace_videos.ratingsByVideo (
    user_email TEXT,
    rating SMALLINT,
    video_name TEXT,
    PRIMARY KEY ( (video_name), user_email )
);


--- b)

-- users

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Danik', 'Daniela Dias', 'ddias@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('DD', 'Daniel Noites', 'dnoites@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Bern', 'Bernardo', 'bern@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('JoANA', 'Joana Gomes', 'j.ana@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('PJ', 'Pedro João', 'p.joao@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Marta', 'Marta Flores', 'marta.flores@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('DMartins', 'Diogo Martins', 'diogo.martins@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Luna', 'Luna Rosas', 'rosas@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('SuperMario', 'Mario Miguel', 'super.mario@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Leo', 'Leonardo Gomes', 'leo.go@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Filipa', 'Filipa Daniela', 'fdani@ua.pt', toTimeStamp(now()));

INSERT INTO keyspace_videos.users
(username, name, email, stamp)
VALUES ('Gaby', 'Gabriela Santos', 'gabyss@ua.pt', toTimeStamp(now()));

-- videos

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Video 1', toTimestamp(now()), 'ddias@ua.pt', 'My first video!', ['Random', 'Attempt']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Video 2', toTimestamp(now()), 'ddias@ua.pt', 'My second video!', ['Random', 'Attempt']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Random title!', toTimestamp(now()), 'bern@ua.pt', 'Just experimenting!', ['Random', 'Experiment']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('DIY House', toTimestamp(now()), 'leo.go@ua.pt', 'My DIY video!', ['DIY', 'House']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Gardening', toTimestamp(now()), 'j.ana@ua.pt', 'Gardening for life!', ['Gardening', 'Flowers']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Moving to a new house!', toTimestamp(now()), 'fdani@ua.pt', 'Lets go!', ['New House', 'Moving']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Travelling to Japan', toTimestamp(now()), 'ddias@ua.pt', 'New adventures.', ['Japan', 'Travel']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('XPTO', toTimestamp(now()), 'rosas@ua.pt', 'I dont know.', ['Random', 'Attempt']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Video Today', toTimestamp(now()), 'rosas@ua.pt', 'Subscribe!', ['Daily', 'Vlog']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Deleting my Account', toTimestamp(now()), 'dnoites@ua.pt', 'Bye bye', ['Goodbye']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Hello everyone', toTimestamp(now()), 'gabyss@ua.pt', 'My first video!', ['Hello', 'First']);

INSERT INTO keyspace_videos.videos
(video_name, stamp, author_email, description, tag)
VALUES ('Videoooo', toTimestamp(now()), 'gabyss@ua.pt', 'My second video!', ['Random', 'Aveiro']);

-- comments

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('rosas@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('ddias@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('rosas@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('fdani@ua.pt', 'Video 2', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('dnoites@ua.pt', 'XPTO', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('leo.go@ua.pt', 'Hello everyone', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('bern@ua.pt', 'Hello Everyone', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('gabyss@ua.pt', 'Video today', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('gabyss@ua.pt', 'Video 2', toTimestamp(now()));

INSERT INTO keyspace_videos.comments
(author_email, video_name, stamp)
VALUES ('bern@ua.pt', 'DIY House', toTimestamp(now()));

-- followers

INSERT INTO keyspace_videos.followers
(video_name, users_email)
VALUES ('Video1', ['rosas@ua.pt', 'fdani@ua.pt']);

INSERT INTO keyspace_videos.followers
(video_name, users_email)
VALUES ('Video 2', ['dnoites@ua.pt']);

INSERT INTO keyspace_videos.followers
(video_name, users_email)
VALUES ('XPTO', ['leo.go@ua.pt', 'dnoites@ua.pt']);

INSERT INTO keyspace_videos.followers
(video_name, users_email)
VALUES ('DIY House', ['bern@ua.pt']);

INSERT INTO keyspace_videos.followers
(video_name, users_email)
VALUES ('Video Today', ['gabyss@ua.pt', 'rosas@ua.pt']);

INSERT INTO keyspace_videos.followers
(video_name, users_email)
VALUES ('XPTO', ['j.ana@ua.pt', 'rosas@ua.pt']);

INSERT INTO keyspace_videos.followers
(video_name, users_email)
VALUES ('Travelling to Japan', ['bern@ua.pt']

-- events

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('ddias@ua.pt', 'Video 1', 'play', toTimestamp(now()), 30);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('ddias@ua.pt', 'Video 1', 'pause', toTimestamp(now()), 50);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('bern@ua.pt', 'Video Today', 'play', toTimestamp(now()), 30);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('bern@ua.pt', 'Video Today', 'stop', toTimestamp(now()), 300);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('rosas@ua.pt', 'DIY House', 'play', toTimestamp(now()), 0);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('rosas@ua.pt', 'DIY House', 'stop', toTimestamp(now()), 20);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('rosas@ua.pt', 'DIY House', 'stop', toTimestamp(now()), 250);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('j.ana@ua.pt', 'Gardening', 'play', toTimestamp(now()), 100);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('leo.go@ua.pt', 'Video 2', 'play', toTimestamp(now()), 20);

INSERT INTO keyspace_videos.events
(user_email, video_name, action, stamp, video_time)
VALUES ('leo.go@ua.pt', 'Video 2', 'stop', toTimestamp(now()), 400);

-- ratings

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('ddias@ua.pt', 'Video Today', 4);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('ddias@ua.pt', 'DIY House', 5);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('rosas@ua.pt', 'Video Today', 2);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('rosas@ua.pt', 'Video 1', 1);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('leo.go@ua.pt', 'Video 2', 2);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('j.ana@ua.pt', 'Video 2', 3);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('bern@ua.pt', 'Gardening', 5);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('dnoites@ua.pt', 'Video Today', 1);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('dnoites@ua.pt', 'Moving to a new house!', 4);

INSERT INTO keyspace_videos.ratings
(user_email, video_name, rating)
VALUES ('fdani@ua.pt', 'Moving to a new house!', 5);

-- videosByAuthor

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Video 1', toTimestamp(now()), 'ddias@ua.pt', 'My first video!', ['Random', 'Attempt']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Video 2', toTimestamp(now()), 'ddias@ua.pt', 'My second video!', ['Random', 'Attempt']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Random title!', toTimestamp(now()), 'bern@ua.pt', 'Just experimenting!', ['Random', 'Experiment']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('DIY House', toTimestamp(now()), 'leo.go@ua.pt', 'My DIY video!', ['DIY', 'House']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Gardening', toTimestamp(now()), 'j.ana@ua.pt', 'Gardening for life!', ['Gardening', 'Flowers']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Moving to a new house!', toTimestamp(now()), 'fdani@ua.pt', 'Lets go!', ['New House', 'Moving']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Travelling to Japan', toTimestamp(now()), 'ddias@ua.pt', 'New adventures.', ['Japan', 'Travel']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('XPTO', toTimestamp(now()), 'rosas@ua.pt', 'I dont know.', ['Random', 'Attempt']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Video Today', toTimestamp(now()), 'rosas@ua.pt', 'Subscribe!', ['Daily', 'Vlog']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Deleting my Account', toTimestamp(now()), 'dnoites@ua.pt', 'Bye bye', ['Goodbye']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Hello everyone', toTimestamp(now()), 'gabyss@ua.pt', 'My first video!', ['Hello', 'First']);

INSERT INTO keyspace_videos.videosByAuthor
(video_name, stamp, author_email, description, tag)
VALUES ('Videoooo', toTimestamp(now()), 'gabyss@ua.pt', 'My second video!', ['Random', 'Aveiro']);

-- commentsByAuthor

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('rosas@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('ddias@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('rosas@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('fdani@ua.pt', 'Video 2', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('dnoites@ua.pt', 'XPTO', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('leo.go@ua.pt', 'Hello everyone', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('bern@ua.pt', 'Hello Everyone', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('gabyss@ua.pt', 'Video today', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('gabyss@ua.pt', 'Video 2', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByAuthor
(author_email, video_name, stamp)
VALUES ('bern@ua.pt', 'DIY House', toTimestamp(now()));

-- commentsByVideo

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('rosas@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('ddias@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('rosas@ua.pt', 'Video 1', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('fdani@ua.pt', 'Video 2', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('dnoites@ua.pt', 'XPTO', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('leo.go@ua.pt', 'Hello everyone', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('bern@ua.pt', 'Hello Everyone', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('gabyss@ua.pt', 'Video today', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('gabyss@ua.pt', 'Video 2', toTimestamp(now()));

INSERT INTO keyspace_videos.commentsByVideo
(author_email, video_name, stamp)
VALUES ('bern@ua.pt', 'DIY House', toTimestamp(now()));

-- ratingsByVideo

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('ddias@ua.pt', 'Video Today', 4);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('ddias@ua.pt', 'DIY House', 5);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('rosas@ua.pt', 'Video Today', 2);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('rosas@ua.pt', 'Video 1', 1);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('leo.go@ua.pt', 'Video 2', 2);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('j.ana@ua.pt', 'Video 2', 3);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('bern@ua.pt', 'Gardening', 5);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('dnoites@ua.pt', 'Video Today', 1);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('dnoites@ua.pt', 'Moving to a new house!', 4);

INSERT INTO keyspace_videos.ratingsByVideo
(user_email, video_name, rating)
VALUES ('fdani@ua.pt', 'Moving to a new house!', 5);
