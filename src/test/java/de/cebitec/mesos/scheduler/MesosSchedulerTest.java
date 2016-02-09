package de.cebitec.mesos.scheduler;

import de.cebitec.mesos.framework.FrameworkDescriptor;
import de.cebitec.mesos.tasks.DockerTask;
import de.cebitec.mesos.tasks.Task;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import org.junit.*;
import util.PropertyStore;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author jsteiner
 */
public class MesosSchedulerTest {

    private SimpleMesosScheduler scheduler;
    private final PropertyStore store;
    private FrameworkDescriptor descriptor;

    public MesosSchedulerTest() {
        store = new PropertyStore();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
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
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testResourceOffers() {

        scheduler.startDriver();
        // ############## Image,mem,cpu,   user   , mounts,mounts
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
        boolean expected = true, result = scheduler.getPendingTasks().isEmpty()
                && scheduler.getRunningTasks().size() == 0; // use-only with no mesos cluster

        scheduler.stopDriver();
        assertEquals(expected, result);
    }

    /**
     * Test of statusUpdate method, of class MesosScheduler.
     */
    @Test
    public void testStatusUpdate() {
        System.out.println("statusUpdate");
        SchedulerDriver driver = null;
        Protos.TaskStatus taskStatus = null;
        System.out.println("Not implemenlted yet");
    }

}
