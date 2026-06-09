package com.studymate.module.ai.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.module.ai.vo.AiAnalyzeResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiAnalyzeResultParserTest {

    private AiAnalyzeResultParser parser;

    @BeforeEach
    void setUp() {
        parser = new AiAnalyzeResultParser(new ObjectMapper());
    }

    @Test
    void parseValidJsonReturnsSanitizedResult() {
        String response = """
                {
                  "durationMinutes": 90,
                  "categories": ["Redis", "unknown"],
                  "studyContent": "学习 Redis 持久化",
                  "weakPoints": ["AOF 和 RDB 对比"],
                  "emotionStatus": "有点累",
                  "tomorrowPlan": "整理一张 AOF 和 RDB 对比表",
                  "aiSummary": "今天完成了 Redis 持久化复盘",
                  "aiComfort": "能看见薄弱点就是进步"
                }
                """;

        AiAnalyzeResultVO result = parser.parse(response, "原始输入");

        assertThat(result.getDurationMinutes()).isEqualTo(90);
        assertThat(result.getCategories()).containsExactly("Redis");
        assertThat(result.getStudyContent()).isEqualTo("学习 Redis 持久化");
        assertThat(result.getWeakPoints()).containsExactly("AOF 和 RDB 对比");
    }

    @Test
    void parseMarkdownWrappedJsonExtractsJson() {
        String response = """
                ```json
                {"durationMinutes":45,"categories":["MySQL"],"studyContent":"索引","weakPoints":[]}
                ```
                """;

        AiAnalyzeResultVO result = parser.parse(response, "今天看了 MySQL 索引");

        assertThat(result.getDurationMinutes()).isEqualTo(45);
        assertThat(result.getCategories()).containsExactly("MySQL");
        assertThat(result.getStudyContent()).isEqualTo("索引");
        assertThat(result.getEmotionStatus()).isEqualTo("平静");
    }

    @Test
    void parseNonJsonReturnsFallbackWithRawContent() {
        AiAnalyzeResultVO result = parser.parse("not json", "今天学习了 Java 集合");

        assertThat(result.getDurationMinutes()).isZero();
        assertThat(result.getCategories()).isEmpty();
        assertThat(result.getStudyContent()).isEqualTo("今天学习了 Java 集合");
        assertThat(result.getTomorrowPlan()).isEqualTo("明天可以用 15 分钟轻量复习今天最不熟的一小点。");
        assertThat(result.getAiComfort()).isEqualTo("今天能留下记录已经很好了，慢慢来，每一步都算数。");
    }

    @Test
    void parseWrongFieldTypesUsesDefaults() {
        String response = """
                {"durationMinutes":"two hours","categories":"Redis","weakPoints":"AOF","emotionStatus":123}
                """;

        AiAnalyzeResultVO result = parser.parse(response, "今天有点累");

        assertThat(result.getDurationMinutes()).isZero();
        assertThat(result.getCategories()).isEmpty();
        assertThat(result.getWeakPoints()).isEmpty();
        assertThat(result.getEmotionStatus()).isEqualTo("平静");
    }
}
