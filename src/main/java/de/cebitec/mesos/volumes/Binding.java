package de.cebitec.mesos.volumes;

import org.apache.mesos.Protos;

public class Binding implements IVolume {

    private final Protos.Volume.Builder builder;

    enum PERMISSION {
        RW,
        RO
    }

    public Binding(String containerPath, PERMISSION permission) {
        builder = Protos.Volume.newBuilder().setContainerPath(containerPath)
                .setMode(permission == PERMISSION.RW ? Protos.Volume.Mode.RW : Protos.Volume.Mode.RO);
    }

    public Binding(String containerPath, String hostPath, PERMISSION permission) {
        builder = Protos.Volume.newBuilder().setContainerPath(containerPath)
                .setHostPath(hostPath)
                .setMode(permission == PERMISSION.RW ? Protos.Volume.Mode.RW : Protos.Volume.Mode.RO);
    }

    @Override
    public Protos.Volume getProtosVolume() {
        return builder.build();
    }
}
