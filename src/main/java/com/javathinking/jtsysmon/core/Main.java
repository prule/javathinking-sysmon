package com.javathinking.jtsysmon.core;

import com.javathinking.jtsysmon.core.cli.CliUi;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author prule
 */
public class Main {

    static MonitorConfigDao monitorDao;
    private static ApplicationContext context;

    public static void main(String[] args) throws Exception {
//        go(new String[] {"add","name=sql1","class=com.javathinking.jtsysmon.core.monitor.SqlMonitor","driverClass=org.hibernate.dialect.DerbyDialect","connectionString=jdbc:derby://localhost:1527/jtsysmon","sql=select count(*) from APP.MONITORCONFIG"});
//        go(new String[] {"add","name=http1","class=com.javathinking.jtsysmon.core.monitor.HttpMonitor","url=http://localhost/"});
        go(new String[]{"-list", "-start", "-timeout", "5000", "-wait", "30000"});
//        go(new String[] {"-alarms","-threshold","5", "-list"});
//        go(new String[] {"-list"});
//        go(new String[] {"-delete","1081344"});
//        go(new String[] {"-list"});
    }

    private static void go(String[] args) {
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        CliUi cli = (CliUi) context.getBeansOfType(CliUi.class).values().toArray()[0];

        if (args != null && args.length > 0) {
            if ("add".equals(args[0])) {
                cli.add(args);
            } else {
                cli.process(args);
            }
        } else {
            cli.help();
        }
    }
}
