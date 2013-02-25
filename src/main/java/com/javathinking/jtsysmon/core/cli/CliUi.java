package com.javathinking.jtsysmon.core.cli;

import com.javathinking.commons.TextTable;
import com.javathinking.jtsysmon.core.*;
import com.javathinking.jtsysmon.core.monitor.Monitor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author prule
 */
@Component
public class CliUi {
    private static final Logger log = Logger.getLogger(CliUi.class);
    @Autowired
    private MonitorConfigDao monitorDao;
    @Autowired
    private PollService pollService;
    @Autowired
    private MonitorConfigService monitorConfigService;

    private static Options options = options();
    private static CommandLineParser parser = new PosixParser();
    private static final String LIST = "list";
    private static final String ADD = "add";
    private static final String ALARMS = "alarms";
    private static final String START = "start";
    private static final String THRESHOLD = "threshold";
    private static final String PERIOD = "period";
    private static final String DELETE = "delete";
    private static final String WAIT = "wait";
    private static final String TIMEOUT = "timeout";

    private static ApplicationContext context;

    public static void main(String[] args) {
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

    static Options options() {
        Options options = new Options();
        options.addOption(LIST, false, "List current monitors");
        options.addOption(ALARMS, false, "Show alarms");
        options.addOption(PERIOD, true, "Period to cover when showing alarms");
        options.addOption(THRESHOLD, true, "Minimum duration when showing alarms");
        options.addOption(DELETE, true, "Delete monitor by ID");
        options.addOption(START, false, "Start monitors (use with 'wait' and 'timeout')");
        options.addOption(WAIT, true, "Wait duration in milliseconds between each poll cycle");
        options.addOption(TIMEOUT, true, "Timeout duration in milliseconds for a poll");

        return options;
    }

    public void process(String[] params) {
        CommandLine cmd;

        try {
            cmd = parser.parse(options, params);
        } catch (org.apache.commons.cli.ParseException ex) {
            throw new RuntimeException(ex);
        }

        if (cmd.hasOption(LIST)) {
            list();
        }

        if (cmd.hasOption(DELETE)) {
            delete(cmd.getOptionValue(DELETE));
        }

        if (cmd.hasOption(START)) {
            long timeout = 10000;
            long wait = 120000;
            if (cmd.hasOption(WAIT)) {
                wait = Long.parseLong(cmd.getOptionValue(WAIT));
            }
            if (cmd.hasOption(TIMEOUT)) {
                timeout = Long.parseLong(cmd.getOptionValue(TIMEOUT));
            }
            pollService.start(wait, timeout);
        }


        if (cmd.hasOption(ALARMS)) {
            Long threshold = 20L;
            Integer minutes = 20;

            if (cmd.hasOption(THRESHOLD)) {
                threshold = Long.parseLong(cmd.getOptionValue(THRESHOLD));
            }
            if (cmd.hasOption(PERIOD)) {
                minutes = Integer.parseInt(cmd.getOptionValue(PERIOD));
            }

            showAlarms(threshold, minutes);
        }
    }

    public void list() {
        final List<MonitorConfig> list = monitorDao.list();
        System.out.println("\nListing monitors");
        for (MonitorConfig monitorConfig : list) {
            System.out.println("\n  " + monitorConfig.getId() + " " + monitorConfig.getName());
            try {
                final Map props = new TreeMap(BeanUtils.describe(monitorConfig.getMonitor()));
                for (Object key : props.keySet()) {
                    System.out.println("    " + key + " = " + props.get(key));
                }
            } catch (Exception ex) {
                log.error(ex);
            }
        }
    }

    public void add(String[] params) {
        Map<String, String> map = paramsToProps(params);
        Monitor monitor = mapToMonitor(map);
        final MonitorConfig monitorConfig = new MonitorConfig();
        monitorConfig.setName(map.get("name"));
        monitorConfig.setMonitor(monitor);
        monitorDao.save(monitorConfig);
    }

    public Map paramsToProps(String[] params) {
        Map<String, String> map = new HashMap();


        for (String s : params) {
            if (s.indexOf('=') > 0) {
                final String[] ss = StringUtils.split(s, '=');
                map.put(ss[0], ss[1]);


            }
        }
        return map;


    }

    public Monitor mapToMonitor(Map<String, String> map) {
        try {
            Monitor monitor = (Monitor) Class.forName(map.get("class")).newInstance();
            map.remove("class");
            BeanUtils.populate(monitor, map);
            return monitor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JtSysMon", options);
    }

    private void showAlarms(Long minDuration, int minutes) {
        final DateTime now = new DateTime();
        final DateTime start = now.minusMinutes(minutes);
        final List<PollResult> alarms = pollService.listAlarms(start.toDate(), now.toDate(), minDuration);
        final List<PollResult> problems = pollService.listProblems(start.toDate(), now.toDate());

        System.out.println("\nListing events over last " + minutes + " minutes");
        System.out.println(" ALARMS (duration > " + minDuration + ")");
        TextTable table = new TextTable("  ", TextTable.ALIGN.LEFT, TextTable.ALIGN.LEFT, TextTable.ALIGN.LEFT, TextTable.ALIGN.RIGHT, TextTable.ALIGN.CENTER);
        if (!alarms.isEmpty()) {
            for (PollResult pollResult : alarms) {
                table.add(format2(pollResult));
            }
            System.out.println(table.toString());
        } else {
            System.out.println("  No alarms");
        }
        table.clear();
        System.out.println(" PROBLEMS (non-successful events)");
        if (!problems.isEmpty()) {
            for (PollResult pollResult : problems) {
                table.add(format2(pollResult));
            }
            System.out.println(table.toString());
        } else {
            System.out.println("  No problems");
        }
    }

    private static FastDateFormat TIMEFORMAT = FastDateFormat.getInstance("HH:mm:ss");

    private Object[] format2(PollResult result) {
        Object[] data = new Object[5];
        data[0] = TIMEFORMAT.format(result.getStart());
        data[1] = result.getInstanceId();
        data[2] = result.getMonitorConfig().getName();
        data[3] = result.getDuration() + "ms";
        data[4] = result.getStatus();
        return data;
    }

    private void delete(String id) {
        System.out.println();
        final Long i = Long.valueOf(id);
        if (monitorConfigService.delete(i)) {
            System.out.println("Deleted monitor " + id);
        } else {
            System.out.println("Could not delete monitor " + id);
        }
    }
}
