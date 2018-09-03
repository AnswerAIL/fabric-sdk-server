package com.hyperledger.fabric.sdk.logger;


import static com.hyperledger.fabric.sdk.logger.LogLevels.*;

/**
 * Created by answer on 2018-09-03 11:18
 */
public enum LogLevel {
    DEBUG(DEBUG_LEVEL, "DEBUG"),
    INFO(INFO_LEVEL, "INFO"),
    WARN(WARN_LEVEL, "WARN"),
    ERROR(ERROR_LEVEL, "ERROR");

    private  int level;
    private String name;

    LogLevel(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }


}