-- =============================================================================
-- Seed de desenvolvimento/testes. Todos os dados são fictícios gerados por IA.
-- Esse script pode ser reexecutado, ele apaga e recria apenas os próprios cenários.
--
-- =============================================================================
-- 1. CÁLCULO DE SALÁRIOS
-- -----------------------------------------------------------------------------
-- Três cenários independentes (podem ser executados em qualquer ordem). Use o
-- dia 01 como data de referência: assim P1 coincide com "idade na data de
-- referência" e o resultado não depende do dia escolhido.
--
--   Cenário    Data de referência  Competência  Calculados  Soma dos valores
--   OUT/2026   2026-11-01          2026-10      34          108657.82
--   DEZ/2025   2026-01-01          2025-12      34          105332.82  (virada de ano)
--   FEV/2026   2026-03-01          2026-02      33          101382.82  (mês de 28 dias)
--
-- Fins de semana: out/2026: 3,4,10,11,17,18,24,25,31 | dez/2025: 6,7,13,14,20,21,27,28
--                 fev/2026: 1,7,8,14,15,21,22,28
--
-- CPF          Nome        Tipo Sexo  OUT/2026  DEZ/2025  FEV/2026  Motivo
-- 09530965214  Ana         A    F      5000.00   já pago   já pago  sem bônus; histórico de 24 meses
-- 67841163720  Bruno       A    M      4700.00   4500.00   4500.00  aniversário em outubro (+200)
-- 22446173942  Carlos      A    M      4120.00   4000.00   4000.00  faz 66 em maio/2026 (+3% só em out)
-- 34678777881  Daniela     A    F      3090.00   3000.00   3290.00  faz 61 em 10/02/2026 (fev: +200 e +3%)
-- 39815794370  Eduardo     A    M      4320.00   4120.00   4120.00  aniversário em out (+200) e 70+ anos (+3%)
-- 53555489976  Fernanda    A    F      3200.00   3000.00   3000.00  aniversário; completa só 60 em 31/10 (sem 3%)
-- 63494263710  Gustavo     A    M      3500.00   3500.00   3500.00  homem com 65 anos (sem 3%)
-- 02171652659  Helena      A    F      2775.00   2500.00   2500.00  completa 61 no último dia do mês (+200 e +3%)
-- 24485805107  Iara        A    N      3800.00   3800.00   3800.00  gênero N, 76 anos (P2: sem 3%)
-- 41589854632  João Pedro  A    M      3200.00   3200.00   3200.00  faz 66 só em 10/11/2026 (sem 3%)
-- 00699618673  Lucas       A    M      4200.00   4200.00   já pago  ex-estagiário; fim de semana não vale para A
-- 52601815906  Marcos      A    M      3600.00   3600.00   3600.00  homem com 63 anos (limite feminino não vale)
-- 08301661305  Roberto     A    M      9785.00   9785.00   9785.00  salário alto com +3% (cabe em NUMERIC(6,2))
-- 18609139034  Henrique    A    M      4000.00   4200.00   4000.00  aniversário em dezembro (virada de ano)
-- 99603082430  Gabriel     A    M      3000.00   3000.00   3200.00  nasceu em 29/02 (P5)
-- 17205315875  Denise      A    F      já pago   3000.00   3000.00  out/2026 já pago com 1234.56 (P6)
-- 69724626067  Mariana     B    F      3500.00   3500.00   3500.00  aniversário e fim de semana não valem para B
-- 92491248131  Nelson      B    M      3090.00   3090.00   3090.00  homem, 68 anos (+3%)
-- 58148543593  Olívia      B    F      2884.00   2884.00   2884.00  mulher, 61/62 anos (+3%)
-- 62927124604  Paulo       B    M      2750.50   2750.50   2750.50  sem bônus; salário com centavos
-- 87537661219  Rafaela     B    F      3433.33   3433.33   3433.33  +3% com arredondamento (3433.3299)
-- 62819482112  Natália     B    F      3090.00   3090.00   3090.00  aniversário em out não vale para B; só +3%
-- 99351819019  Kim         B    N      3000.00   3000.00   3000.00  gênero N, 70+ anos (P2: sem 3%)
-- 93786579741  Larissa     B    F      9999.99   9999.99   9999.99  salário base máximo de NUMERIC(6,2)
-- 54323194897  Otávio      B    M      3090.00   3090.00   3090.00  completa 66 em 15/12/2025 (+3% já em dez)
-- 66489888602  Sofia       C    F      1360.00   1320.00   1200.00  out: 4 dias de fds; dez: 3 dias
-- 74418966498  Tiago       C    M      1200.00   1200.00   1200.00  só dias úteis; sem histórico de pagamentos
-- 07906652045  Úrsula      C    F      1100.00   1100.00   1100.00  aniversário em out não vale para C; sem presença
-- 49113806912  Vicente     C    M      1040.00   1040.00   1000.00  71 anos (sem 3%); limites do mês (ver presenças)
-- 75749118606  Maria José  C    F      1150.00   1150.00   1150.00  aspas no nome (CSV)
-- 40333348745  Wagner      D    M      1860.00   1580.00   1580.00  out: 9 dias; dez/fev: 2 dias + vizinhos fora do mês
-- 94068119368  Yasmin      D    N       940.00    900.00    980.00  2 entradas no mesmo dia contam 1
-- 03042832406  Zélia       D    F       800.00    800.00    800.00  70 anos (sem 3%); sem presença
-- 48132432070  Álvaro      D    M      1000.00   1000.00   1000.00  fins de semana só fora do mês de competência
-- 25276018987  Igor        D    M      1080.00   1000.00   1040.00  aniversário em out não vale para D; 2 dias de fds
--
-- Nunca devem ser calculados (inativos):
-- 93330563435  Beatriz     A    F   inativa, aniversário em outubro
-- 73355519600  Caio        D    M   inativo, com presença em fim de semana de out/2026
--
-- Erros esperados na data de referência: data futura, formato inválido, ausente.
--
-- =============================================================================
-- 2. HISTÓRICO DE PAGAMENTOS POR CPF (antes de qualquer cálculo)
-- -----------------------------------------------------------------------------
-- - Ana (09530965214): 24 pagamentos, out/2024 a set/2026, atravessando 3 anos.
--   4500.00 até dez/2025, 5000.00 a partir de jan/2026, com +200 nos meses de março.
-- - Lucas (00699618673): 9 pagamentos; jan-jun/2026 como estagiário (1200.00) e
--   jul-set/2026 já como tipo A (4200.00). O tipo é o "tipo atual" do funcionário.
-- - Denise (17205315875): 4 pagamentos (jul-out/2026).
-- - Caio (73355519600, inativo): 9 pagamentos (jan-set/2026).
-- - Beatriz (93330563435, inativa): 3 pagamentos (jan-mar/2026).
-- - Tiago (74418966498): existe, mas não tem pagamentos (lista vazia, não é erro).
-- - Demais ativos: 3 pagamentos (jul-set/2026), iguais ao salário base.
-- - CPF válido inexistente: 92572993848 (funcionário não encontrado).
-- - CPF com zero à esquerda: 09530965214, 02171652659, 00699618673, 07906652045,
--   03042832406, 08301661305.
--
-- =============================================================================
-- 3. EXPORTAÇÃO CSV DE UM MÊS/ANO (antes de qualquer cálculo)
-- -----------------------------------------------------------------------------
--   Mês       Linhas  Observação
--   2026-09   35      34 ativos (todos menos Tiago) + Caio, inativo mas pago no mês
--   2026-10    1      apenas Denise; após o cenário OUT/2026, 35 linhas
--   2026-03    4      Ana (5200.00), Lucas, Beatriz e Caio
--   2024-09    0      só o cabeçalho
-- Caracteres que exigem escape/codificação: vírgula (Nelson "Souza, Jr."),
-- apóstrofo (Rafaela "Sant'Anna"), aspas (Maria José "Mazé") e acentos
-- (Álvaro, Úrsula, Otávio, Natália). Erros esperados: mês inválido (ex.: 2026-13).
--
-- =============================================================================
-- 4. CEPs DOS FUNCIONÁRIOS ATIVOS (padrão <5 dígitos>-<3 dígitos>)
-- -----------------------------------------------------------------------------
-- 28 ocorrências, 27 CEPs distintos.
-- - 22451-900 aparece em 2 funcionários (Ana e Bruno).
-- - João tem 2 CEPs no endereço; Daniela tem o CEP no início do texto;
--   Lucas tem "CEP:22270-000", sem espaço.
-- - 22410-000 é de Mariana (ativa) e de Caio (inativo): deve aparecer por Mariana.
-- - Não devem gerar CEP:
--     Eduardo (endereço NULL), Kim (endereço vazio), Fernanda (sem CEP),
--     Gustavo (50050000, sem hífen), Roberto (22.640-102, com ponto),
--     Helena (4011-300, prefixo curto), Iara (123456-789, prefixo longo),
--     Maria José (20050-0300, sufixo longo).
-- - Não devem aparecer, pois são só de inativos: 20040-030 (Beatriz).
-- =============================================================================

