-- =============================================================================
-- Seed de desenvolvimento/testes. Todos os dados são fictícios.
--
-- Fica fora de db/migration de propósito: o Flyway não deve executá-lo.
-- Pré-requisito: as tabelas já criadas (basta subir a aplicação uma vez).
--
-- Execução, a partir da raiz do projeto, com o banco no ar:
--   docker compose exec -T db psql -U payment -d puc_challenge -v ON_ERROR_STOP=1 < backend/db/seed.sql
--
-- Pode ser reexecutado: apaga e recria apenas os CPFs listados aqui, inclusive
-- pagamentos calculados pelo sistema para eles. Outros funcionários não são tocados.
--
-- -----------------------------------------------------------------------------
-- CENÁRIO DE TESTE: data de referência 2026-11 => mês de competência 2026-10
-- Fins de semana de out/2026: 3, 4, 10, 11, 17, 18, 24, 25 e 31.
-- Resultado esperado: 24 funcionários calculados.
--
-- CPF          Nome       Tipo Esperado Motivo
-- 09530965214  Ana        A     5000.00 sem bônus; histórico de 24 meses
-- 67841163720  Bruno      A     4700.00 aniversário em outubro (+200)
-- 22446173942  Carlos     A     4120.00 homem, 66 anos (+3%)
-- 34678777881  Daniela    A     3090.00 mulher, 61 anos (+3%)
-- 39815794370  Eduardo    A     4320.00 aniversário (+200) e 71 anos (+3%)
-- 53555489976  Fernanda   A     3200.00 aniversário; completa só 60 anos em 31/10 (sem 3%)
-- 63494263710  Gustavo    A     3500.00 homem com exatos 65 anos (sem 3%)
-- 02171652659  Helena     A     2775.00 completa 61 anos em 31/10 (+200 e +3%)
-- 24485805107  Iara       A     3800.00 gênero N, 76 anos (regra não define bônus)
-- 41589854632  João       A     3200.00 faz 66 anos só em novembro (sem bônus)
-- 00699618673  Lucas      A     4200.00 foi em fins de semana, mas tipo A não recebe
-- 69724626067  Mariana    B     3500.00 aniversário não vale para tipo B
-- 92491248131  Nelson     B     3090.00 homem, 68 anos (+3%)
-- 58148543593  Olívia     B     2884.00 mulher, 62 anos (+3%)
-- 62927124604  Paulo      B     2750.50 sem bônus; salário com centavos
-- 87537661219  Rafaela    B     3433.33 +3% com arredondamento (3433,3299)
-- 66489888602  Sofia      C     1360.00 4 dias de fim de semana
-- 74418966498  Tiago      C     1200.00 só dias úteis; sem histórico de pagamentos
-- 07906652045  Úrsula     C     1100.00 aniversário não vale para tipo C; sem presença
-- 49113806912  Vicente    C     1040.00 71 anos (sem 3%); só 31/10 23:30 conta no mês
-- 40333348745  Wagner     D     1860.00 todos os 9 dias de fim de semana
-- 94068119368  Yasmin     D      940.00 2 entradas no mesmo sábado contam 1 dia
-- 03042832406  Zélia      D      800.00 70 anos (sem 3%); sem presença
-- 48132432070  Álvaro     D     1000.00 fins de semana apenas fora do mês de competência
--
-- Não devem ser calculados:
-- 17205315875  Denise     A    1234.56 já tem pagamento em 2026-10 (não pode ser sobrescrito)
-- 93330563435  Beatriz    A            inativa
-- 73355519600  Caio       D            inativo, com presença em fim de semana
--
-- Outros testes:
-- - CPF inexistente (válido): 92572993848
-- - Consulta por CPF: Ana tem 24 pagamentos anteriores, e os outros têm 3 (jul-set/2026);
--   Tiago não tem nenhum; Denise tem 4 e os inativos têm 3 (jan-mar/2026)
-- - CSV e ordenação: nomes acentuados (Álvaro, Úrsula), apóstrofo (Sant'Anna) e vírgula (Souza, Jr.)
-- - CPF com zero à esquerda: 09530965214, 02171652659, 00699618673, 07906652045, 03042832406
--
-- CEPs de funcionários ativos: 21 ocorrências, 20 distintos.
-- - 22451-900 aparece em 2 funcionários (Ana e Bruno)
-- - João tem 2 CEPs no endereço; Daniela tem o CEP no início do texto
-- - Não devem gerar CEP: Eduardo (endereço NULL), Fernanda (sem CEP), Gustavo (50050000,
--   sem hífen), Helena (4011-300, curto), Iara (123456-7890, dígitos a mais)
-- - Não devem aparecer, pois são de inativos: 20040-030 e 20050-008
-- =============================================================================

BEGIN;

CREATE TEMP TABLE seed_employee (LIKE employee) ON COMMIT DROP;

INSERT INTO seed_employee
    (id_social_number, first_name, last_name, birth_date, gender, active, base_salary, address, type)
VALUES
    -- A: contrato por tempo indeterminado
    ('09530965214', 'Ana Beatriz', 'Almeida',          '1985-03-12', 'F', TRUE, 5000.00, 'Rua Marquês de São Vicente, 225 - Gávea, Rio de Janeiro - RJ, 22451-900', 'A'),
    ('67841163720', 'Bruno',       'Carvalho',         '1990-10-15', 'M', TRUE, 4500.00, 'Rua Marquês de São Vicente, 124, apto 302 - Gávea, Rio de Janeiro - RJ, 22451-900', 'A'),
    ('22446173942', 'Carlos',      'Dias',             '1960-05-20', 'M', TRUE, 4000.00, 'Av. Paulista, 1000 - Bela Vista, São Paulo - SP, CEP 01310-100', 'A'),
    ('34678777881', 'Daniela',     'Esteves',          '1965-02-10', 'F', TRUE, 3000.00, '30130-010 - Av. Afonso Pena, 500, Belo Horizonte - MG', 'A'),
    ('39815794370', 'Eduardo',     'Farias',           '1955-10-05', 'M', TRUE, 4000.00, NULL, 'A'),
    ('53555489976', 'Fernanda',    'Gomes',            '1966-10-31', 'F', TRUE, 3000.00, 'Rua das Flores, 45 - Centro, Curitiba - PR', 'A'),
    ('63494263710', 'Gustavo',     'Lima',             '1961-01-15', 'M', TRUE, 3500.00, 'Rua da Aurora, 300 - Boa Vista, Recife - PE, 50050000', 'A'),
    ('02171652659', 'Helena',      'Martins',          '1965-10-31', 'F', TRUE, 2500.00, 'Av. Sete de Setembro, 1500, Salvador - BA, CEP 4011-300', 'A'),
    ('24485805107', 'Iara',        'Nogueira',         '1950-06-01', 'N', TRUE, 3800.00, 'Travessa 123456-7890, casa 2, Manaus - AM', 'A'),
    ('41589854632', 'João Pedro',  'Oliveira',         '1960-11-10', 'M', TRUE, 3200.00, 'Residencial: Rua A, 10, Niterói - RJ, 24020-125 / Correspondência: Rua B, 20, Niterói - RJ, 24210-200', 'A'),
    ('00699618673', 'Lucas',       'Queiroz',          '1992-07-07', 'M', TRUE, 4200.00, 'Rua Voluntários da Pátria, 89 - Botafogo, Rio de Janeiro - RJ, 22270-000', 'A'),
    ('17205315875', 'Denise',      'Barros',           '1987-06-18', 'F', TRUE, 3000.00, 'Rua Uruguaiana, 94 - Centro, Rio de Janeiro - RJ, 20050-091', 'A'),
    ('93330563435', 'Beatriz',     'Costa',            '1980-10-02', 'F', FALSE, 3500.00, 'Rua do Ouvidor, 50 - Centro, Rio de Janeiro - RJ, 20040-030', 'A'),

    -- B: contrato a prazo
    ('69724626067', 'Mariana',     'Ribeiro',          '1995-10-20', 'F', TRUE, 3500.00, 'Rua Visconde de Pirajá, 330 - Ipanema, Rio de Janeiro - RJ, 22410-000', 'B'),
    ('92491248131', 'Nelson',      'Souza, Jr.',       '1958-08-08', 'M', TRUE, 3000.00, 'Av. Beira Mar, 200 - Meireles, Fortaleza - CE, 60165-121', 'B'),
    ('58148543593', 'Olívia',      'Teixeira',         '1964-04-04', 'F', TRUE, 2800.00, 'Rua dos Andradas, 1000 - Centro Histórico, Porto Alegre - RS, 90020-008', 'B'),
    ('62927124604', 'Paulo',       'Vieira',           '1980-01-25', 'M', TRUE, 2750.50, 'SQS 308, Bloco A, apto 101 - Asa Sul, Brasília - DF, 70355-010', 'B'),
    ('87537661219', 'Rafaela',     'Sant''Anna',       '1963-12-01', 'F', TRUE, 3333.33, 'Rua XV de Novembro, 700 - Centro, Curitiba - PR, 80020-310', 'B'),

    -- C: estagiário
    ('66489888602', 'Sofia',       'Andrade',          '2003-02-14', 'F', TRUE, 1200.00, 'Rua Jardim Botânico, 600 - Jardim Botânico, Rio de Janeiro - RJ, 22461-000', 'C'),
    ('74418966498', 'Tiago',       'Moreira',          '2004-08-22', 'M', TRUE, 1200.00, 'Rua Augusta, 1500 - Consolação, São Paulo - SP, 01304-001', 'C'),
    ('07906652045', 'Úrsula',      'Pires',            '2002-10-09', 'F', TRUE, 1100.00, 'Rua Conde de Bonfim, 400 - Tijuca, Rio de Janeiro - RJ, 20520-054', 'C'),
    ('49113806912', 'Vicente',     'Rocha',            '1955-03-30', 'M', TRUE, 1000.00, 'Rua do Catete, 311 - Catete, Rio de Janeiro - RJ, 22220-001', 'C'),

    -- D: bolsista
    ('40333348745', 'Wagner',      'Santos',           '2000-05-05', 'M', TRUE, 1500.00, 'Rua São Clemente, 258 - Botafogo, Rio de Janeiro - RJ, 22260-000', 'D'),
    ('94068119368', 'Yasmin',      'Tavares',          '2001-09-17', 'N', TRUE,  900.00, 'Av. Atlântica, 1702 - Copacabana, Rio de Janeiro - RJ, 22021-001', 'D'),
    ('03042832406', 'Zélia',       'Vasconcelos',      '1956-07-11', 'F', TRUE,  800.00, 'Rua Haddock Lobo, 595 - Cerqueira César, São Paulo - SP, 01414-001', 'D'),
    ('48132432070', 'Álvaro',      'Xavier',           '2001-01-03', 'M', TRUE, 1000.00, 'Av. Rio Branco, 156 - Centro, Rio de Janeiro - RJ, 20040-901', 'D'),
    ('73355519600', 'Caio',        'Duarte',           '1999-04-27', 'M', FALSE, 950.00, 'Rua da Carioca, 10 - Centro, Rio de Janeiro - RJ, 20050-008', 'D');

-- Remove o resultado de execuções anteriores (somente dos CPFs deste seed)
DELETE FROM attendance WHERE id_employee IN (SELECT id_social_number FROM seed_employee);
DELETE FROM payment    WHERE id_employee IN (SELECT id_social_number FROM seed_employee);
DELETE FROM employee   WHERE id_social_number IN (SELECT id_social_number FROM seed_employee);

INSERT INTO employee SELECT * FROM seed_employee;

-- -----------------------------------------------------------------------------
-- Pagamentos anteriores. Valor simplificado: histórico igual ao salário base.
-- O mês 2026-10 fica pendente para ser calculado pelo sistema (exceto Denise).
-- -----------------------------------------------------------------------------

-- Ativos: jul-set/2026 (Tiago não tem histórico)
INSERT INTO payment (id_employee, month, year, payment)
SELECT e.id_social_number, EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), e.base_salary
FROM seed_employee e
CROSS JOIN generate_series(TIMESTAMP '2026-07-01', TIMESTAMP '2026-09-01', INTERVAL '1 month') AS m
WHERE e.active
  AND e.id_social_number <> '74418966498';

