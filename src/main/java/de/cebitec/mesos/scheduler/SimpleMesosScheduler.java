/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.cebitec.mesos.scheduler;

import de.cebitec.mesos.comparator.TaskComparator;
import de.cebitec.mesos.framework.IFramework;
import de.cebitec.mesos.tasks.Task;
import org.apache.mesos.Protos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Example scheduler to launch Docker containers.
 */
public class SimpleMesosScheduler extends AMesosScheduler {


    private List<Task> pendingTasks;
    private List<Task> stagingTasks;
    private List<Task> runningTasks;
    private List<Task> finishedTasks;
    private List<Task> failedTasks;
    private List<Task> allTasks;

    public SimpleMesosScheduler(IFramework framework) {
        super(framework);
        pendingTasks = new ArrayList<>();
        stagingTasks = new ArrayList<>();
        runningTasks = new ArrayList<>();
        finishedTasks = new ArrayList<>();
        failedTasks = new ArrayList<>();
        allTasks = new ArrayList<>();
    }

    @Override
    protected void handleResources(List<Protos.Offer> offers) {

        /**
         * Adding rule to devide resources. Maybe ( numberOfOffers / #containers
         * ) = amount per offer
         */
        for (final Protos.Offer offer : offers) {

            if (pendingTasks.isEmpty()) {

                stagingTasks.clear();
                declineOffer(offer);

            } else {

                for (Task d : pendingTasks) {
                    d.calculatePriority(offers.size(), offer); // calculate priority
                }

                Collections.sort(pendingTasks, new TaskComparator()); // sort by priority

                double available_CPU = super.getResourcesCpus(offer); // slave cpu
                double available_MEM = super.getResourcesMem(offer); // slave mem

                stagingTasks.clear();

                while (!pendingTasks.isEmpty()) {

                    Task actualTask = pendingTasks.get(0);

                    double needed_CPU = actualTask.getNeededCPU();
                    double needed_MEM = actualTask.getNeededMEM();

                    if (available_CPU >= needed_CPU
                            && available_MEM >= needed_MEM) {

                        available_CPU -= needed_CPU;
                        available_MEM -= needed_MEM;

                        pendingTasks.remove(actualTask);
                        stagingTasks.add(actualTask.prepareToRun(offer));

                        logger.debug("Added Task: " + actualTask.getTaskContent().getTaskId().getValue());
                    } else {
                        logger.warn("BREAK: Task execution not possible. Offer resources exhausted");
                        break;
                    }
                }
                if (!stagingTasks.isEmpty()) {

                    Protos.Filters filters = Protos.Filters.newBuilder().setRefuseSeconds(10).build();
                    List<Protos.TaskInfo> tmp = new ArrayList<>();

                    for (Task t : stagingTasks) {
                        tmp.add(t.getTaskContent());
                        runningTasks.add(t);
                    }
                    launchTask(offer, tmp, filters);

                    logger.info("Started Tasks: {} on Slave: ({})", tmp.size(), offer.getId().getValue());
                } else {
                    declineOffer(offer);
                }
            }
        }
    }

    @Override
    public Protos.TaskInfo addTask(Task task) {
        allTasks.add(task);
        pendingTasks.add(task);
        logger.info("PendingTasks Size {}", pendingTasks.size());
        return task.getTaskContent();
    }

    @Override
    public Task getTask(Protos.TaskID taskId) {
        for (Task t : allTasks) {
            if (t.getTaskContent().getTaskId().equals(taskId)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void onRunning(Task runningTask) {
    }

    @Override
    public void onExecutorFailure(Task actualTask) {
        pendingTasks.add(actualTask);
        runningTasks.remove(actualTask);
    }

    @Override
    public void onFinished(Task actualTask) {
        finishedTasks.add(actualTask);
        runningTasks.remove(actualTask);
    }

    @Override
    public void onUndefinedFailure(Task failedTask) {
        runningTasks.remove(failedTask);
        pendingTasks.remove(failedTask);
    }

    public List<Task> getPendingTasks() {
        return pendingTasks;
    }

    public List<Task> getStagingTasks() {
        return stagingTasks;
    }

    public List<Task> getRunningTasks() {
        return runningTasks;
    }

    public List<Task> getFinishedTasks() {
        return finishedTasks;
    }

    public List<Task> getFailedTasks() {
        return failedTasks;
    }
}
