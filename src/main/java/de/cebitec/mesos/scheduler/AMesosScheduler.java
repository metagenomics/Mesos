package de.cebitec.mesos.scheduler;

import de.cebitec.mesos.framework.IFramework;
import de.cebitec.mesos.tasks.Task;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AMesosScheduler implements Scheduler {

    protected static final Logger logger = LoggerFactory.getLogger(AMesosScheduler.class);

    private Protos.FrameworkID frameworkID;

    private SchedulerDriver driver;

    public AMesosScheduler(IFramework framework){
        driver = new MesosSchedulerDriver(this,
                Protos.FrameworkInfo.newBuilder()
                        .setHostname(framework.getHostname())
                        .setName(framework.getFrameworkName())
                        .setUser(framework.getUserName())
                        .build(),
                framework.getMasterIp() + ":" + framework.getMasterPort());
        driver.start();
    }

    public void manageResourceOffers(List<Protos.Offer> offers){
        resourceOffers(driver, offers);
    }

    @Override
    public void resourceOffers(SchedulerDriver schedulerDriver, List<Protos.Offer> list) {
        this.driver = schedulerDriver;
        handleResources(list);
    }

    protected abstract void handleResources(List<Protos.Offer> list);

    protected void declineOffer(Protos.Offer offer){
        driver.declineOffer(offer.getId());
    }

    protected void launchTask(Protos.Offer offer, List<Protos.TaskInfo> infos, Protos.Filters filters){
        driver.launchTasks(offer.getId(),infos, filters);
    }

    /**
     * Add Task
     * @param task Task
     * @return
     */
    public abstract Protos.TaskInfo addTask(Task task);

    @Override
    public void registered(SchedulerDriver schedulerDriver, Protos.FrameworkID frameworkID, Protos.MasterInfo masterInfo) {
        logger.info("registered() master={}:{}, framework={}", masterInfo.getIp(), masterInfo.getPort(), frameworkID);
        this.frameworkID = frameworkID;
    }

    @Override
    public void reregistered(SchedulerDriver schedulerDriver, Protos.MasterInfo masterInfo) {
        logger.info("reregistered()");
    }

    /**
     *
     * Get Resources Memory
     * @param offer
     * @return
     */
    protected double getResourcesMem(Protos.Offer offer) {
        for (Protos.Resource r : offer.getResourcesList()) {
            if (r.getName().equals("mem")) {
                return r.getScalar().getValue();
            }
        }
        return -1;
    }

    /**
     * Get Resources Cpus
     * @param offer
     * @return
     */
    protected double getResourcesCpus(Protos.Offer offer) {
        for (Protos.Resource r : offer.getResourcesList()) {
            if (r.getName().equals("cpus")) {
                return r.getScalar().getValue();
            }
        }
        return -1;
    }

    @Override
    public void offerRescinded(SchedulerDriver schedulerDriver, Protos.OfferID offerID) {
        logger.info("offerRescinded()");
    }

    public abstract Task getTask(Protos.TaskID taskID);

    @Override
    public void statusUpdate(SchedulerDriver driver, Protos.TaskStatus taskStatus) {

        final String taskId = taskStatus.getTaskId().getValue();

        Task actualTask = getTask(taskStatus.getTaskId());

        actualTask.setStatus(taskStatus.getState());

        logger.info("statusUpdate() task {} is in state {}",
                taskId, actualTask.getStatus());

        switch (actualTask.getStatus()) {
            case TASK_RUNNING:
                logger.info("Task [{}] running ( {} seconds | {} minutes )", actualTask.getTaskContent().getTaskId().getValue(), actualTask.getRuntimeSeconds(), actualTask.getRuntimeMinutes());
                onRunning(actualTask);
                break;
            case TASK_FINISHED:
                logger.info("Task {} FINISHED (in {} seconds)", actualTask.getTaskContent().getTaskId().getValue(), actualTask.getRuntimeSeconds());
                onFinished(actualTask);
                break;
            default:
                logger.warn("Task-Problem: Cause = {}", taskStatus.getReason());
                if (actualTask.getExecutionErrors() <= 3) {
                    switch (taskStatus.getReason()) {
                        case REASON_COMMAND_EXECUTOR_FAILED:
                            actualTask.setExecutionErrors(actualTask.getExecutionErrors() + 1);
                            logger.info("Retrying Task Execution ...");
                            logger.debug("REASON: {}", taskStatus.getReason());
                            break;
                    }
                } else {
                    logger.warn("Task [{}] gets dequeued in case of a three-timed failed execution.", actualTask.getTaskContent().getTaskId());
                    logger.warn("Task [{}] -> latest Reason [{}]", taskStatus.getReason());
                    onUndefinedFailure(actualTask);
                }
                break;
        }
    }

    public abstract void onRunning(Task runningTask);

    public abstract void onExecutorFailure(Task runningTask);

    public abstract void onFinished(Task finishedTask);

    public abstract void onUndefinedFailure(Task failedTask);

    @Override
    public void frameworkMessage(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, Protos.SlaveID slaveID, byte[] bytes) {
        logger.info("frameworkMessage()");
    }

    @Override
    public void disconnected(SchedulerDriver schedulerDriver) {
        logger.info("disconnected()");
    }

    @Override
    public void slaveLost(SchedulerDriver schedulerDriver, Protos.SlaveID slaveID) {
        logger.info("slaveLost()");
    }

    @Override
    public void executorLost(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, Protos.SlaveID slaveID, int i) {
        logger.info("executorLost()");
    }

    @Override
    public void error(SchedulerDriver schedulerDriver, String s) {
        logger.error("error() {}", s);
    }

    public Protos.FrameworkID getFramework() {
        return this.frameworkID;
    }
}