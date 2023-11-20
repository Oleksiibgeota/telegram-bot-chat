CREATE SCHEMA chat_bot_local_db;
CREATE USER bestchat WITH ENCRYPTED PASSWORD 'bestchat';
ALTER USER bestchat WITH SUPERUSER;
GRANT ALL PRIVILEGES ON DATABASE chat_bot_local_db TO bestchat;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";