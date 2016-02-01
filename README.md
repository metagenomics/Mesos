## Mesos Scheduler

### Run Unit Tests

#### Example with local Mesos installation and one slave

1. Install Mesos: http://mesos.apache.org/gettingstarted/


2. Start Mesos Master

  ~~~BASH
  nohup mesos-master --ip=127.0.0.1  --work_dir=/tmp --log_dir=/tmp > mesos-master.log 2>&1 &
  ~~~

3. Start Mesos Slave 

  ~~~BASH
  nohup mesos-slave --master=127.0.0.1:5050 > mesos-slave.log 2>&1 &
  ~~~

4. Run Maven Tests

  ~~~BASH
  mvn clean compile test
  ~~~

