package com.hyperledger.fabric.sdk.common;

/**
 * Created by L.Answer on 2018-09-03 14:36
 *
 * 以下常量
 *      REDIS_PREFIX
 *      PROPOSAL_WAIT_TIME
 *      SERIAL_CHANNEL_TO_FILE
 *      CHANNEL_SERIAL_PATH
 *  没有特殊情况或强迫症患者, 可无需修改其值
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

    /** 是否将通道序列化到本地磁盘文件 */
    public static final boolean SERIAL_CHANNEL_TO_FILE = false;
    /** 将通道对象序列化到本地磁盘文件路径, 为空时默认路径: 当前项目根目录下 */
    public static final String CHANNEL_SERIAL_PATH = "";

}