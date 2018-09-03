package com.hyperledger.fabric.sdk.entity.dto.api;

import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.ChaincodeID;

/**
 * Created by answer on 2018-09-03 16:20
 *
 * 查询智能合约数据传输对象
 */
public class QueryCCDTO {
    private String funcName;
    private String[] params;
    private ChaincodeID chaincodeID;

    private QueryCCDTO(Builder builder) {
        this.funcName = builder.funcName;
        this.params = builder.params;
        this.chaincodeID = builder.chaincodeID;
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

    public static class Builder {
        private String funcName;
        private String[] params;
        private ChaincodeID chaincodeID;

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

        public QueryCCDTO build() {
            if (StringUtils.isEmpty(funcName) || params == null || chaincodeID == null) {
                throw new IllegalArgumentException("funcName | params | chaincodeID must not be empty.");
            }
            return new QueryCCDTO(this);
        }
    }

}