
/* ------------------------------------ */
/*    USERS (RBAC)                      */
/* ------------------------------------ */
CREATE TABLE IF NOT EXISTS user (
    email          text,
    password       text,
    firstname      text,
    lastname       text,
    lang_key       text,
    picture        text,
    activation_key text,
    is_active      boolean,
    reset_key      text,
    reset_date     timestamp,
    authorities    set<text>,
    PRIMARY KEY((email))
);

/** Data table user. */
INSERT INTO user(email, password, firstname, lastname, lang_key, is_active, authorities) VALUES('admin@feedsreader.net', '$2a$10$sakpNz.pH28O3oCcx3/iQe5VN1nNiBxDqHwth5iXkguCE02UtApKW', 'admin', 'admin', 'fr', true, {'USER','ADMIN'});
INSERT INTO user(email, password, firstname, lastname, lang_key, is_active, authorities) VALUES('user@feedsreader.net', '$2a$10$sakpNz.pH28O3oCcx3/iQe5VN1nNiBxDqHwth5iXkguCE02UtApKW', 'user', 'user', 'fr', true, {'USER'});
INSERT INTO user(email, password, firstname, lastname, lang_key, is_active, authorities) 
VALUES('cedrick.lunven@gmail.com', '$2a$10$sakpNz.pH28O3oCcx3/iQe5VN1nNiBxDqHwth5iXkguCE02UtApKW', 'Cedrick', 'Lunven', 'fr', true, {'USER','ADMIN'});

select email,firstname,lastname,authorities from user;

/* authentication query. */
select email,password from user where email=?
select email,password from user where email='user@feedsreader.net'

/* get user profile query. */
select * from user where email=?
select * from user where email='user@feedsreader.net'

/* ------------------------------------
 * USER FOLDERS: 
 * - Sorts are done at application level
 * - Using identifiers to allow renaming
 * - Define a parent clause to allow nested folder
 * - Expected query is retrieving all folders for a user and draw the tree at UI
 * - The stats could be done in a dedicated table or in the application with a SUM
 * ------------------------------------ */
CREATE TABLE IF NOT EXISTS folders_by_user (
    email     text,
    id        uuid,
    label     text,
    offset    int,
    parent_id uuid,
    PRIMARY KEY ((email), id)
) WITH CLUSTERING ORDER BY (id ASC);

/**
 * Data Proposition
 * - News
 * - Sciences
 * - Programming
 * +-- SpringFramework
 * +-- Micronaut
 * +-- Quarkus
 * - DB MarketWatch
 */
INSERT INTO folders_by_user(email, id, label, offset) VALUES('cedrick.lunven@gmail.com', 4de847d4-30b1-4255-bbd5-3647c744d0a5, 'News',1);
INSERT INTO folders_by_user(email, id, label, offset) VALUES('cedrick.lunven@gmail.com', 2aca2821-1445-4fb2-bef9-f57ed760a40d, 'Sciences',2);
INSERT INTO folders_by_user(email, id, label, offset) VALUES('cedrick.lunven@gmail.com', b9eb8c75-0dcb-4016-8086-6c163edff619, 'Programming',3);
INSERT INTO folders_by_user(email, id, label, offset) VALUES('cedrick.lunven@gmail.com', 322c7255-6171-4d9e-8e2c-39eff4b48ccb, 'DB MarketWatch',4);
INSERT INTO folders_by_user(email, id, label, offset, parent_id) VALUES('cedrick.lunven@gmail.com', uuid(), 'SpringFramework',1, b9eb8c75-0dcb-4016-8086-6c163edff619);
INSERT INTO folders_by_user(email, id, label, offset, parent_id) VALUES('cedrick.lunven@gmail.com', uuid(), 'Micronaut',2, b9eb8c75-0dcb-4016-8086-6c163edff619);
INSERT INTO folders_by_user(email, id, label, offset, parent_id) VALUES('cedrick.lunven@gmail.com', uuid(), 'Quarkus',3, b9eb8c75-0dcb-4016-8086-6c163edff619);

/* ------------------------------------
 * USER FEEDS: 
 * - Look for all feeds of a user
 * - last_read to get the last message read timestamp
 * - Uncount read will be a counter
 */
CREATE TABLE IF NOT EXISTS feeds_by_user (
    email      text,
    feed_id    uuid,
    feed_title text,
    feed_icon  text,
    folder_id  uuid,
    last_read  timestamp,
    PRIMARY KEY ((email), feed_id)
) WITH CLUSTERING ORDER BY (feed_id ASC);


/* ZDNET as a feed. */
INSERT INTO feeds_by_user(email,feed_id, feed_title, feed_url, folder_id, last_read) VALUES('cedrick.lunven@gmail.com', uuid(), 'ZDNet', 'https://www.zdnet.fr/feeds/rss/', 4de847d4-30b1-4255-bbd5-3647c744d0a5, toTimestamp(now()));

CREATE TABLE IF NOT EXISTS feed (
    id          uuid,
    url         text,
    title       text,
    description text,
    language    text,
    last_build  timestamp,
    last_parsed timestamp,
    image,
    PRIMARY KEY (id)
);

INSERT INTO feeds_by_user(email,feed_id, feed_title, feed_url, folder_id, last_read) VALUES('cedrick.lunven@gmail.com', uuid(), 'ZDNet', 'https://www.zdnet.fr/feeds/rss/', 4de847d4-30b1-4255-bbd5-3647c744d0a5, toTimestamp(now()));



CREATE TABLE IF NOT EXISTS unread_articles_stats (
    email      text,
    feed_id    uuid,
    num_unread counter,
)















