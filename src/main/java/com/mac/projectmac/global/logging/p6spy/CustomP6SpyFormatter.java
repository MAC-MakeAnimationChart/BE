package com.mac.projectmac.global.logging.p6spy;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CustomP6SpyFormatter implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed,
                                String category, String prepared, String sql, String url) {
        // commit, rollback 제외
        if (Category.COMMIT.getName().equals(category)
                || Category.ROLLBACK.getName().equals(category)) {
            return "";
        }

        // 빈 쿼리 제외
        if (sql == null || sql.isBlank()) {
            return "";
        }

        return String.format("[SQL | %dms]\n%s", elapsed, formatSql(sql));
    }

    private String formatSql(String sql) {
        return sql.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT)
                .replace("SELECT ", "\nSELECT ")
                .replace("FROM ", "\nFROM ")
                .replace("WHERE ", "\nWHERE ")
                .replace("AND ", "\n  AND ")
                .replace("OR ", "\n  OR ")
                .replace("LEFT JOIN ", "\nLEFT JOIN ")
                .replace("INNER JOIN ", "\nINNER JOIN ")
                .replace("ORDER BY ", "\nORDER BY ")
                .replace("GROUP BY ", "\nGROUP BY ")
                .trim();
    }
}
