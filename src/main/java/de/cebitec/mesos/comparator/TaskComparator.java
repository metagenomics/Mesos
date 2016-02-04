package de.cebitec.mesos.comparator;

import java.util.Comparator;
import de.cebitec.mesos.tasks.Task;

/**
 *
 * @author jsteiner
 */
public class TaskComparator implements Comparator<Task> {

    @Override
    public int compare(Task t, Task t1) {
        if (t.getPriority() < t1.getPriority()) {
            return 1;
        } else if (t.getPriority() > t1.getPriority()) {
            return -1;
        } else {
            return 0;
        }
    }
}
