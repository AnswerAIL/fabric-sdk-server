package com.hyperledger.fabric.sdk.entity.yaml.tag;

import java.io.Serializable;

/**
 * Created by answer on 2018-08-29 12:30
 */
public class PeerNode implements Serializable {

    private String peerName;
    /**
     * Field grpcUrl 请求地址
     * */
    private String grpcUrl;
    /**
     * Field evenHubUrl 事件地址
     * */
    private String evenHubUrl;
    private String pemFile;

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getGrpcUrl() {
        return grpcUrl;
    }

    public void setGrpcUrl(String grpcUrl) {
        this.grpcUrl = grpcUrl;
    }

    public String getEvenHubUrl() {
        return evenHubUrl;
    }

    public void setEvenHubUrl(String evenHubUrl) {
        this.evenHubUrl = evenHubUrl;
    }

    public String getPemFile() {
        return pemFile;
    }

    public void setPemFile(String pemFile) {
        this.pemFile = pemFile;
    }
}