-- Ana: completa 24 meses (out/2024 a set/2026), para testar paginação e ordenação
INSERT INTO payment (id_employee, month, year, payment)
SELECT '09530965214', EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), 5000.00
FROM generate_series(TIMESTAMP '2024-10-01', TIMESTAMP '2026-06-01', INTERVAL '1 month') AS m;

-- Inativos: jan-mar/2026
INSERT INTO payment (id_employee, month, year, payment)
SELECT e.id_social_number, EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), e.base_salary
FROM seed_employee e
CROSS JOIN generate_series(TIMESTAMP '2026-01-01', TIMESTAMP '2026-03-01', INTERVAL '1 month') AS m
WHERE NOT e.active;

-- Denise: 2026-10 já pago, com valor diferente do que seria calculado (3000.00)
INSERT INTO payment (id_employee, month, year, payment)
VALUES ('17205315875', 10, 2026, 1234.56);

-- -----------------------------------------------------------------------------
-- Presenças
-- -----------------------------------------------------------------------------
INSERT INTO attendance (id_employee, entrance)
VALUES
    -- Lucas (A): fins de semana não geram bônus
    ('00699618673', '2026-10-10 09:00:00'),
    ('00699618673', '2026-10-11 09:00:00'),

    -- Sofia (C): 4 dias de fim de semana e 3 dias úteis
    ('66489888602', '2026-10-03 08:55:00'),
    ('66489888602', '2026-10-04 09:02:00'),
    ('66489888602', '2026-10-05 08:58:00'),
    ('66489888602', '2026-10-06 09:01:00'),
    ('66489888602', '2026-10-07 08:47:00'),
    ('66489888602', '2026-10-10 09:10:00'),
    ('66489888602', '2026-10-11 09:05:00'),

    -- Tiago (C): só dias úteis
    ('74418966498', '2026-10-05 09:00:00'),
    ('74418966498', '2026-10-06 09:00:00'),
    ('74418966498', '2026-10-07 09:00:00'),
    ('74418966498', '2026-10-08 09:00:00'),
    ('74418966498', '2026-10-09 09:00:00'),

    -- Vicente (C): limites do mês. Só 31/10 (último dia, sábado) conta
    ('49113806912', '2026-09-27 09:00:00'), -- domingo do mês anterior
    ('49113806912', '2026-10-01 00:00:00'), -- quinta, primeiro instante do mês
    ('49113806912', '2026-10-31 23:30:00'), -- sábado, último dia do mês
    ('49113806912', '2026-11-01 00:10:00'), -- domingo do mês seguinte

    -- Wagner (D): todos os fins de semana, incluindo o primeiro instante de 03/10
    ('40333348745', '2026-10-03 00:00:00'),
    ('40333348745', '2026-10-04 10:00:00'),
    ('40333348745', '2026-10-10 10:00:00'),
    ('40333348745', '2026-10-11 10:00:00'),
    ('40333348745', '2026-10-17 10:00:00'),
    ('40333348745', '2026-10-18 10:00:00'),
    ('40333348745', '2026-10-24 10:00:00'),
    ('40333348745', '2026-10-25 10:00:00'),
    ('40333348745', '2026-10-31 10:00:00'),

    -- Yasmin (D): duas entradas no mesmo sábado valem um dia
    ('94068119368', '2026-10-17 08:00:00'),
    ('94068119368', '2026-10-17 13:30:00'),

    -- Álvaro (D): fins de semana apenas fora do mês de competência
    ('48132432070', '2026-09-26 10:00:00'),
    ('48132432070', '2026-09-27 10:00:00'),
    ('48132432070', '2026-10-05 10:00:00'),
    ('48132432070', '2026-10-06 10:00:00'),
    ('48132432070', '2026-11-01 10:00:00'),

    -- Caio (D, inativo): não deve ser calculado
    ('73355519600', '2026-10-03 10:00:00'),
    ('73355519600', '2026-10-04 10:00:00');

COMMIT;
