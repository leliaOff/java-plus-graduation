DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS event_compilations CASCADE;

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
    initiator_id BIGINT NOT NULL,
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