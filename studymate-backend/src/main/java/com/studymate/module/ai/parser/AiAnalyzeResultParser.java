package com.studymate.module.ai.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.module.ai.vo.AiAnalyzeResultVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AiAnalyzeResultParser {

    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "Java基础",
            "Spring Boot",
            "MySQL",
            "Redis",
            "MyBatis Plus",
            "计算机基础",
            "项目实战",
            "面试复盘"
    );
    private static final String DEFAULT_EMOTION = "平静";
    private static final String DEFAULT_TOMORROW_PLAN = "明天可以用 15 分钟轻量复习今天最不熟的一小点。";
    private static final String DEFAULT_AI_SUMMARY = "今天已经完成了一次学习复盘，后续可以从一个小问题继续巩固。";
    private static final String DEFAULT_AI_COMFORT = "今天能留下记录已经很好了，慢慢来，每一步都算数。";

    private final ObjectMapper objectMapper;

    public AiAnalyzeResultParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiAnalyzeResultVO parse(String aiResponse, String rawContent) {
        JsonNode root = readJson(aiResponse);
        if (root == null || !root.isObject()) {
            return fallback(rawContent);
        }

        AiAnalyzeResultVO resultVO = new AiAnalyzeResultVO();
        resultVO.setDurationMinutes(readDuration(root));
        resultVO.setCategories(readStringArray(root.path("categories"), true));
        resultVO.setStudyContent(readText(root.path("studyContent"), defaultStudyContent(rawContent)));
        resultVO.setWeakPoints(readStringArray(root.path("weakPoints"), false));
        resultVO.setEmotionStatus(readText(root.path("emotionStatus"), DEFAULT_EMOTION));
        resultVO.setTomorrowPlan(readText(root.path("tomorrowPlan"), DEFAULT_TOMORROW_PLAN));
        resultVO.setAiSummary(readText(root.path("aiSummary"), DEFAULT_AI_SUMMARY));
        resultVO.setAiComfort(readText(root.path("aiComfort"), DEFAULT_AI_COMFORT));
        return resultVO;
    }

    public boolean canParseJson(String aiResponse) {
        JsonNode root = readJson(aiResponse);
        return root != null && root.isObject();
    }

    private JsonNode readJson(String aiResponse) {
        String json = extractJson(aiResponse);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractJson(String aiResponse) {
        if (!StringUtils.hasText(aiResponse)) {
            return "";
        }
        String trimmed = aiResponse.trim();
        if (trimmed.startsWith("```")) {
            int firstLineEnd = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstLineEnd >= 0 && lastFence > firstLineEnd) {
                trimmed = trimmed.substring(firstLineEnd + 1, lastFence).trim();
            }
        }
        int objectStart = trimmed.indexOf('{');
        int objectEnd = trimmed.lastIndexOf('}');
        if (objectStart >= 0 && objectEnd > objectStart) {
            return trimmed.substring(objectStart, objectEnd + 1);
        }
        return trimmed;
    }

    private Integer readDuration(JsonNode root) {
        JsonNode node = root.path("durationMinutes");
        if (!node.isNumber()) {
            return 0;
        }
        int value = node.asInt();
        return Math.max(value, 0);
    }

    private List<String> readStringArray(JsonNode node, boolean filterCategories) {
        if (!node.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        for (JsonNode item : node) {
            if (item.isTextual() && StringUtils.hasText(item.asText())) {
                String value = item.asText().trim();
                if (!filterCategories || ALLOWED_CATEGORIES.contains(value)) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    private String readText(JsonNode node, String defaultValue) {
        if (!node.isTextual() || !StringUtils.hasText(node.asText())) {
            return defaultValue;
        }
        return node.asText().trim();
    }

    private AiAnalyzeResultVO fallback(String rawContent) {
        AiAnalyzeResultVO resultVO = new AiAnalyzeResultVO();
        resultVO.setDurationMinutes(0);
        resultVO.setCategories(List.of());
        resultVO.setStudyContent(defaultStudyContent(rawContent));
        resultVO.setWeakPoints(List.of());
        resultVO.setEmotionStatus(DEFAULT_EMOTION);
        resultVO.setTomorrowPlan(DEFAULT_TOMORROW_PLAN);
        resultVO.setAiSummary(DEFAULT_AI_SUMMARY);
        resultVO.setAiComfort(DEFAULT_AI_COMFORT);
        return resultVO;
    }

    private String defaultStudyContent(String rawContent) {
        return StringUtils.hasText(rawContent) ? rawContent : "";
    }
}
