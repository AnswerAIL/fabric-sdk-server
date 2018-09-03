package com.hyperledger.fabric.sdk.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by answer on 2018-09-03 11:18
 */
public class Logger {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static LogLevel loggerLevel = LogLevel.DEBUG;

    public static void debug(String log) {
        logger(LogLevel.DEBUG, Thread.currentThread().getStackTrace()[2], log);
    }

    public static void debug(String log, Object... args) {
        logger(LogLevel.DEBUG, Thread.currentThread().getStackTrace()[2], String.format(log, args));
    }

    public static void info(String log) {
        logger(LogLevel.INFO, Thread.currentThread().getStackTrace()[2], log);
    }

    public static void info(String log, Object... args) {
        logger(LogLevel.INFO, Thread.currentThread().getStackTrace()[2], String.format(log, args));
    }

    public static void warn(String log) {
        logger(LogLevel.WARN, Thread.currentThread().getStackTrace()[2], log);
    }

    public static void warn(String log, Object... args) {
        logger(LogLevel.WARN, Thread.currentThread().getStackTrace()[2], String.format(log, args));
    }

    public static void error(String log) {
        logger(LogLevel.ERROR, Thread.currentThread().getStackTrace()[2], log);
    }

    public static void error(String log, Object... args) {
        logger(LogLevel.ERROR, Thread.currentThread().getStackTrace()[2], String.format(log, args));
    }

    private static void logger(LogLevel logLevel, StackTraceElement stackTraceElement, String log) {
        if (loggerLevel.getLevel() <= logLevel.getLevel()) {
            System.out.println(logPrefix(logLevel) + stackTraceElement.getClassName() + ":[" + stackTraceElement.getLineNumber() + "] " + log);
        }
    }

    private static String logPrefix(LogLevel logLevel) {
        return "["+ logLevel.getName() +"] " + SDF.format(new Date()) + " ";
    }

    private static String logPrefix(Class clz) {
        return "[INFO] " + SDF.format(new Date()) + " " + clz.getName() + " ";
    }

    private static String logPrefix(Class clz, LogLevel logLevel) {
        return "["+ logLevel.getName() +"] " + SDF.format(new Date()) + " " + clz.getName() + " ";
    }

}