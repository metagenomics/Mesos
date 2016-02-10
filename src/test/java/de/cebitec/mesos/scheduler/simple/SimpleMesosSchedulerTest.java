package de.cebitec.mesos.scheduler.simple;

import de.cebitec.mesos.framework.FrameworkDescriptor;
import de.cebitec.mesos.scheduler.SimpleMesosScheduler;
import de.cebitec.mesos.state.IStateListener;
import de.cebitec.mesos.tasks.DockerTask;
import de.cebitec.mesos.tasks.Task;
import org.junit.*;
import org.junit.Test;
import util.PropertyStore;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 *This test shows an example usage of the Mesos Scheduler.
 *
 * @author pbelmann
 */
public class SimpleMesosSchedulerTest {

    private SimpleMesosScheduler scheduler;

    private final PropertyStore store;

    private FrameworkDescriptor descriptor;

    public SimpleMesosSchedulerTest() {
        store = new PropertyStore();
    }

    @Before
    public void setUp() {
        descriptor = new FrameworkDescriptor();
        descriptor.setFrameworkName(store.getProperty("framework.name"));
        descriptor.setHostname(store.getProperty("master.hostname"));
        descriptor.setUserName(store.getProperty("master.user"));
        descriptor.setMasterIp(store.getProperty("master.ip"));
        descriptor.setMasterPort(store.getProperty("master.port"));
        scheduler = new SimpleMesosScheduler(descriptor);
        scheduler.startDriver();
    }

    @After
    public void tearDown() {
        scheduler.stopDriver();
    }

    @Test
    public void canExecuteSimpleTask() {

        Task task1 = new DockerTask();
        Task task2 = new DockerTask();
        Task task3 = new DockerTask();
        Task task4 = new DockerTask();

        task1.createTask("busybox", 2, 2, "root");
        task2.createTask("busybox", 1, 1, "root");
        task3.createTask("busybox", 3, 2, "root");
        task4.createTask("busybox", 1, 4, "root");

        scheduler.runTask(task1);
        scheduler.runTask(task2);
        scheduler.runTask(task3);
        scheduler.runTask(task4);

        while(scheduler.getRunningTasks().size() +
                scheduler.getStagingTasks().size()  +
                scheduler.getPendingTasks().size() != 0){
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        boolean noPendingTasks = scheduler.getPendingTasks().isEmpty();
        boolean noRunningTasks = scheduler.getRunningTasks().isEmpty();
        boolean noStagingTasks = scheduler.getStagingTasks().isEmpty();
        boolean noFailedTasks  = scheduler.getFailedTasks().isEmpty();
        int finishedTasks = scheduler.getFinishedTasks().size();

        assertTrue(noPendingTasks);
        assertTrue(noRunningTasks);
        assertTrue(noFailedTasks);
        assertTrue(noStagingTasks);
        assertEquals(4, finishedTasks);
    }

    @Test
    public void canScheduleWithListener() {

        StateListener listener = new StateListener();

        scheduler.addListener(listener);

        Task task1 = new DockerTask();
        Task task2 = new DockerTask();
        Task task3 = new DockerTask();
        Task task4 = new DockerTask();

        task1.createTask("busybox", 2, 2, "root");
        task2.createTask("busybox", 1, 1, "root");
        task3.createTask("busybox", 3, 2, "root");
        task4.createTask("busybox", 1, 4, "root");

        scheduler.runTask(task1);
        scheduler.runTask(task2);
        scheduler.runTask(task3);
        scheduler.runTask(task4);

        while(scheduler.getRunningTasks().size() +
                scheduler.getStagingTasks().size()  +
                scheduler.getPendingTasks().size() != 0){
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        int expectedFinishedTasks = 4;
        int tasksDetectedByTheListener = listener.getFinishedCounter();
        assertEquals(expectedFinishedTasks, tasksDetectedByTheListener);
    }

    public class StateListener implements IStateListener {

        private int finishedCounter;

        public StateListener(){
            this.finishedCounter = 0;
        }

        @Override
        public void onRunning(Task runningTask) {
        }

        @Override
        public void onExecutorFailure(Task runningTask) {
        }

        @Override
        public void onFinished(Task finishedTask) {
            this.finishedCounter++;
        }

        @Override
        public void onUndefinedFailure(Task failedTask) {
        }

        public int getFinishedCounter() {
            return finishedCounter;
        }
    }
}