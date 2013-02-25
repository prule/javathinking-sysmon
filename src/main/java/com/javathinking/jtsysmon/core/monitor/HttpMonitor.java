package com.javathinking.jtsysmon.core.monitor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import javax.persistence.Transient;

/**
 * @author prule
 */
public class HttpMonitor implements Monitor {
    @Transient
    private static Logger log = Logger.getLogger(HttpMonitor.class);

    private String url;
    private String checkFor;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCheckFor() {
        return checkFor;
    }

    public void setCheckFor(String checkFor) {
        this.checkFor = checkFor;
    }


    public MonitorResult poll() {
        MonitorResult result = new MonitorResult();

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        try {

            int response = client.executeMethod(method);
            log.debug("http response =" + response);
            if (response != 200) {
                result.addFailure("Response code was not 200 (was " + response + ")");
            }

            if (checkFor != null) {
                String body = method.getResponseBodyAsString();
                if (body.indexOf(checkFor) == -1) {
                    result.addFailure("Text not found in response (" + checkFor + ")");
                }
            }

        } catch (Exception e) {
            result.addError(e.getMessage());
            log.error(e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }

        return result;
    }
}
