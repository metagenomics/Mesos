package de.cebitec.mesos.volumes;

import org.apache.mesos.Protos;

public interface IVolume {

    public Protos.Volume getProtosVolume();

}