BEGIN;

CREATE TEMP TABLE seed_employee (LIKE employee) ON COMMIT DROP;

INSERT INTO seed_employee
    (id_social_number, first_name, last_name, birth_date, gender, active, base_salary, address, type)
VALUES
    -- A: contrato por tempo indeterminado
    ('09530965214', 'Ana Beatriz',       'Almeida',     '1985-03-12', 'F', TRUE,  5000.00, 'Rua Marquês de São Vicente, 225 - Gávea, Rio de Janeiro - RJ, 22451-900', 'A'),
    ('67841163720', 'Bruno',             'Carvalho',    '1990-10-15', 'M', TRUE,  4500.00, 'Rua Marquês de São Vicente, 124, apto 302 - Gávea, Rio de Janeiro - RJ, 22451-900', 'A'),
    ('22446173942', 'Carlos',            'Dias',        '1960-05-20', 'M', TRUE,  4000.00, 'Av. Paulista, 1000 - Bela Vista, São Paulo - SP, CEP 01310-100', 'A'),
    ('34678777881', 'Daniela',           'Esteves',     '1965-02-10', 'F', TRUE,  3000.00, '30130-010 - Av. Afonso Pena, 500, Belo Horizonte - MG', 'A'),
    ('39815794370', 'Eduardo',           'Farias',      '1955-10-05', 'M', TRUE,  4000.00, NULL, 'A'),
    ('53555489976', 'Fernanda',          'Gomes',       '1966-10-31', 'F', TRUE,  3000.00, 'Rua das Flores, 45 - Centro, Curitiba - PR', 'A'),
    ('63494263710', 'Gustavo',           'Lima',        '1961-01-15', 'M', TRUE,  3500.00, 'Rua da Aurora, 300 - Boa Vista, Recife - PE, 50050000', 'A'),
    ('02171652659', 'Helena',            'Martins',     '1965-10-31', 'F', TRUE,  2500.00, 'Av. Sete de Setembro, 1500, Salvador - BA, CEP 4011-300', 'A'),
    ('24485805107', 'Iara',              'Nogueira',    '1950-06-01', 'N', TRUE,  3800.00, 'Travessa 123456-789, casa 2, Manaus - AM', 'A'),
    ('41589854632', 'João Pedro',        'Oliveira',    '1960-11-10', 'M', TRUE,  3200.00, 'Residencial: Rua A, 10, Niterói - RJ, 24020-125 / Correspondência: Rua B, 20, Niterói - RJ, 24210-200', 'A'),
    ('00699618673', 'Lucas',             'Queiroz',     '1992-07-07', 'M', TRUE,  4200.00, 'Rua Voluntários da Pátria, 89 - Botafogo, Rio de Janeiro - RJ, CEP:22270-000', 'A'),
    ('52601815906', 'Marcos',            'Pinto',       '1963-04-18', 'M', TRUE,  3600.00, 'Rua São Francisco Xavier, 524 - Maracanã, Rio de Janeiro - RJ, 20550-013', 'A'),
    ('08301661305', 'Roberto',           'Mendes',      '1958-03-03', 'M', TRUE,  9500.00, 'Av. das Américas, 4666 - Barra da Tijuca, Rio de Janeiro - RJ, 22.640-102', 'A'),
    ('18609139034', 'Henrique',          'Prado',       '1985-12-25', 'M', TRUE,  4000.00, 'Rua Pinheiro Machado, 70 - Laranjeiras, Rio de Janeiro - RJ, 22231-090', 'A'),
    ('99603082430', 'Gabriel',           'Moura',       '1972-02-29', 'M', TRUE,  3000.00, 'Rua Barata Ribeiro, 370 - Copacabana, Rio de Janeiro - RJ, 22040-002', 'A'),
    ('17205315875', 'Denise',            'Barros',      '1987-06-18', 'F', TRUE,  3000.00, 'Rua Uruguaiana, 94 - Centro, Rio de Janeiro - RJ, 20050-091', 'A'),
    ('93330563435', 'Beatriz',           'Costa',       '1980-10-02', 'F', FALSE, 3500.00, 'Rua do Ouvidor, 50 - Centro, Rio de Janeiro - RJ, 20040-030', 'A'),

    -- B: contrato a prazo
    ('69724626067', 'Mariana',           'Ribeiro',     '1995-10-20', 'F', TRUE,  3500.00, 'Rua Visconde de Pirajá, 330 - Ipanema, Rio de Janeiro - RJ, 22410-000', 'B'),
    ('92491248131', 'Nelson',            'Souza, Jr.',  '1958-08-08', 'M', TRUE,  3000.00, 'Av. Beira Mar, 200 - Meireles, Fortaleza - CE, 60165-121', 'B'),
    ('58148543593', 'Olívia',            'Teixeira',    '1964-04-04', 'F', TRUE,  2800.00, 'Rua dos Andradas, 1000 - Centro Histórico, Porto Alegre - RS, 90020-008', 'B'),
    ('62927124604', 'Paulo',             'Vieira',      '1980-01-25', 'M', TRUE,  2750.50, 'SQS 308, Bloco A, apto 101 - Asa Sul, Brasília - DF, 70355-010', 'B'),
    ('87537661219', 'Rafaela',           'Sant''Anna',  '1963-12-01', 'F', TRUE,  3333.33, 'Rua XV de Novembro, 700 - Centro, Curitiba - PR, 80020-310', 'B'),
    ('62819482112', 'Natália',           'Freitas',     '1962-10-12', 'F', TRUE,  3000.00, 'Rua Chile, 22 - Centro, Salvador - BA, 40020-000', 'B'),
    ('99351819019', 'Kim',               'Lopes',       '1955-08-30', 'N', TRUE,  3000.00, '', 'B'),
    ('93786579741', 'Larissa',           'Cunha',       '1994-06-06', 'F', TRUE,  9999.99, 'Rua Oscar Freire, 900 - Jardins, São Paulo - SP, 01426-001', 'B'),
    ('54323194897', 'Otávio',            'Ramos',       '1959-12-15', 'M', TRUE,  3000.00, 'Av. Getúlio Vargas, 1300 - Savassi, Belo Horizonte - MG, 30112-021', 'B'),

    -- C: estagiário
    ('66489888602', 'Sofia',             'Andrade',     '2003-02-14', 'F', TRUE,  1200.00, 'Rua Jardim Botânico, 600 - Jardim Botânico, Rio de Janeiro - RJ, 22461-000', 'C'),
    ('74418966498', 'Tiago',             'Moreira',     '2004-08-22', 'M', TRUE,  1200.00, 'Rua Augusta, 1500 - Consolação, São Paulo - SP, 01304-001', 'C'),
    ('07906652045', 'Úrsula',            'Pires',       '2002-10-09', 'F', TRUE,  1100.00, 'Rua Conde de Bonfim, 400 - Tijuca, Rio de Janeiro - RJ, 20520-054', 'C'),
    ('49113806912', 'Vicente',           'Rocha',       '1955-03-30', 'M', TRUE,  1000.00, 'Rua do Catete, 311 - Catete, Rio de Janeiro - RJ, 22220-001', 'C'),
    ('75749118606', 'Maria José "Mazé"', 'Lins',        '2004-05-05', 'F', TRUE,  1150.00, 'Rua Gonçalves Dias, 32 - Centro, Rio de Janeiro - RJ, 20050-0300', 'C'),

    -- D: bolsista
    ('40333348745', 'Wagner',            'Santos',      '2000-05-05', 'M', TRUE,  1500.00, 'Rua São Clemente, 258 - Botafogo, Rio de Janeiro - RJ, 22260-000', 'D'),
    ('94068119368', 'Yasmin',            'Tavares',     '2001-09-17', 'N', TRUE,   900.00, 'Av. Atlântica, 1702 - Copacabana, Rio de Janeiro - RJ, 22021-001', 'D'),
    ('03042832406', 'Zélia',             'Vasconcelos', '1956-07-11', 'F', TRUE,   800.00, 'Rua Haddock Lobo, 595 - Cerqueira César, São Paulo - SP, 01414-001', 'D'),
    ('48132432070', 'Álvaro',            'Xavier',      '2001-01-03', 'M', TRUE,  1000.00, 'Av. Rio Branco, 156 - Centro, Rio de Janeiro - RJ, 20040-901', 'D'),
    ('25276018987', 'Igor',              'Barbosa',     '2002-10-21', 'M', TRUE,  1000.00, 'Rua Mariz e Barros, 100 - Tijuca, Rio de Janeiro - RJ, 20270-002', 'D'),
    ('73355519600', 'Caio',              'Duarte',      '1999-04-27', 'M', FALSE,  950.00, 'Rua da Carioca, 10 - Centro, Rio de Janeiro - RJ, 22410-000', 'D');

