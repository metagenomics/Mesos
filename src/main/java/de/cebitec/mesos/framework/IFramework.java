package de.cebitec.mesos.framework;


public interface IFramework {

    public String getHostname();
    
    public String getFrameworkName();

    public String getUserName();

    public String getMasterIp();

    public String getMasterPort();

}
