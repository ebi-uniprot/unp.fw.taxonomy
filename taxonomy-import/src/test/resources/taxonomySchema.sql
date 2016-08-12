CREATE SCHEMA taxonomy;

CREATE TABLE taxonomy.v_public_node
   (    tax_id INTEGER,
        parent_id INTEGER,
        hidden VARCHAR(64),
        internal VARCHAR(64),
        rank VARCHAR(64),
        gc_id VARCHAR(64),
        mgc_id VARCHAR(64),
        ncbi_scientific VARCHAR(64),
        ncbi_common VARCHAR(64),
        sptr_scientific VARCHAR(64),
        sptr_common VARCHAR(64),
        sptr_synonym VARCHAR(64),
        sptr_code VARCHAR(64),
        tax_code VARCHAR(64),
        sptr_ff VARCHAR(64)
   );

CREATE TABLE taxonomy.v_public_merged
   (old_tax_id INTEGER,new_tax_id INTEGER);

CREATE TABLE taxonomy.v_public_deleted
   (tax_id INTEGER);