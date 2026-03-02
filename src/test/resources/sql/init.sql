CREATE TABLE IF NOT EXISTS tiny_url (
                          id   BIGSERIAL PRIMARY KEY,
                          url  VARCHAR(255) NOT NULL UNIQUE,
                          tiny VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS expire_url (
                            id         BIGINT PRIMARY KEY,
                            expired_at TIMESTAMP NOT NULL,
                            CONSTRAINT fk_expire_url_tiny_url
                                FOREIGN KEY (id) REFERENCES tiny_url (id)
                                    ON DELETE CASCADE
);
