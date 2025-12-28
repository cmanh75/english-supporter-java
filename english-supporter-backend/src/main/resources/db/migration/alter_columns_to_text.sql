-- Migration script to alter columns to TEXT type for MariaDB
-- Run this script if you have existing data and need to change column types

-- Alter meanings table
ALTER TABLE meanings MODIFY COLUMN meaning TEXT NOT NULL;

-- Alter engdefs table
ALTER TABLE engdefs MODIFY COLUMN definition TEXT NOT NULL;

-- Alter examples table
ALTER TABLE examples MODIFY COLUMN example TEXT NOT NULL;

-- Alter categories table (increase length)
ALTER TABLE categories MODIFY COLUMN category VARCHAR(500) NOT NULL;

-- Alter words table (set appropriate lengths)
ALTER TABLE words MODIFY COLUMN text VARCHAR(255) NOT NULL;
ALTER TABLE words MODIFY COLUMN type VARCHAR(100) NOT NULL;
ALTER TABLE words MODIFY COLUMN pronunciation VARCHAR(255) NOT NULL;

