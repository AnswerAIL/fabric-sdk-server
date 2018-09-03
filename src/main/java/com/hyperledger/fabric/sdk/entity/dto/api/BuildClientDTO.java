package com.hyperledger.fabric.sdk.entity.dto.api;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by L.Answer on 2018-09-03 14:31
 *
 * 构建客户端需要传入操作员区块的用户信息
 *
 * mspPath 和 certPath & keyPath 任选一个
 * 选择 mspPath 必须有 signcerts 和 keystore 两个文件夹
 */
public class BuildClientDTO {

    private String name;
    private String mspId;
    private String mspPath;

    private String certPath;
    private String keyPath;

    private BuildClientDTO(Builder builder) {
        this.mspPath = builder.mspPath;
        this.name = builder.name;
        this.mspId = builder.mspId;
        this.certPath = builder.certPath;
        this.keyPath = builder.keyPath;
    }

    public String getName() {
        return name;
    }

    public String getMspId() {
        return mspId;
    }

    public String getMspPath() {
        return mspPath;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    /**
     * path: crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/
     * cert: path + signcerts
     * key: path + keystore
     * */
    public static class Builder {
        private String name;
        private String mspId;
        private String mspPath;

        private String certPath;
        private String keyPath;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder mspId(String mspId) {
            this.mspId = mspId;
            return this;
        }

        public Builder mspPath(String mspPath) {
            this.mspPath = mspPath;
            return this;
        }

        public Builder certPath(String certPath) {
            this.certPath = certPath;
            return this;
        }

        public Builder keyPath(String keyPath) {
            this.keyPath = keyPath;
            return this;
        }

        public BuildClientDTO build() {
            if ((StringUtils.isEmpty(mspPath) && (StringUtils.isEmpty(certPath) || StringUtils.isEmpty(keyPath))) ||
                    StringUtils.isEmpty(name)|| StringUtils.isEmpty(mspId)) {
                throw new IllegalArgumentException("name | mspId | (mspPath && certPath | keyPath) must not be empty.");
            }
            return new BuildClientDTO(this);
        }
    }

}