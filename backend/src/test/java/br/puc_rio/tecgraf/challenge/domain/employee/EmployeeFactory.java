package br.puc_rio.tecgraf.challenge.domain.employee;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.concurrent.ThreadLocalRandom;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EmployeeFactory {
    // CPF sintético derivado de um número, útil para ter um id distinto por cenário
    public static String employeeId(int number) {
        return "%011d".formatted(number);
    }

    // Sorteia um dia dentro do mês de competência: a pessoa completa `age` anos
    // até o fim da competência.
    public static LocalDate birthdayIn(YearMonth period, int age) {
        return randomDayOf(period.minusYears(age));
    }

    // Sorteia um dia em um dos 11 meses anteriores à competência: o aniversário
    // já passou, então a idade no fim da competência é exatamente `age`, inclusive
    // quando o mês sorteado cai no ano anterior.
    public static LocalDate birthdayNotIn(YearMonth period, int age) {
        final var monthsBefore = ThreadLocalRandom.current().nextInt(1, 12);

        return randomDayOf(period.minusYears(age).minusMonths(monthsBefore));
    }

    private static LocalDate randomDayOf(YearMonth month) {
        final var day = ThreadLocalRandom.current().nextInt(1, month.lengthOfMonth() + 1);

        return month.atDay(day);
    }
}
