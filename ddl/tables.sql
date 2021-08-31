-- Table: gblibrary.associated_word
CREATE TABLE gblibrary.associated_word
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.associated_word_id_seq'::regclass),
    associated_word character varying(50) COLLATE pg_catalog."default",
    word character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT associated_word_pkey PRIMARY KEY (id),
    CONSTRAINT fk7ix7p9sex9uwbs0pc0c15ex0i FOREIGN KEY (word)
        REFERENCES gblibrary.word (word) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkst54ujswkoiddqsounjd5wa30 FOREIGN KEY (associated_word)
        REFERENCES gblibrary.word (word) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: gblibrary.book_info
CREATE TABLE gblibrary.book_info
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.book_info_id_seq'::regclass),
    gb_book_author character varying(500) COLLATE pg_catalog."default",
    gb_book_url character varying(200) COLLATE pg_catalog."default",
    gb_book_id character varying(20) COLLATE pg_catalog."default",
    gb_book_issued timestamp without time zone,
    gb_book_language character varying(10) COLLATE pg_catalog."default",
    gb_book_modified timestamp without time zone,
    gb_book_title character varying(500) COLLATE pg_catalog."default",
    catalog_id bigint,
    CONSTRAINT book_info_pkey PRIMARY KEY (id),
    CONSTRAINT idx_gbid UNIQUE (gb_book_id),
    CONSTRAINT fk52s8sdjtg06k6nf8vjdewhdut FOREIGN KEY (catalog_id)
        REFERENCES gblibrary.catalog (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: gblibrary.book_info_detail
CREATE TABLE gblibrary.book_info_detail
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.book_info_detail_id_seq'::regclass),
    book_id bigint,
    word_count bigint,
    word character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT book_info_detail_pkey PRIMARY KEY (id),
    CONSTRAINT fk5tae2re71j67sd5p1sk713uy9 FOREIGN KEY (book_id)
        REFERENCES gblibrary.book_info (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: gblibrary.book_info_search
CREATE TABLE gblibrary.book_info_search
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.book_info_search_id_seq'::regclass),
    search_limit integer,
    search_dt timestamp without time zone,
    search_type character varying(20) COLLATE pg_catalog."default",
    gb_book_id bigint,
    book_option boolean,
    search_option character varying(20) COLLATE pg_catalog."default",
    status character varying(20) COLLATE pg_catalog."default",
    searched_word character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT book_info_search_pkey PRIMARY KEY (id),
    CONSTRAINT fk3dn0dp3iip0rfatvd0sjny4c4 FOREIGN KEY (searched_word)
        REFERENCES gblibrary.word (word) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkl58x1d6rub1pupj7elbm252cd FOREIGN KEY (gb_book_id)
        REFERENCES gblibrary.book_info (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: gblibrary.book_info_search_result
CREATE TABLE gblibrary.book_info_search_result
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.book_info_search_result_id_seq'::regclass),
    gb_book_search_id bigint,
    returned_book_id bigint,
    returned_word character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT book_info_search_result_pkey PRIMARY KEY (id),
    CONSTRAINT fk9ppdmvco04x3ayh46162avse5 FOREIGN KEY (returned_word)
        REFERENCES gblibrary.word (word) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fki271x572o5xgofypnnkx6t05s FOREIGN KEY (returned_book_id)
        REFERENCES gblibrary.book_info (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkn1jvh3segrk2b3oui43to33hk FOREIGN KEY (gb_book_search_id)
        REFERENCES gblibrary.book_info_search (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: gblibrary.book_search_info
CREATE TABLE gblibrary.book_search_info
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.book_search_info_id_seq'::regclass),
    search_limit integer,
    search_type character varying(20) COLLATE pg_catalog."default",
    searched_word character varying(200) COLLATE pg_catalog."default",
    gb_book_id bigint,
    CONSTRAINT book_search_info_pkey PRIMARY KEY (id),
    CONSTRAINT fksjcevfj3kcmnp9goqf48xf5ir FOREIGN KEY (gb_book_id)
        REFERENCES gblibrary.book_info (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: gblibrary.catalog
CREATE TABLE gblibrary.catalog
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.catalog_id_seq'::regclass),
    last_refresh_dt timestamp without time zone,
    name character varying(50) COLLATE pg_catalog."default",
    url character varying(250) COLLATE pg_catalog."default",
    CONSTRAINT catalog_pkey PRIMARY KEY (id),
    CONSTRAINT idx_name UNIQUE (name)
)

-- Table: gblibrary.ignored_word
CREATE TABLE gblibrary.ignored_word
(
    id bigint NOT NULL DEFAULT nextval('gblibrary.ignored_word_id_seq'::regclass),
    word character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT ignored_word_pkey PRIMARY KEY (id)
)

-- Table: gblibrary.word
CREATE TABLE gblibrary.word
(
    word character varying(50) COLLATE pg_catalog."default" NOT NULL,
    last_search_dt timestamp without time zone,
    search_count integer,
    CONSTRAINT word_pkey PRIMARY KEY (word)
)