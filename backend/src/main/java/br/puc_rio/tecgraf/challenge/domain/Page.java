package br.puc_rio.tecgraf.challenge.domain;

import java.util.List;
import java.util.function.Function;

// Classe mínima para representar a paginação e poder trafegar essa informação entre as camadas
// TODO: Tentar paginar por afterKey
public record Page<T>(List<T> items, Cursor cursor, long total) {
    public Page(List<T> items) {
        this(items, new Cursor(null, Math.max(items.size(), 1)), items.size()); // O Cursor exige limit > 0, mesmo em lista vazia
    }

    public Cursor nexCursor(Function<T, String> keyExtractor) {
        if (items.size() == 0) {
            return new Cursor(null, cursor().limit());
        }

        return new Cursor(keyExtractor.apply(items().getLast()), cursor.limit());
    }

    public <R> Page<R> map(Function<T, R> mapFunction) {
        var mappedItems = items().stream().map(mapFunction).toList();

        return new Page<>(mappedItems, cursor(), total());
    }

    public record Cursor(String afterKey, int limit) {
        public Cursor {
            if (limit <= 0) {
                throw new IllegalArgumentException("limit must be greater than 0");
            }

            if (afterKey == null) {
                afterKey = String.valueOf(0);
            } else if (!afterKey.matches("\\d+")) {
                throw new IllegalArgumentException("afterKey must be null or an positive integer number");
            }
        }
    }
}
