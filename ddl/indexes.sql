
-- Index: idx_associated_word
CREATE INDEX idx_associated_word
    ON gblibrary.associated_word USING btree
    (associated_word COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: idx_word
CREATE INDEX idx_word
    ON gblibrary.associated_word USING btree
    (word COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;


-- Index: idx_author
CREATE INDEX idx_author
    ON gblibrary.book_info USING btree
    (gb_book_author COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Index: idx_catalog
CREATE INDEX idx_catalog
    ON gblibrary.book_info USING btree
    (catalog_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Index: idx_title
CREATE INDEX idx_title
    ON gblibrary.book_info USING btree
    (gb_book_title COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;


-- Index: idx_bid_word
CREATE INDEX idx_bid_word
    ON gblibrary.book_info_detail USING btree
    (word COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Index: idx_bookid
CREATE INDEX idx_bookid
    ON gblibrary.book_info_detail USING btree
    (book_id ASC NULLS LAST)
    TABLESPACE pg_default;

