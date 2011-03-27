package org.codesearch.indexer.core;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 *
 * @author Samuel Kogler
 */
public class QuartzGuiceJobFactory implements JobFactory{

    private final Injector injector;

    @Inject
    public QuartzGuiceJobFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Job newJob(TriggerFiredBundle tfb) throws SchedulerException {
        JobDetail jd = tfb.getJobDetail();
        return (Job) injector.getInstance(jd.getJobClass());
    }

}
