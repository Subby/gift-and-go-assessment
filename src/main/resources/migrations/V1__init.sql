CREATE TABLE record_request
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid          VARCHAR(255) NOT NULL,
    uri           VARCHAR(255) NOT NULL,
    time_stamp    TIMESTAMP    NOT NULL,
    response_code SMALLINT     NOT NULL,
    request_ip    VARCHAR(255) NOT NULL,
    request_isp   VARCHAR(255) NOT NULL,
    country_code  VARCHAR(255) NOT NULL,
    time_lapsed   INTEGER      NOT NULL
);
