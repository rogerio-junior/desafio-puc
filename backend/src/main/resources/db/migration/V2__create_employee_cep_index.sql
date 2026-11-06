-- Função para extrair o CEP de um texto (endereço)
-- Considera-se que há no máximo um CEP por campo address, por ser singular
-- Se tiver mais de um, considera o primeiro. Se não tiver nenhum, devolve NULL.
CREATE FUNCTION extract_cep(address TEXT) RETURNS TEXT
LANGUAGE sql IMMUTABLE PARALLEL SAFE AS $$
    SELECT (regexp_match(address, '(?<![0-9])[0-9]{5}-[0-9]{3}(?![0-9])'))[1] -- Primeiro grupo do primeiro match
$$;

-- A própria função é usada para gerar um índice e deixar a busca por CEP mais rápida
-- O que permite isso é a função ser determinística (o mesmo input sempre gera a mesma saída)
-- Para usar o índice na busca, precisa usar a mesma expressão declarada. Exemplo: extract_cep(address) = :cep
CREATE INDEX idx_employee_cep ON employee (extract_cep(address));
