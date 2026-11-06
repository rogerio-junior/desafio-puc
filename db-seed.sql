-- =============================================================================
-- Seed de desenvolvimento/testes. Todos os dados são fictícios gerados por IA.
-- Pode ser reexecutado: apaga e recria apenas os próprios funcionários.
--
-- Como ler os dados:
--   - Cada funcionário é um cenário de SalaryServiceUTest.buildScenarios aplicado
--     a um tipo de contrato (12 cenários x 4 tipos = 48 funcionários ativos).
--   - Primeiro nome = cenário; sobrenome = tipo. Ex.: "Lúcia Costa" é o cenário 11
--     para um estagiário.
--   - CPF = <tipo 1-4> + 00000000 + <cenário 01-12>. Ex.: 30000000011.
--
--   Tipo                          Sobrenome  1º dígito do CPF
--   A - Tempo indeterminado       Silva      1
--   B - Tempo determinado         Souza      2
--   C - Estagiário                Costa      3
--   D - Bolsista                  Lima       4
--
-- =============================================================================
-- 1. CÁLCULO DE SALÁRIOS
-- -----------------------------------------------------------------------------
-- Data de referência: qualquer dia entre 2026-11-01 e hoje (competência 2026-10).
-- Salário base 1000.00 para todos, exceto o cenário 12 (1000.50).
-- Idade completada até o fim da competência (31/10/2026).
--
--   #   Nome    Cenário                                 A        B        C e D
--   01  Alex    30 anos, aniversário em out, N          1200.00  1000.00  1000.00
--   02  Ariel   59 anos, N                              1000.00  1000.00  1000.00
--   03  Carlos  66 anos, M                              1030.00  1030.00  1000.00
--   04  Diana   61 anos, F                              1030.00  1030.00  1000.00
--   05  Eli     70 anos, N (sem bônus idoso)            1000.00  1000.00  1000.00
--   06  Fábio   64 anos, M                              1000.00  1000.00  1000.00
--   07  Gustavo 65 anos, M (precisa ser > 65)           1000.00  1000.00  1000.00
--   08  Helena  60 anos, F (precisa ser > 60)           1000.00  1000.00  1000.00
--   09  Isabel  59 anos, F                              1000.00  1000.00  1000.00
--   10  João    23 anos, M, 3 dias de fim de semana     1000.00  1000.00  1120.00
--   11  Lúcia   61 anos, F, aniversário em out, 3 fds   1230.00  1030.00  1120.00
--   12  Marta   61 anos, F, base 1000.50 (arredonda)    1030.52  1030.52  1000.50
--
-- Resultado esperado: 48 calculados, soma 49122.04.
-- Nina Silva (10000000099) é inativa e aniversariante em outubro: não é calculada.
--
-- =============================================================================
-- 2. HISTÓRICO DE PAGAMENTOS POR CPF
-- -----------------------------------------------------------------------------
--   10000000001  Alex Silva   out/2025 a set/2026 (12 meses, 1200.00 em out/2025)
--   20000000003  Carlos Souza jul/2026 a set/2026 (como todos os outros ativos)
--   10000000099  Nina Silva   inativa, sem pagamentos (lista vazia)
--   92572993848  —            CPF inexistente (funcionário não encontrado)
--
-- =============================================================================
-- 3. EXPORTAÇÃO CSV DE UM MÊS
-- -----------------------------------------------------------------------------
--   2026-09  48 linhas (todos os ativos)
--   2026-10   0 linhas antes do cálculo; 48 depois
--   2025-10   1 linha (Alex Silva)
--
-- =============================================================================
-- 4. FUNCIONÁRIOS ATIVOS POR CEP
-- -----------------------------------------------------------------------------
--   22451-900  12 (Silva)    01310-100  12 (Souza)
--   30130-010  12 (Costa)    20040-030  12 (Lima)
--   20050-091   0 (só Nina, que é inativa)
-- =============================================================================

BEGIN;

CREATE TEMP TABLE seed_employee (LIKE employee) ON COMMIT DROP;

INSERT INTO seed_employee
    (id_social_number, first_name, last_name, birth_date, gender, active, base_salary, address, type)
