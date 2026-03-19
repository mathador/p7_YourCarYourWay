-- Ensure users.is_psh_profile exists and is safe for existing rows
ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS is_psh_profile boolean;

UPDATE users
SET is_psh_profile = false
WHERE is_psh_profile IS NULL;

ALTER TABLE IF EXISTS users
    ALTER COLUMN is_psh_profile SET DEFAULT false;

ALTER TABLE IF EXISTS users
    ALTER COLUMN is_psh_profile SET NOT NULL;

