package com.libertytree;

import com.agorapulse.worker.JobManager;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@MicronautTest
@Property(name = "worker.jobs.email-digest-distributed-job-send-email.enabled", value = "true")
@Property(name = "worker.jobs.email-digest-distributed-job-send-email.fixed-rate", value = "1ms")
class EmailDigestDistributedJobTest {

    @Inject JobManager jobManager;

    @MockBean(FallbackEmailDigestService.class) EmailDigestService emailDigestService = mock(EmailDigestService.class);

    @Test
    void testSendEmailsDistributed() {
        jobManager.forceRun(EmailDigestDistributedJob.class, "generateEmailsForDigest");

        await().atMost(2, SECONDS).untilAsserted(() ->
                verify(emailDigestService, times(1)).sendEmail("user@example.com")
        );
    }

}