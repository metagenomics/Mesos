package de.cebitec.mesos.framework;

public class FrameworkDescriptor implements IFramework{

    private String hostname;

    private String frameworkName;

    private String userName;

    private String masterIp;

    private String masterPort;

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getFrameworkName() {
        return frameworkName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getMasterIp() {
        return masterIp;
    }

    @Override
    public String getMasterPort() {
        return masterPort;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setFrameworkName(String frameworkName) {
        this.frameworkName = frameworkName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public void setMasterPort(String masterPort) {
        this.masterPort = masterPort;
    }
}
