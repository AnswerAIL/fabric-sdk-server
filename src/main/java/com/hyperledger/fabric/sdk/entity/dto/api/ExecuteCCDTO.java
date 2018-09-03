package com.hyperledger.fabric.sdk.entity.dto.api;

import com.hyperledger.fabric.sdk.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.ChaincodeID;

/**
 * Created by L.Answer on 2018-09-03 16:20
 *
 * 操作(查询|交易)智能合约数据传输对象
 */
public class ExecuteCCDTO {
    private String funcName;
    private String[] params;
    private ChaincodeID chaincodeID;
    // 单位: ms
    private Integer proposalWaitTime;

    private ExecuteCCDTO(Builder builder) {
        this.funcName = builder.funcName;
        this.params = builder.params;
        this.chaincodeID = builder.chaincodeID;
        this.proposalWaitTime = builder.proposalWaitTime;
    }

    public String getFuncName() {
        return funcName;
    }

    public String[] getParams() {
        return params;
    }

    public ChaincodeID getChaincodeID() {
        return chaincodeID;
    }

    public Integer getProposalWaitTime() {
        if (proposalWaitTime == null || proposalWaitTime <= 0) {
            proposalWaitTime = Constants.PROPOSAL_WAIT_TIME;
        }
        return proposalWaitTime;
    }

    public static class Builder {
        private String funcName;
        private String[] params;
        private ChaincodeID chaincodeID;
        private Integer proposalWaitTime;

        public Builder funcName(String funcName) {
            this.funcName = funcName;
            return this;
        }

        public Builder params(String[] params) {
            this.params = params;
            return this;
        }

        public Builder chaincodeID(ChaincodeID chaincodeID) {
            this.chaincodeID = chaincodeID;
            return this;
        }

        public Builder proposalWaitTime(Integer proposalWaitTime) {
            this.proposalWaitTime = proposalWaitTime;
            return this;
        }

        public ExecuteCCDTO build() {
            if (StringUtils.isEmpty(funcName) || params == null || chaincodeID == null) {
                throw new IllegalArgumentException("funcName | params | chaincodeID must not be empty.");
            }
            return new ExecuteCCDTO(this);
        }
    }

}