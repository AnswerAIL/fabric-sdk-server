package com.hyperledger.fabric.sdk.entity;

import lombok.Data;

import java.util.List;

/**
 * Created by L.Answer on 2018-11-30 15:01
 */
@Data
public class TxInfo {
    private String txID;
    private Long blockNum;
    private String mspID;
    private String createTime;
    /** 交易类型 */
    private String businessType;
    /** 参数个数 */
    private Integer paramsLen;
    /** 背书节点个数 */
    private Integer epLen;
    private String namespace;
    private List<Pair> pairs;

    @Data
    public static class Pair {
        private String key;
        private String value;
    }

}