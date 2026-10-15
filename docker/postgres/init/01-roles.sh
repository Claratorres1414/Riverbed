#!/bin/bash

set -e

psql \
  -v ON_ERROR_STOP=1 \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --set=app_username="$DB_USERNAME" \
  --set=app_password="$DB_PASSWORD" \
  --set=migration_username="$MIGRATION_DB_USERNAME" \
  --set=migration_password="$MIGRATION_DB_PASSWORD" <<'SQL'

CREATE ROLE :"app_username"
    LOGIN
    PASSWORD :'app_password';

CREATE ROLE :"migration_username"
    LOGIN
    PASSWORD :'migration_password';

ALTER SCHEMA public OWNER TO :"migration_username";

GRANT USAGE ON SCHEMA public
TO :"app_username";

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO :"app_username";

GRANT USAGE, SELECT, UPDATE
ON ALL SEQUENCES IN SCHEMA public
TO :"app_username";

ALTER DEFAULT PRIVILEGES
FOR ROLE :"migration_username"
IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE
ON TABLES
TO :"app_username";

ALTER DEFAULT PRIVILEGES
FOR ROLE :"migration_username"
IN SCHEMA public
GRANT USAGE, SELECT, UPDATE
ON SEQUENCES
TO :"app_username";

SQL