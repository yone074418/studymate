package com.studymate.module.statistics.service;

import com.studymate.module.statistics.service.impl.StatisticsServiceImpl;
import com.studymate.module.study.mapper.StudyRecordCategoryMapper;
import com.studymate.module.study.mapper.StudyRecordMapper;
import com.studymate.module.study.mapper.WeakPointMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class StatisticsServiceSpringContextTest {

    @Test
    void springCanCreateStatisticsServiceWithConstructorInjection() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(StudyRecordMapper.class, () -> mock(StudyRecordMapper.class));
            context.registerBean(StudyRecordCategoryMapper.class, () -> mock(StudyRecordCategoryMapper.class));
            context.registerBean(WeakPointMapper.class, () -> mock(WeakPointMapper.class));
            context.registerBean(StatisticsServiceImpl.class);

            context.refresh();

            assertThat(context.getBean(StatisticsService.class)).isInstanceOf(StatisticsServiceImpl.class);
        }
    }
}
