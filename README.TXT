JTSYSMON
========

JtSysMon is a very simple monitoring tool which will poll resources and time the response.
The response time is logged to a database, and from there, you can see which resources are
either slow or failing/erroring.

I wrote this because I needed a way to make sure that various resources were available and
responsive during development, so that we could respond quickly (proactively) to problems
and have them fixed before developers or testers noticed or wasted time fault finding.

Existing solutions (Nagios, Zenoss etc) seemed too complex for such a simple requirement,
with too much of a learning curve and setup required for essentially what is polling HTTP
and SQL (please correct me if I am wrong).

Install
-------

Extract the binary archive so that you have a directory structure like:

- jtsysmon
  |- bin
  |- conf
  |- lib

By default, an embedded instance of the DERBY database will be used to store the configuration
and poll results. If you want, you can edit conf/database.properties to point to another database.
If you do, you will have to add the database driver jar file to the lib dir.

Configure
---------

JtSysMon currently comes with 2 monitor types - SqlMonitor and HttpMonitor.

AFTER EITHER ADDING OR DELETING A MONITOR, YOU MUST RESTART.

To add a monitor, specify the class, a name, and then properties of the class as required. Reflection
is used to set the properties of the class:

  sh run.sh add name=sql1 class=com.javathinking.jtsysmon.core.monitor.SqlMonitor driverClass=org.hibernate.dialect.DerbyDialect connectionString=jdbc:derby://localhost:1527/jtsysmon "sql=select count(*) from APP.MONITORCONFIG"
  sh run.sh add name=http1 class=com.javathinking.jtsysmon.core.monitor.HttpMonitor url=http://localhost/ checkFor=Hello

To list the monitors, use:

  sh run.sh -list

  Listing monitors

    1048576 sql1
      class = class com.javathinking.jtsysmon.core.monitor.SqlMonitor
      connectionString = jdbc:derby://localhost:1527/jtsysmon
      driverClass = org.hibernate.dialect.DerbyDialect
      sql = select count(*) from APP.MONITORCONFIG

To delete a monitor, use delete, with the ID of the monitor (shown in the listing above):

  sh run.sh -delete 1048576

To start monitoring, use:

  sh run.sh -start

Use CTRL-C to stop.

To view alarms (where the time taken to perform poll exceeds a specified value) or problems (where
the poll failed or errored) use:

  sh run.sh -alarms -threshold 5 -period 20

    Listing events over last 20 minutes
     ALARMS (duration > 5)
      19:08:18 paul-laptop1 sql1 28ms SUCCESS

     PROBLEMS (non-successful events)
      19:08:21 paul-laptop1 http1 90ms  ERROR

Implementing more monitor types
-------------------------------
If you need to implement another monitor, start by looking at HttpMonitor and SqlMonitor:

  http://code.google.com/p/javathinking/source/browse/trunk/javathinking-sysmon/src/main/java/com/javathinking/jtsysmon/core/monitor/HttpMonitor.java
  http://code.google.com/p/javathinking/source/browse/trunk/javathinking-sysmon/src/main/java/com/javathinking/jtsysmon/core/monitor/SqlMonitor.java

Provide getters and setters for your monitors properties, and implement the poll() method. 

Summary
-------

So there you have it - a bare bones way to check connectivity, response times, and availability
of resources in your environment. This is meant only as a development tool, NOT for monitoring
production resources.

Futures
-------

If there is any interest from the community in using this tool, you could easily imagine:
- multiple agents logging to a central database
- more monitors
-- snmp
-- ssh
-- scripts
- a web client 
-- to display alarms and graphs of response times
-- to configure the monitors
- notifiers to alert users of problems

