CREATE TABLE users (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    uuid UUID NOT NULL,
    login VARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL
);

CREATE TABLE email_status (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    user_id INTEGER NOT NULL,
    confirm_status BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE password_status (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    user_id INTEGER NOT NULL,
    new_password_uuid UUID NOT NULL,
    new_password VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE tokens (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    user_id INTEGER NOT NULL,
    authentication_token VARCHAR(256) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_details (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    user_id INTEGER NOT NULL,
    user_photo VARCHAR(85),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE friends (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    user_id INTEGER NOT NULL,
    friend_user_id INTEGER NOT NULL,
    friend_status SMALLINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_user_id) REFERENCES users (id)
);

CREATE TABLE messages (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    user_id INTEGER NOT NULL,
    text TEXT NOT NULL,
    date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE users_from_dialogs (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    dialog_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (dialog_id) REFERENCES dialogs (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE messages_from_dialogs (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    dialog_id INTEGER NOT NULL,
    message_id INTEGER NOT NULL,
    FOREIGN KEY (dialog_id) REFERENCES dialogs (id),
    FOREIGN KEY (message_id) REFERENCES messages (id)
);

CREATE TABLE dialogs (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    dialog_photo VARCHAR(85),
    dialog_name VARCHAR(100),
    last_message_id INTEGER NOT NULL,
    FOREIGN KEY (last_message_id) REFERENCES messages (id)
);

CREATE TABLE blocked_users (
    id INTEGER NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    user_details_id INTEGER NOT NULL,
    blocked_user_id INTEGER NOT NULL,
    FOREIGN KEY (user_details_id) REFERENCES user_details (id),
    FOREIGN KEY (blocked_user_id) REFERENCES users (id)
);

DROP TABLE users;
DROP TABLE email_status;
DROP TABLE password_status;
DROP TABLE tokens;
DROP TABLE user_details;
DROP TABLE friends;
DROP TABLE messages;
DROP TABLE users_from_dialogs;
DROP TABLE messages_from_dialogs;
DROP TABLE dialogs;
DROP TABLE blocked_users;

SELECT * FROM users;
SELECT * FROM email_status;
SELECT * FROM password_status;
SELECT * FROM tokens;
SELECT * FROM user_details;
SELECT * FROM friends;
SELECT * FROM messages;
SELECT * FROM users_from_dialogs;
SELECT * FROM messages_from_dialogs;
SELECT * FROM dialogs;
SELECT * FROM blocked_users;