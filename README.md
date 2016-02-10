[![Release](https://jitpack.io/v/metagenomics/Mesos.svg)](https://jitpack.io/#metagenomics/Mesos) [![Circle CI](https://circleci.com/gh/metagenomics/Mesos/tree/master.svg?style=svg)](https://circleci.com/gh/metagenomics/Mesos/tree/master)

## Mesos Scheduler

### Usage

For code examples, please look at our [Test cases](src/test/java/de/cebitec/mesos/scheduler).

### Install Maven dependency

Click on the above jitpack button for an installation description of the current Maven Mesos artifact.

### Run Unit Tests

#### Example with local Mesos installation and one slave

1. Install Mesos: http://mesos.apache.org/gettingstarted/


2. Start Mesos Master

  ~~~BASH
  sudo nohup mesos-master --ip=127.0.0.1  --work_dir=/tmp --log_dir=/tmp > mesos-master.log 2>&1 &
  ~~~

3. Start Mesos Slave 

  ~~~BASH
  sudo nohup mesos-slave --master=127.0.0.1:5050  --containerizers=mesos,docker > mesos-slave.log 2>&1 &
  ~~~

4. Run Maven Tests

  ~~~BASH
  sudo mvn clean compile test
  ~~~

###### Optional: Test Configuration

You can configure the test in [this](src/test/resources/mesosConf.properties) property file.
