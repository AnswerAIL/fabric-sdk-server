package com.hyperledger.fabric.sdk.entity;

import lombok.Getter;

/**
 * Created by L.Answer on 2019-01-16 19:01
 */
public enum RequestEnum {
    /** update */
    UPGRADE_PROPOSAL_REQUEST("UpgradeProposalRequest"),
    /** initialize */
    INSTANTIATE_PROPOSAL_REQUEST("InstantiateProposalRequest"),
    /** query */
    QUERYBY_CHAINCODE_REQUEST("QueryByChaincodeRequest"),
    /** invoke */
    TRANSACTION_PROPOSAL_REQUEST("TransactionProposalRequest");

    @Getter
    private String name;

    RequestEnum(String name) {
        this.name = name;
    }
}