package com.studymate.module.ai.prompt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudyRecordPromptBuilderTest {

    @Test
    void buildContainsStrictJsonRulesAndRawContent() {
        String prompt = new StudyRecordPromptBuilder().build("今天学了 Redis，有点累");

        assertThat(prompt).contains("Only return valid JSON");
        assertThat(prompt).contains("Do not wrap the result in ```json or ```");
        assertThat(prompt).contains("durationMinutes");
        assertThat(prompt).contains("Java基础");
        assertThat(prompt).contains("平静");
        assertThat(prompt).contains("今天学了 Redis，有点累");
    }
}
