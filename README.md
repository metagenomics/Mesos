[![Release](https://jitpack.io/v/metagenomics/Mesos.svg)](https://jitpack.io/#metagenomics/Mesos)

## Mesos Scheduler

### Install

Add jitpack repository:

~~~XML
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
~~~

and dependency:

~~~XML
<dependency>
	    <groupId>com.github.metagenomics</groupId>
	    <artifactId>Mesos</artifactId>
	    <version>0.1.0-alpha</version>
</dependency>
~~~

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
  mvn clean compile test
  ~~~

###### Optional: Test Configuration

You can configure the test in [this](src/test/resources/mesosConf.properties) property file.
