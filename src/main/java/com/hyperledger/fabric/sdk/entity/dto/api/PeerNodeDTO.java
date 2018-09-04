package com.hyperledger.fabric.sdk.entity.dto.api;

/**
 * Created by L.Answer on 2018-09-03 19:03
 */
public class PeerNodeDTO extends NodeDTO {

    private String evenHubUrl;

    public String getEvenHubUrl() {
        return evenHubUrl;
    }

    public void setEvenHubUrl(String evenHubUrl) {
        this.evenHubUrl = evenHubUrl;
    }
}