-- Remove o resultado de execuções anteriores (somente dos CPFs deste seed)
DELETE FROM attendance WHERE id_employee IN (SELECT id_social_number FROM seed_employee);
DELETE FROM payment    WHERE id_employee IN (SELECT id_social_number FROM seed_employee);
DELETE FROM employee   WHERE id_social_number IN (SELECT id_social_number FROM seed_employee);

INSERT INTO employee SELECT * FROM seed_employee;

-- -----------------------------------------------------------------------------
-- Pagamentos anteriores. Os meses 2026-10, 2025-12 e 2026-02 ficam pendentes para
-- serem calculados pelo sistema, exceto onde indicado (Ana, Lucas e Denise).
-- -----------------------------------------------------------------------------

-- Ativos: jul-set/2026, iguais ao salário base (Tiago não tem histórico)
INSERT INTO payment (id_employee, month, year, payment)
SELECT e.id_social_number, EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), e.base_salary
FROM seed_employee e
CROSS JOIN generate_series(TIMESTAMP '2026-07-01', TIMESTAMP '2026-09-01', INTERVAL '1 month') AS m
WHERE e.active
  AND e.id_social_number <> '74418966498';

-- Ana: completa 24 meses (out/2024 a set/2026), com reajuste em jan/2026 e
-- bônus de aniversário (+200) em março. Ocupa dez/2025 e fev/2026.
INSERT INTO payment (id_employee, month, year, payment)
SELECT '09530965214', EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m),
       CASE WHEN m < TIMESTAMP '2026-01-01' THEN 4500.00 ELSE 5000.00 END
     + CASE WHEN EXTRACT(MONTH FROM m) = 3 THEN 200.00 ELSE 0 END
