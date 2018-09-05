package com.hyperledger.fabric.sdk.common;

/**
 * Created by L.Answer on 2018-09-05 17:06
 */
public class Config {

    /** 组织1-Peer节点配置信息 */
    public static final String PEER0_ORG1_NAME = "peer0.org1.example.com";
    public static final String PEER0_ORG1_GRPC_URL = "grpc://119.23.XXX.XXX:7051";
    public static final String PEER0_ORG1_EVENT_URL = "grpc://119.23.XXX.XXX:7053";

    public static final String PEER1_ORG1_NAME = "peer1.org1.example.com";
    public static final String PEER1_ORG1_GRPC_URL = "grpc://119.23.XXX.XXX:8051";
    public static final String PEER1_ORG1_EVENT_URL = "grpc://119.23.XXX.XXX:8053";


    /** 组织2-Peer节点配置信息 */
    public static final String PEER0_ORG2_NAME = "peer0.org2.example.com";
    public static final String PEER0_ORG2_GRPC_URL = "grpc://119.23.XXX.XXX:9051";
    public static final String PEER0_ORG2_EVENT_URL = "grpc://119.23.XXX.XXX:9053";

    public static final String PEER1_ORG2_NAME = "peer1.org2.example.com";
    public static final String PEER1_ORG2_GRPC_URL = "grpc://119.23.XXX.XXX:10051";
    public static final String PEER1_ORG2_EVENT_URL = "grpc://119.23.XXX.XXX:10053";


    /** Order节点配置信息 */
    public static final String ORDER_NAME = "orderer.example.com";
    public static final String ORDER_GRPC_URL = "grpc://119.23.XXX.XXX:7050";
}