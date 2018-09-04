package com.hyperledger.fabric.sdk.common;

/**
 * Created by L.Answer on 2018-09-03 14:36
 */
public class Constants {

    public static final String REDIS_IP = "127.0.0.1";
    /** 需要指定redis端口请指定具体端口值, 否则设为-1使用默认端口 */
    public static final int REDIS_PORT = -1;
    public static final int REDIS_INDEX = 0;
    public static final String REDIS_PASSWD = "";
    /** redis缓存中key的前缀 */
    public static final String REDIS_PREFIX = "hyperledger:fabric:cache:";


    /** 交易等待时间, 单位: ms */
    public static final int PROPOSAL_WAIT_TIME = 30000;

}