FROM generate_series(TIMESTAMP '2024-10-01', TIMESTAMP '2026-06-01', INTERVAL '1 month') AS m;

-- Lucas: era estagiário em jan-jun/2026 (ocupa fev/2026)
INSERT INTO payment (id_employee, month, year, payment)
SELECT '00699618673', EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), 1200.00
FROM generate_series(TIMESTAMP '2026-01-01', TIMESTAMP '2026-06-01', INTERVAL '1 month') AS m;

-- Inativos: Beatriz jan-mar/2026; Caio jan-set/2026 (aparece no CSV de set/2026)
INSERT INTO payment (id_employee, month, year, payment)
SELECT e.id_social_number, EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), e.base_salary
FROM seed_employee e
CROSS JOIN generate_series(TIMESTAMP '2026-01-01', TIMESTAMP '2026-03-01', INTERVAL '1 month') AS m
WHERE e.id_social_number = '93330563435';

INSERT INTO payment (id_employee, month, year, payment)
SELECT e.id_social_number, EXTRACT(MONTH FROM m), EXTRACT(YEAR FROM m), e.base_salary
FROM seed_employee e
CROSS JOIN generate_series(TIMESTAMP '2026-01-01', TIMESTAMP '2026-09-01', INTERVAL '1 month') AS m
WHERE e.id_social_number = '73355519600';