SELECT t.digit || '00000000' || s.num, s.first_name, t.last_name, s.birth_date, s.gender, TRUE, s.base_salary, t.address, t.type
FROM (VALUES
    ('01', 'Alex',    DATE '1996-10-15', 'N', 1000.00),
    ('02', 'Ariel',   DATE '1967-05-10', 'N', 1000.00),
    ('03', 'Carlos',  DATE '1960-05-20', 'M', 1000.00),
    ('04', 'Diana',   DATE '1965-03-08', 'F', 1000.00),
    ('05', 'Eli',     DATE '1956-04-12', 'N', 1000.00),
    ('06', 'Fábio',   DATE '1962-06-15', 'M', 1000.00),
    ('07', 'Gustavo', DATE '1961-07-22', 'M', 1000.00),
    ('08', 'Helena',  DATE '1966-08-30', 'F', 1000.00),
    ('09', 'Isabel',  DATE '1967-01-18', 'F', 1000.00),
    ('10', 'João',    DATE '2003-02-14', 'M', 1000.00),
    ('11', 'Lúcia',   DATE '1965-10-25', 'F', 1000.00),
    ('12', 'Marta',   DATE '1965-09-05', 'F', 1000.50)
) AS s (num, first_name, birth_date, gender, base_salary)
CROSS JOIN (VALUES
    ('1', 'A', 'Silva', 'Rua Marquês de São Vicente, 225 - Gávea, Rio de Janeiro - RJ, 22451-900'),
    ('2', 'B', 'Souza', 'Av. Paulista, 1000 - Bela Vista, São Paulo - SP, 01310-100'),
    ('3', 'C', 'Costa', 'Av. Afonso Pena, 500 - Centro, Belo Horizonte - MG, 30130-010'),
    ('4', 'D', 'Lima',  'Rua do Ouvidor, 50 - Centro, Rio de Janeiro - RJ, 20040-030')
) AS t (digit, type, last_name, address);

-- Inativa
INSERT INTO seed_employee
    (id_social_number, first_name, last_name, birth_date, gender, active, base_salary, address, type)
VALUES
    ('10000000099', 'Nina', 'Silva', '1990-10-02', 'F', FALSE, 1000.00, 'Rua Uruguaiana, 94 - Centro, Rio de Janeiro - RJ, 20050-091', 'A');

-- Remove o resultado de execuções anteriores (somente dos CPFs deste seed)
DELETE FROM attendance WHERE id_employee IN (SELECT id_social_number FROM seed_employee);
DELETE FROM payment    WHERE id_employee IN (SELECT id_social_number FROM seed_employee);
DELETE FROM employee   WHERE id_social_number IN (SELECT id_social_number FROM seed_employee);

INSERT INTO employee SELECT * FROM seed_employee;

-- Pagamentos de jul-set/2026 para todos os ativos, iguais ao salário base
INSERT INTO payment (id_employee, month, year, payment)
SELECT e.id_social_number, EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), e.base_salary
FROM seed_employee e
CROSS JOIN generate_series(DATE '2026-07-01', DATE '2026-09-01', INTERVAL '1 month') AS m
WHERE e.active;

-- Alex Silva: histórico mais longo, out/2025 a jun/2026 (com bônus de aniversário em out/2025)
INSERT INTO payment (id_employee, month, year, payment)
SELECT '10000000001', EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m),
       CASE WHEN EXTRACT(MONTH FROM m) = 10 THEN 1200.00 ELSE 1000.00 END
FROM generate_series(DATE '2025-10-01', DATE '2026-06-01', INTERVAL '1 month') AS m;

-- Presenças: cenários 10 e 11 trabalharam 3 dias de fim de semana em out/2026
-- (sáb 03, dom 04 e sáb 10). Só contam para estagiários (C) e bolsistas (D).
INSERT INTO attendance (id_employee, entrance)
SELECT e.id_social_number, d
FROM seed_employee e
CROSS JOIN (VALUES
    (TIMESTAMP '2026-10-03 09:00:00'),
    (TIMESTAMP '2026-10-04 09:00:00'),
    (TIMESTAMP '2026-10-10 09:00:00')
) AS a (d)
WHERE RIGHT(e.id_social_number, 2) IN ('10', '11') AND e.active;

COMMIT;
