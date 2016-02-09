/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mesos.tasks;

import de.cebitec.mesos.volumes.IVolume;
import de.cebitec.mesos.volumes.Volumes;
import org.apache.mesos.Protos;
import org.apache.mesos.Protos.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author jsteiner
 */
public class DockerTask implements Task {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DockerTask.class);

    /**
     * Starttime.
     */
    private Date startTime;

    /**
     * The overlying ProtosTask Content to execute.
     */
    private Protos.TaskInfo taskContent;

    /**
     * The needed amount of CPU resources.
     */
    private int neededCPU;

    /**
     * The needed amount of Memory resources.
     */
    private double neededMEM;

    /**
     * The actual task state.
     */
    private Protos.TaskState status;

    /**
     * The assigned slave this task will run on.
     */
    private Protos.Offer assignedSlave;

    /**
     * The task queue priority.
     */
    private double priority = 0.0;

    /**
     * The number of waitingtickets. Equals the number of times this task
     * couldn't be run.
     */
    private int waitingTickets = 0;

    /**
     * Number of times this task has failed.
     */
    private int executionErrors = 0;

    @Override
    public Task createTask(String dockerImage, int maxCPU, int maxMEM, String principal, Volumes volumes, String... arg) {
        Protos.TaskID taskId = Protos.TaskID.newBuilder()
                .setValue(principal + "_" + UUID.randomUUID().toString()).build();

        /**
         * DockerContainer Builder.
         */
        Protos.ContainerInfo.DockerInfo.Builder dockerInfoBuilder = Protos.ContainerInfo.DockerInfo.newBuilder();
        dockerInfoBuilder.setImage(dockerImage);
        dockerInfoBuilder.setNetwork(Protos.ContainerInfo.DockerInfo.Network.BRIDGE);
        /**
         * Container Builder.
         */
        Protos.ContainerInfo.Builder containerInfoBuilder = Protos.ContainerInfo.newBuilder();
        containerInfoBuilder.setType(Protos.ContainerInfo.Type.DOCKER);
        containerInfoBuilder.setDocker(dockerInfoBuilder.build());
        // Mount volumes if needed
        for (IVolume volume : volumes.getVolumes()) {
            containerInfoBuilder.addVolumes(volume.getProtosVolume());
        }
        /**
         * Task Builder.
         */
        Protos.TaskInfo task = Protos.TaskInfo.newBuilder()
                .setName("task " + taskId.getValue())
                .setTaskId(taskId)
                .addResources(Protos.Resource.newBuilder()
                        .setName("cpus")
                        .setType(Protos.Value.Type.SCALAR)
                        .setScalar(Protos.Value.Scalar.newBuilder().setValue(maxCPU)))
                .addResources(Protos.Resource.newBuilder()
                        .setName("mem")
                        .setType(Protos.Value.Type.SCALAR)
                        .setScalar(Protos.Value.Scalar.newBuilder().setValue(maxMEM)))
                .setContainer(containerInfoBuilder)
                .setCommand(Protos.CommandInfo.newBuilder().setShell(false).build())
                .buildPartial(); // partialBuild, because we'll add the slaveID later

        taskContent = Protos.TaskInfo.newBuilder(task).mergeCommand(Protos.CommandInfo.newBuilder().addAllArguments(Arrays.asList(arg)).setShell(false).build()).buildPartial();

        this.neededCPU = (int) getResource("cpus", taskContent.getResourcesList());
        this.neededMEM = (int) getResource("mem", taskContent.getResourcesList());

        return this;

    }

    @Override
    public Task createTask(String dockerImage, int maxCPU, int maxMEM, String principal, String... arg) {
        return this.createTask(dockerImage, maxCPU, maxMEM, principal, new Volumes(), arg);
    }

    @Override
    public DockerTask calculatePriority(int numberOfSlaves, Protos.Offer currentSlave) {
        priority = (numberOfSlaves / ((getResource("cpus", currentSlave.getResourcesList()) / this.neededCPU)
                + (getResource("mem", currentSlave.getResourcesList()) / this.neededMEM))) + this.waitingTickets;
        return this;
    }

    /**
     * @param type      - 'mem' , 'cpus'
     * @param resources
     * @return -1 if error double if default
     */
    private double getResource(String type, List<Protos.Resource> resources) {
        for (Protos.Resource r : resources) {
            if (r.getName().toLowerCase().equals(type)) {
                return r.getScalar().getValue();
            }
        }
        return -1;
    }

    @Override
    public DockerTask prepareToRun(Protos.Offer slave) {
        this.assignedSlave = slave;
        taskContent = TaskInfo.newBuilder(taskContent).mergeSlaveId(slave.getSlaveId()).build();
        startTime = new Date();
        return this;
    }

    @Override
    public TaskInfo getTaskContent() {
        return taskContent;
    }

    public int getNeededCPU() {
        return neededCPU;
    }

    public double getNeededMEM() {
        return neededMEM;
    }

    public Protos.TaskState getStatus() {
        return status;
    }

    public void setStatus(Protos.TaskState state) {
        this.status = state;
    }

    public Protos.Offer getAssignedSlave() {
        return assignedSlave;
    }

    public double getPriority() {
        return priority;
    }

    public int getWaitingTickets() {
        return waitingTickets;
    }

    public int getExecutionErrors() {
        return executionErrors;
    }

    public void setExecutionErrors(int executionErrors) {
        this.executionErrors = executionErrors;
    }

    public long getRuntimeSeconds() {
        long diff = new Date().getTime() - startTime.getTime();
        return diff / 1000 % 60;
    }

    public long getRuntimeMinutes() {
        long diff = new Date().getTime() - startTime.getTime();
        return diff / (60 * 1000) % 60;
    }
}