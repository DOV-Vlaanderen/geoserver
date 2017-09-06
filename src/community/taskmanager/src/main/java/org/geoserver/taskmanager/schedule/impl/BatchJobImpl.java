/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.schedule.impl;

import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.BatchElement;
import org.geoserver.taskmanager.data.Run;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.report.Report;
import org.geoserver.taskmanager.report.ReportService;
import org.geoserver.taskmanager.schedule.TaskResult;
import org.geoserver.taskmanager.schedule.TaskType;
import org.geoserver.taskmanager.util.TaskManagerBeans;
import org.geotools.util.logging.Logging;

/**
 * The scheduled batch job implementation.
 * 
 * @author Niels Charlier
 *
 */
@DisallowConcurrentExecution
public class BatchJobImpl implements InterruptableJob {
    
    private static final Logger LOGGER = Logging.getLogger(BatchJobImpl.class);
    
    protected boolean interrupted = false;
        
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        //get all the context beans
        ApplicationContext appContext;
        try {
            appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
        TaskManagerBeans beans = appContext.getBean(TaskManagerBeans.class);
        
        //get the batch
        String batchName = (String) context.getJobDetail().getKey().getName();
        Batch batch = beans.getDao().getBatch(batchName);        
        List<? extends BatchElement> elements = batch.getElements();
        
        //stacks for processing
        Stack<TaskResult> resultStack = new Stack<TaskResult>();
        Stack<Run> runStack = new Stack<Run>();
                
        for (int i = 0 ;  i < elements.size() ; i++) {
           
            BatchElement element = elements.get(i);
            
            //if this task is currently running, wait
            Run run = null;
            while ((run = beans.getDataUtil().runIfPossible(element)) == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
            
            //OK, let's go
            Task task = element.getTask();                        
            TaskType type = beans.getTaskTypes().get(task.getType());
            Map<String, String> rawParameterValues = beans.getTaskUtil().getRawParameterValues(task);            
            boolean rollback = false;

            try {
                Map<String, Object> parameterValues = beans.getTaskUtil().parseParameters(type, rawParameterValues);
                resultStack.push(type.run(batch, task, parameterValues));

                run.setEnd(new Date());   
                run = beans.getDao().save(run);
                runStack.push(run);                
            } catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Task " + task.getFullName() + " failed in batch "
                        + batch.getFullName() + ", rolling back.", e);
                run.setMessage(e.getMessage());
                run.setEnd(new Date());
                run.setStatus(Run.Status.FAILED);
                run = beans.getDao().save(run);
                rollback = true;
            }
            
            if (interrupted) {
                LOGGER.log(Level.INFO, "Batch  " + batch.getFullName() + " manually cancelled, rolling back.");
                rollback = true;
            }
                        
            if (rollback) {
                while (!resultStack.isEmpty()) {
                    Run runPop = runStack.pop();
                    try {                    
                        resultStack.pop().rollback();
                        runPop.setStatus(Run.Status.ROLLED_BACK);
                    } catch (Exception e) {
                        Task popTask = runPop.getBatchElement().getTask();
                        runPop.setMessage(e.getMessage());
                        LOGGER.log(Level.SEVERE, "Task " + popTask.getFullName() + 
                                " failed to rollback in batch " + batch.getFullName() + "", e);
                    }
                    beans.getDao().save(runPop);
                }
                break; //leave for-loop           
            }
            
        }

        while (!resultStack.isEmpty()) {
            Run runPop = runStack.pop();
            Run runTemp;
            //to avoid concurrent commit, if this task is currently still waiting for a commit, wait
            while ((runTemp = beans.getDataUtil().startCommitIfPossible(runPop)) == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
            runPop = runTemp;
            try {
                resultStack.pop().commit();
                runPop.setStatus(Run.Status.COMMITTED);
            } catch (Exception e) {
                Task task = runPop.getBatchElement().getTask();
                LOGGER.log(Level.SEVERE, "Task " + task.getFullName() + 
                        " failed to commit in batch " + batch.getFullName() + "", e);
                runPop.setMessage(e.getMessage());
                runPop.setStatus(Run.Status.FAILED);
            }
            beans.getDao().save(runPop);
        }
        
        //send the report
        Report report = beans.getReportBuilder().buildBatchReport(batchName);
        for (ReportService reportService : beans.getReportServices()) {
            if (reportService.getFilter().matches(report.getType())) {
                reportService.sendReport(report);
            }
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        interrupted = true;
    }
    

}
