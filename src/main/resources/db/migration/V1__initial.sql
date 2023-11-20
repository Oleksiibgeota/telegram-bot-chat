create or replace function now_utc() returns timestamp without time zone as
$$
select now() at time zone 'utc';
$$ language sql;


CREATE TABLE IF NOT EXISTS users
(
    id         bigserial primary key NOT NULL,
    uuid       varchar(36) UNIQUE    NOT NULL,
    "password" varchar(128)          NOT NULL,
    email      varchar(254)          NOT NULL,
    is_active  boolean               NOT NULL

);

CREATE UNIQUE INDEX unq_common_email_idx on users (LOWER(email));

CREATE TYPE role_type_enum AS ENUM ('ADMIN', 'USER', 'DEVELOPER');

CREATE TABLE IF NOT EXISTS roles
(
    id          serial primary key    NOT NULL,
    role_type   role_type_enum UNIQUE NOT NULL,
    description varchar(160)
);

INSERT INTO roles(role_type)
VALUES ('ADMIN'),
       ('USER'),
       ('DEVELOPER');

CREATE TABLE IF NOT EXISTS roles_user_matcher
(
    id      bigserial primary key NOT NULL,
    role_id integer               NOT NULL,
    user_id integer               NOT NULL,

    CONSTRAINT constraint_unique_role_user_idx UNIQUE (role_id, user_id),
    CONSTRAINT fk_roles_user_matcher_to_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_roles_user_matcher_to_role_id FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS user_session
(
    id           bigserial primary key       NOT NULL,
    session_uuid varchar(36)                 NOT NULL,
    ip           varchar(16),
    user_id      integer                     NOT NULL,
    created_at   timestamp without time zone NOT NULL DEFAULT now_utc(),
    expired_at   timestamp without time zone NOT NULL,
    CONSTRAINT unq_user_session_uuid_idx UNIQUE (session_uuid),
    CONSTRAINT fk_user_session_user_id_to_user_id
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS telegram_user
(
    id          bigserial primary key       NOT NULL,
    external_id bigint UNIQUE               NOT NULL,
    first_name  varchar,
    last_name   varchar,
    user_name   varchar,
    created_at  timestamp without time zone NOT NULL DEFAULT now_utc()
);

CREATE TABLE IF NOT EXISTS chat_dialog
(
    id               bigserial primary key       NOT NULL,
    telegram_user_id bigint                      NOT NULL,
    user_request     varchar                     NOT NULL,
    bot_response     varchar,
    created_at       timestamp without time zone NOT NULL DEFAULT now_utc(),
    CONSTRAINT fk_chat_dialog_to_telegram_user_id
        FOREIGN KEY (telegram_user_id) REFERENCES telegram_user (id)
);
