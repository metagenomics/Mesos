package de.cebitec.mesos.state;

import de.cebitec.mesos.tasks.Task;

public interface IStateListener {

    public abstract void onRunning(Task runningTask);

    public abstract void onExecutorFailure(Task runningTask);

    public abstract void onFinished(Task finishedTask);

    public abstract void onUndefinedFailure(Task failedTask);

}
