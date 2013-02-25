package com.javathinking.jtsysmon.core.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author prule
 */
public class MonitorResult {

    private List<String> errors = new ArrayList<String>();
    private List<String> failures = new ArrayList<String>();
    private String timeout;

    public void addFailure(String message) {
        failures.add(message);
    }

    public void addError(String message) {
        errors.add(message);
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getFailures() {
        return failures;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public boolean hasTimedOut() {
        return timeout != null;
    }

    public boolean hasFailures() {
        return failures.size() > 0;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }


}
