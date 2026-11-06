package br.puc_rio.tecgraf.challenge.domain;

import java.util.List;
import java.util.function.Function;

// Classe mínima para representar a paginação e poder trafegar essa informação entre as camadas.
// Decidi fazer a paginação é por offset, visto que o volume de dados do desafio é pequeno.
// Com volumes grandes eu usaria keyset (afterKey), que aumenta a complexidade.
public record Page<T>(List<T> items, Range range, long total) {
    public Page(List<T> items) {
        this(items, new Range(0, Math.max(items.size(), 1)), items.size());
    }

    public <R> Page<R> map(Function<T, R> mapFunction) {
        var mappedItems = items().stream().map(mapFunction).toList();

        return new Page<>(mappedItems, range(), total());
    }

    public record Range(int offset, int limit) {
        public Range {
            if (offset < 0) {
                throw new IllegalArgumentException("offset must be greater than or equal to 0");
            }

            if (limit <= 0) {
                throw new IllegalArgumentException("limit must be greater than 0");
            }
        }
    }
}
