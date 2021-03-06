package de.cebitec.mesos.tasks;

import de.cebitec.mesos.volumes.Volumes;
import org.apache.mesos.Protos;

import java.util.UUID;

/**
 *
 * @author jsteiner
 * @param <T> TaskImplementation
 */
public interface Task<T extends Task> {

    public T createTask(String dockerImage,
                        int maxCPU,
                        int maxMEM,
                        String principal,
                        Volumes volumes,
                        String... arg);

    public T createTask(String dockerImage,
                        int maxCPU,
                        int maxMEM,
                        String principal,
                        String... arg);

    /**
     * @param numberOfSlaves
     * @param currentSlave
     * @return
     */
    public T calculatePriority(int numberOfSlaves, Protos.Offer currentSlave);

    /**
     * @return TaskInfo
     */
    public Protos.TaskInfo getTaskContent();

    /**
     * Assign selected slave to task and prepare it to run.
     *
     * @param slave The selected Slave
     */
    public T prepareToRun(Protos.Offer slave);

    /**
     * Get Task Priority
     *
     * @return priority
     */
    public double getPriority();


    /**
     * Get Needed CPU
     *
     * @return cpu
     */
    public int getNeededCPU();

    /**
     * Get Needed mem
     *
     * @return mem
     */
    public double getNeededMEM();

    /**
     * @param state Protos.TaskState set task status
     */
    public void setStatus(Protos.TaskState state);

    /**
     * @return Protos.TaskState task status
     */
    public Protos.TaskState getStatus();

    /**
     * @return Execution Errors
     */
    public int getExecutionErrors();

    /**
     * Set execution Errors
     *
     * @param executionErrors Execution Errors
     */
    public void setExecutionErrors(int executionErrors);

    public long getRuntimeSeconds();

    public long getRuntimeMinutes();

}