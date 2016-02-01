/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mesos.comparator;

import java.util.Comparator;
import de.cebitec.mesos.tasks.DockerTask;

/**
 *
 * @author jsteiner
 */
public class DockerTaskComparator implements Comparator<DockerTask> {

    @Override
    public int compare(DockerTask t, DockerTask t1) {
        if (t.getPriority() < t1.getPriority()) {
            return 1;
        } else if (t.getPriority() > t1.getPriority()) {
            return -1;
        } else {
            return 0;
        }
    }

}
