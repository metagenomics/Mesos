package de.cebitec.mesos.comparator;

import de.cebitec.mesos.framework.FrameworkDescriptor;
import de.cebitec.mesos.scheduler.SimpleMesosScheduler;
import de.cebitec.mesos.tasks.DockerTask;
import de.cebitec.mesos.tasks.Task;
import org.apache.mesos.Protos;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.PropertyStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author pbelmann
 */
public class DockerTaskComparatorTest {


    private SimpleMesosScheduler scheduler;

    private static PropertyStore store;

    private FrameworkDescriptor descriptor;

    @BeforeClass
    public static void initPropertyStore(){
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

    /**
     * Test of compare method, of class DockerTaskComparator.
     */
    @Test
    public void testCompare() {

        Task task1 = new DockerTask().createTask("busybox", 2, 2, "root");
        Task task2 = new DockerTask().createTask("busybox", 1, 1, "root");
        Task task3 = new DockerTask().createTask("busybox", 1, 4, "root");
        Task highestPriorityTask = new DockerTask().createTask("busybox", 3, 2, "root");
        Protos.TaskID highestPriorityTaskId = highestPriorityTask.getTaskContent().getTaskId();

        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(highestPriorityTask);

        for (Task t : tasks) {
            t.calculatePriority(2, Protos.Offer.newBuilder()
                    .addResources(Protos.Resource.newBuilder()
                            .setName("cpus")
                            .setType(Protos.Value.Type.SCALAR)
                            .setScalar(Protos.Value.Scalar.newBuilder().setValue(4)))
                    .addResources(Protos.Resource.newBuilder()
                            .setName("mem")
                            .setType(Protos.Value.Type.SCALAR)
                            .setScalar(Protos.Value.Scalar.newBuilder().setValue(4)))
                    .setId(Protos.OfferID.newBuilder().setValue("offerId").build())
                    .setFrameworkId(Protos.FrameworkID.newBuilder().setValue("klkklkkll").build())
                    .setSlaveId(Protos.SlaveID.newBuilder().setValue("12345z6t").build())
                    .setHostname("hostname")
                    .build());
        }
        Collections.sort(tasks, new TaskComparator());
        Task selected = tasks.get(0);
        Protos.TaskID expectedHighestPriorityTaskID = selected.getTaskContent().getTaskId();

        assertEquals(highestPriorityTaskId, expectedHighestPriorityTaskID);
    }
}