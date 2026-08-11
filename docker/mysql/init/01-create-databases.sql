-- Runs once, on the very first start of an empty mysql-data volume.
--
-- Creates the empty schemas both backends expect. No table DDL and no rows
-- here on purpose: schema creation and row seeding are owned by the
-- applications themselves —
--   Java   : spring.jpa.hibernate.ddl-auto=update  +  com.fleman.config.DataSeeder
--   .NET   : db.Database.Migrate()                 +  FlemanApi.Data.DataSeeder
-- Each seeder is a no-op when its states table already has rows, so restarts
-- never duplicate the fixture data.

CREATE DATABASE IF NOT EXISTS fleetmanagement_dev
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fleetmanagement_dotnet_dev
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Available for the prod/non-dev profiles without a second compose file.
CREATE DATABASE IF NOT EXISTS fleetmanagement_prod
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fleetmanagement_dotnet
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
