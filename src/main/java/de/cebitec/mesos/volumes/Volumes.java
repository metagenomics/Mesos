package de.cebitec.mesos.volumes;

import java.util.ArrayList;
import java.util.List;

public class Volumes {

    private List<IVolume> volumes;

    public Volumes() {
        this.volumes = new ArrayList<>();
    }

    public Volumes(List<IVolume> volumes) {
        this.volumes = volumes;
    }

    public List<IVolume> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<IVolume> volumes) {
        this.volumes = volumes;
    }
}
