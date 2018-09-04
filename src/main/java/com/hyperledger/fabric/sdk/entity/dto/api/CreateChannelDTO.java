package com.hyperledger.fabric.sdk.entity.dto.api;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by L.Answer on 2018-09-03 14:26
 */
public class CreateChannelDTO implements Serializable {

    private String channelConfigPath;

    private Collection<OrderNodeDTO> orderNodeDTOS;

    private Collection<PeerNodeDTO> peerNodeDTOS;

    public String getChannelConfigPath() {
        return channelConfigPath;
    }

    public void setChannelConfigPath(String channelConfigPath) {
        this.channelConfigPath = channelConfigPath;
    }

    public Collection<OrderNodeDTO> getOrderNodeDTOS() {
        return orderNodeDTOS;
    }

    public void setOrderNodeDTOS(Collection<OrderNodeDTO> orderNodeDTOS) {
        this.orderNodeDTOS = orderNodeDTOS;
    }

    public Collection<PeerNodeDTO> getPeerNodeDTOS() {
        return peerNodeDTOS;
    }

    public void setPeerNodeDTOS(Collection<PeerNodeDTO> peerNodeDTOS) {
        this.peerNodeDTOS = peerNodeDTOS;
    }
}