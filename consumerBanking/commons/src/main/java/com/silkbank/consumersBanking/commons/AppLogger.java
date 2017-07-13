package com.silkbank.consumersBanking.commons;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Created by asad on 7/6/2017.
 */
public class AppLogger {

    private AppLogger() {
        BasicConfigurator.configure();
    }


    /**
     * @param appLoggerName: Application Name
     * @return instance of logger
     */
    public static Logger getLoggerInstance(String appLoggerName) {
        return Logger.getLogger(appLoggerName);
    }

}