-- Denise: 2026-10 já pago, com valor diferente do que seria calculado (3000.00)
INSERT INTO payment (id_employee, month, year, payment)
VALUES ('17205315875', 10, 2026, 1234.56);

-- -----------------------------------------------------------------------------
-- Presenças
-- -----------------------------------------------------------------------------
INSERT INTO attendance (id_employee, entrance)
VALUES
    -- ===== OUT/2026 =====
    -- Lucas (A): fins de semana não geram bônus
    ('00699618673', '2026-10-10 09:00:00'),
    ('00699618673', '2026-10-11 09:00:00'),

    -- Mariana (B): fins de semana não geram bônus
    ('69724626067', '2026-10-17 09:00:00'),
    ('69724626067', '2026-10-18 09:00:00'),

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

    -- Wagner (D): todos os 9 dias de fim de semana, incluindo o primeiro instante de 03/10
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

    -- Igor (D): aniversariante de outubro com 2 dias de fim de semana
    ('25276018987', '2026-10-24 09:00:00'),
    ('25276018987', '2026-10-25 09:00:00'),

    -- Caio (D, inativo): não deve ser calculado
    ('73355519600', '2026-10-03 10:00:00'),
    ('73355519600', '2026-10-04 10:00:00'),

    -- ===== DEZ/2025 (virada de ano) =====
    -- Sofia (C): 3 dias de fim de semana
    ('66489888602', '2025-12-06 09:00:00'),
    ('66489888602', '2025-12-07 09:00:00'),
    ('66489888602', '2025-12-13 09:00:00'),

    -- Wagner (D): 2 dias no mês; 30/11/2025 e 03/01/2026 ficam de fora
    ('40333348745', '2025-11-30 10:00:00'), -- domingo do mês anterior
    ('40333348745', '2025-12-27 10:00:00'),
    ('40333348745', '2025-12-28 10:00:00'),
    ('40333348745', '2026-01-03 10:00:00'), -- sábado do ano seguinte

    -- Vicente (C): 28/12 conta; 31/12 23:59 é quarta-feira
    ('49113806912', '2025-12-28 09:00:00'),
    ('49113806912', '2025-12-31 23:59:00'),

    -- ===== FEV/2026 (1º e último dia do mês são fim de semana) =====
    -- Wagner (D): 01/02 00:00 e 28/02 23:59 contam; 31/01 e 01/03 não
    ('40333348745', '2026-01-31 10:00:00'), -- sábado do mês anterior
    ('40333348745', '2026-02-01 00:00:00'), -- domingo, primeiro instante do mês
    ('40333348745', '2026-02-28 23:59:00'), -- sábado, último dia do mês
    ('40333348745', '2026-03-01 10:00:00'), -- domingo do mês seguinte

    -- Yasmin (D): 2 entradas no sábado 14/02 e 1 no domingo 15/02 => 2 dias
    ('94068119368', '2026-02-14 08:00:00'),
    ('94068119368', '2026-02-14 14:00:00'),
    ('94068119368', '2026-02-15 08:00:00'),

    -- Igor (D): 1 dia
    ('25276018987', '2026-02-07 09:00:00');

COMMIT;
