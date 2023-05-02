CREATE TABLE brands
(
    brand_id SERIAL PRIMARY KEY,
    name     varchar(255) not null
);

CREATE TABLE categories
(
    category_id SERIAL PRIMARY KEY,
    name        varchar(255) not null
);

CREATE TABLE models
(
    model_id        SERIAL PRIMARY KEY,
    name            varchar(255) not null,
    production_year integer      not null,
    brand_ref       bigint
        REFERENCES brands (brand_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE cars
(
    car_id    SERIAL PRIMARY KEY,
    number    varchar(255) UNIQUE not null,
    model_ref bigint
        REFERENCES models (model_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE category_model
(
    category_ref INT REFERENCES categories (category_id) ON UPDATE CASCADE ON DELETE CASCADE,
    model_ref    INT REFERENCES models (model_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT category_model_pk PRIMARY KEY (category_ref, model_ref)
);

ALTER TABLE models ADD CONSTRAINT unique_model_info UNIQUE (name, production_year, brand_ref);