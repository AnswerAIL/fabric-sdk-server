package com.hyperledger.fabric.sdk.entity;

/**
 * Created by L.Answer on 2018-09-03 10:56
 */
public enum ExecuteTypeEnum {
    /** 创建通道 */
    CREATE_CHANNEL("CreateChannel"),
    /** 部署/安装智能合约 */
    INSTALL_CC("InstallChainCode"),
    /** 加入节点 */
    JOIN_NODE("JoinNode"),
    /** 初始化智能合约 */
    INITITAL_CC("InititalChainCode"),
    /** 查询智能合约 */
    QUERY_CC("QueryChainCode"),
    /** 操作智能合约 */
    INVOKE_CC("InvokeChainCode"),
    /** 升级智能合约 */
    UPGRADE_CC("UpgradeChainCode");


    private String name;

    ExecuteTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}