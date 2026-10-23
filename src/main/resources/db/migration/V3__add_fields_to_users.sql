-- Adiciona campos necessários para o cadastro de jogadores
ALTER TABLE users
    ADD COLUMN name VARCHAR(150),
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN password VARCHAR(255);

-- Torna o campo username opcional para compatibilidade com novos cadastros de jogadores
ALTER TABLE users
    ALTER COLUMN username DROP NOT NULL;

-- Garante a unicidade do e-mail entre os jogadores cadastrados (RF04)
ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email);
