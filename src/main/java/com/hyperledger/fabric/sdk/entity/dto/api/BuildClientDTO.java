package com.hyperledger.fabric.sdk.entity.dto.api;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by answer on 2018-09-03 14:31
 *
 * 构建客户端需要传入操作员区块的用户信息
 */
public class BuildClientDTO {

    private String cert;
    private String key;
    private String name;
    private String mspId;

    private BuildClientDTO(Builder builder) {
        this.cert = builder.cert;
        this.key = builder.key;
        this.name = builder.name;
        this.mspId = builder.mspId;
    }

    public String getCert() {
        return cert;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getMspId() {
        return mspId;
    }


    /**
     * path: crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/
     * cert: path + signcerts
     * key: path + keystore
     * */
    public static class Builder {
        private String cert;
        private String key;
        private String name;
        private String mspId;

        public Builder cert(String cert) {
            this.cert = cert;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder mspId(String mspId) {
            this.mspId = mspId;
            return this;
        }

        public BuildClientDTO build() {
            if (StringUtils.isEmpty(cert) || StringUtils.isEmpty(key) ||
                    StringUtils.isEmpty(name)|| StringUtils.isEmpty(mspId)) {
                throw new IllegalArgumentException("cert | key | name | mspId must not be empty.");
            }
            return new BuildClientDTO(this);
        }
    }

}