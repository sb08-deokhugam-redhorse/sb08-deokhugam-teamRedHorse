package com.redhorse.deokhugam.batch;

import com.redhorse.deokhugam.global.batch.batchConfig.AlarmsCleanupBatchConfig;
import com.redhorse.deokhugam.global.batch.repository.AlarmBatchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmsCleanupTaskletUnitTest {

    @Mock
    private AlarmBatchRepository alarmRepository;

    @InjectMocks
    private AlarmsCleanupBatchConfig batchConfig;

    @Test
    @DisplayName("삭제된 데이터가 있으면 CONTINUABLE을 반환")
    void execute_WhenDataDeleted_ReturnsContinuable() throws Exception {
        // given
        Tasklet tasklet = batchConfig.deleteOldAlarmsTasklet();

        StepExecution stepExecution = new StepExecution("deleteOldAlarmsStep", new JobExecution(1L));
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        when(alarmRepository.deleteOldAlarmsInBulk(1000)).thenReturn(500);

        // when
        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        // then
        assertEquals(RepeatStatus.CONTINUABLE, status);
        assertEquals(500, contribution.getStepExecution().getWriteCount());
        verify(alarmRepository, times(1)).deleteOldAlarmsInBulk(1000);
    }

    @Test
    @DisplayName("삭제된 데이터가 없으면 FINISHED를 반환")
    void execute_WhenNoDataDeleted_ReturnsFinished() throws Exception {
        // given
        Tasklet tasklet = batchConfig.deleteOldAlarmsTasklet();
        StepExecution stepExecution = new StepExecution("deleteOldAlarmsStep", new JobExecution(1L));
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        when(alarmRepository.deleteOldAlarmsInBulk(1000)).thenReturn(0);

        // when
        RepeatStatus status = tasklet.execute(contribution, chunkContext);

        // then
        assertEquals(RepeatStatus.FINISHED, status);
        assertEquals(0, contribution.getStepExecution().getWriteCount());
    }
}