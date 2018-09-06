package com.hyperledger.fabric.sdk.common;

/**
 * Created by L.Answer on 2018-09-03 14:36
 */
public class Constants {

    /** Redis服务器IP地址 */
    public static final String REDIS_IP = "127.0.0.1";
    /** 需要指定redis端口请指定具体端口值, 否则设为-1使用默认端口: 6379 */
    public static final int REDIS_PORT = -1;
    /** Redis库索引号 */
    public static final int REDIS_INDEX = 0;
    /** 如果需要密码登录, 请设置密码, 否则请置空 */
    public static final String REDIS_PASSWD = "";
    /** redis缓存中key的前缀 */
    public static final String REDIS_PREFIX = "hyperledger:fabric:cache:";


    /** 交易等待时间, 单位: ms */
    public static final int PROPOSAL_WAIT_TIME = 60000;

}