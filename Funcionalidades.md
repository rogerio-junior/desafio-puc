# Funcionalidades

## Calcular pagamento do mês dos funcionários
```
POST /payments/calculate (RPC)
Request: 
{
    "month": "2026-05" // Padrão ISO 8601
}

Response:
{
    "month": "2026-05",
    "calculated": 78877 // Total de funcionários calculados no mês
}
```

Comportamento do endpoint:
- Valida formato do input
- Ponto principal do sistema, onde se quer ver o uso de herança. Cálculo do salário:
    - Contrato por tempo indeterminado && aniversário no mês computado = + R200
    - Contrato por tempo (indeterminado || determinado) && ( (idade > 65 && homem) || (idade > 60 && mulher)) = + 3 % do salário base
        - E genêro neutro?
    - Funcionário (bolsista || estagiário) = + R$40 por cada sábado ou domingo
    - Sem considerar outros benefícios
- O mês de referência é o mês de fechamento, logo, deve ser calculado o salário do mês anterior, não o do atual (enviado por parâmetro)

Front end:
- Valor padrão do mês atual
- Retornar os resultados de uma vez?


Consulta de pagamentos de um funcionário
```
GET /employees/{cpf}/payments?month=05-2026
Response: 
{
    [
        { "month": "2026-05", "salary": "52.30"}
    ]
}
// Totais e informações de página
// Mais recente para o mais antigo
```

Consulta e exportação de pagamentos no mês
```
GET /payments?month=05-2026
HEADER: Accept?: csv | retornar tbm em json para a tela
Response: 
{
    [
        { "id_employee": "44454", "firstName": "asdda", "lastName": "asdasd", "month": 5, "year": 1887, "salary": ""}
    ]
}
// Em ordem alfabética
// Estrutura CSV `"CPF", "Nome", "Sobrenome", "Mês", "Ano", "Salário a receber"`
```


Busca funcionários ativos por CEP
```
GET /employees?CEP=&active=true
Response: 
{
    [
        { "id_employee": "44454", "firstName": "asdda", "lastName": "asdasd", "month": 5, "year": 1887, "salary": ""}
    ]
}
// Em ordem alfabética
// Estrutura CSV `"CPF", "Nome", "Sobrenome", "Mês", "Ano", "Salário a receber"`
```
- o CEP cumpre com o padrão de formação seguinte: <cinco números>-<três números>


- Ideia inicial: Fazer um serviço de query em Payments com filtros, ordenação, fields, etc. Deixar ela flexível para que o Caso de Uso possa usar e formatar os dados para a saida esperada

## Adicionais

### Cadastrar funcionário

### Login/Auth