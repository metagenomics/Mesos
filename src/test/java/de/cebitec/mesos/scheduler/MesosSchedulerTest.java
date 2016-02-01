/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mesos.scheduler;

import de.cebitec.mesos.tasks.DockerTask;
import de.cebitec.mesos.tasks.Task;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author jsteiner
 */
public class MesosSchedulerTest {

    public MesosSchedulerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of resourceOffers method, of class MesosScheduler.
     */
    @Test
    public void testResourceOffers() {
        System.out.println("resourceOffers");

        SimpleMesosScheduler instance = new SimpleMesosScheduler();

        // ############## Image,mem,cpu,   user   , mounts,mounts
        Task task1 = new DockerTask();
        Task task2 = new DockerTask();
        Task task3 = new DockerTask();
        Task task4 = new DockerTask();
        task1.createTask(1, "test", 2, 2, "jsteiner1", null, null);
        task2.createTask(2, "test", 1, 1, "jsteiner2", null, null);
        task3.createTask(3, "test", 3, 2, "jsteiner3", null, null);
        task4.createTask(4, "test", 1, 4, "jsteiner4", null, null);
        instance.addTask(task1);
        instance.addTask(task2);
        instance.addTask(task3);
        instance.addTask(task4);

        List<Protos.Offer> offers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            offers.add(Protos.Offer.newBuilder()
                    .addResources(Protos.Resource.newBuilder()
                            .setName("cpus")
                            .setType(Protos.Value.Type.SCALAR)
                            .setScalar(Protos.Value.Scalar.newBuilder().setValue(4)))
                    .addResources(Protos.Resource.newBuilder()
                            .setName("mem")
                            .setType(Protos.Value.Type.SCALAR)
                            .setScalar(Protos.Value.Scalar.newBuilder().setValue(4)))
                    .setId(Protos.OfferID.newBuilder().setValue("dasdsadsdadsad").build())
                    .setFrameworkId(Protos.FrameworkID.newBuilder().setValue("klkklkkll").build())
                    .setSlaveId(Protos.SlaveID.newBuilder().setValue(i + " 12345z6t").build())
                    .setHostname("tetzkatlipoklan")
                    .build());
        }

        instance.resourceOffers(new MesosSchedulerDriver(instance,
                Protos.FrameworkInfo.newBuilder()
                .setHostname("test")
                .setName("tests")
                .setUser("jsteiner")
                .build(),
                "127.0.0.1:5050"), offers);

        System.out.println("Pending: " + instance.getPendingTasks().size());
        System.out.println("Running: " + instance.getRunningTasks().size());
        System.out.println("Finished: " + instance.getFinishedTasks().size());

        /**
         * Expected not empty running and empty pending list at the end to
         * ensure a correct working scheduler. There can't be a status update to
         * finished so far cause of a scheduler emulation only
         */
        boolean expected = true, result = instance.getPendingTasks().isEmpty()
                && instance.getRunningTasks().size() == 4; // use-only with no mesos cluster

        // use only with working mesos cluster!
//        boolean expected = true, result = instance.getRunningTasks().isEmpty()
//                && instance.getPendingTasks().isEmpty()
//                && !instance.getFinishedTasks().isEmpty();
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
//        MesosScheduler instance = new MesosScheduler();
//        instance.statusUpdate(driver, taskStatus);
        // TODO review the generated test code and remove the default call to fail.
        System.out.println("Not implemented yet");
    }

}
