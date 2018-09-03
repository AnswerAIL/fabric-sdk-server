package com.hyperledger.fabric.sdk.entity;

/**
 * Created by L.Answer on 2018-09-03 10:56
 */
public enum ExecuteTypeEnum {
    /** 创建通道 */
    CREATE_CHANNEL("createChannel"),
    /** 部署/安装智能合约 */
    INSTALL_CC("installChainCode"),
    /** 加入节点 */
    JOIN_NODE("joinNode"),
    /** 初始化智能合约 */
    INITITAL_CC("inititalChainCode"),
    /** 查询智能合约 */
    QUERY_CC("queryChainCode"),
    /** 操作智能合约 */
    INVOKE_CC("invokeChainCode"),
    /** 升级智能合约 */
    UPGRADE_CC("upgradeChainCode");


    private String name;

    ExecuteTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}