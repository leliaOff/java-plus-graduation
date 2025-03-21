DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS event_compilations CASCADE;
DROP TABLE IF EXISTS event_requests CASCADE;
DROP TABLE IF EXISTS event_comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email VARCHAR(254) NOT NULL,
    name VARCHAR(250) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);
create index users_id_index on users (id);
create index users_email_index on users (email);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);
create index categories_name_index on categories (name);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL CONSTRAINT events_categories_id_fk references categories,
    confirmed_requests INT DEFAULT 0,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000),
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL CONSTRAINT events_users_id_fk references users,
    paid BOOLEAN DEFAULT false,
    participant_limit INT DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT false,
    state VARCHAR(50) NOT NULL,
    lat FLOAT,
    lon FLOAT,
    CONSTRAINT pk_events PRIMARY KEY (id)
);
create index events_id_index on events (id);
create index events_title_index on events (title);
create index events_category_id_index on events (category_id);
create index events_event_date_index on events (event_date);
create index events_initiator_id_index on events (initiator_id);
create index events_state_index on events (state);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN DEFAULT false,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);
create index compilations_title_index on compilations (title);

CREATE TABLE IF NOT EXISTS event_compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL CONSTRAINT event_compilations_events_id_fk references events,
    compilation_id BIGINT NOT NULL CONSTRAINT event_compilations_compilations_id_fk references compilations,
    CONSTRAINT pk_event_compilations PRIMARY KEY (id)
);
create index event_compilations_event_id_compilation_id_index on event_compilations (event_id, compilation_id);

CREATE TABLE IF NOT EXISTS event_requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL CONSTRAINT event_requests_events_id_fk references events,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    requester_id BIGINT NOT NULL CONSTRAINT event_requests_users_id_fk references users,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT pk_event_requests PRIMARY KEY (id)
);
create index event_requests_event_id_index on event_requests (event_id);
create index event_requests_status_index on event_requests (status);

CREATE TABLE IF NOT EXISTS event_comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL CONSTRAINT event_comments_events_id_fk references events,
    author_id BIGINT NOT NULL CONSTRAINT event_comments_users_id_fk references users,
    text VARCHAR(7000),
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT pk_event_comments PRIMARY KEY (id)
);
create index event_comments_id_index on event_comments (id);
create index event_comments_event_id_index on event_comments (event_id);
create index event_comments_author_id_index on event_comments (author_id);
create index event_comments_status_index on event_comments (status);