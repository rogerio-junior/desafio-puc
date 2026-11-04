CREATE TABLE employee (
    id_social_number VARCHAR(11) PRIMARY KEY, -- Varchar para armazenar CPFs que começam com 0
    first_name       VARCHAR(2000) NOT NULL,
    last_name        VARCHAR(2000) NOT NULL,
    birth_date       DATE          NOT NULL,
    gender           VARCHAR(1)    NOT NULL CHECK (gender IN ('M', 'F', 'N')),
    active           BOOLEAN       NOT NULL,
    base_salary      NUMERIC(6,2)  NOT NULL,
    address          VARCHAR(4000),
    type             VARCHAR(10)   NOT NULL CHECK (type IN ('A', 'B', 'C', 'D'))
);

CREATE TABLE payment (
    id_employee VARCHAR(11) NOT NULL,
    month NUMERIC(2,0) NOT NULL CHECK (month BETWEEN 1 AND 12),
    year NUMERIC (4,0) NOT NULL CHECK (year BETWEEN 1000 AND 9999),
    payment NUMERIC(6,2) NOT NULL,

    PRIMARY KEY (id_employee, month, year),
    FOREIGN KEY (id_employee) REFERENCES employee (id_social_number)
);


CREATE TABLE attendance (
    id_employee VARCHAR(11) NOT NULL,
    entrance TIMESTAMP NOT NULL,

    PRIMARY KEY (id_employee, entrance),
    FOREIGN KEY (id_employee) REFERENCES employee (id_social_number)
);