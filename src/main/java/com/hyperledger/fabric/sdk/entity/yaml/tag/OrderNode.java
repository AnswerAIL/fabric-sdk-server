package com.hyperledger.fabric.sdk.entity.yaml.tag;

import java.io.Serializable;

/**
 * Created by answer on 2018-08-29 12:29
 */
public class OrderNode implements Serializable {

    private String orderName;
    private String grpcUrl;
    private String pemFile;

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getGrpcUrl() {
        return grpcUrl;
    }

    public void setGrpcUrl(String grpcUrl) {
        this.grpcUrl = grpcUrl;
    }

    public String getPemFile() {
        return pemFile;
    }

    public void setPemFile(String pemFile) {
        this.pemFile = pemFile;
    }
}