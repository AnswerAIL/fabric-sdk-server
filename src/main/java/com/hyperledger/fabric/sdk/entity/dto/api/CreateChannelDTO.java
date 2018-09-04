package com.hyperledger.fabric.sdk.entity.dto.api;

/**
 * Created by L.Answer on 2018-09-03 14:26
 */
public class CreateChannelDTO {

    private String channelConfigPath;

    private OrderNodeDTO orderNodeDTO;

    public String getChannelConfigPath() {
        return channelConfigPath;
    }

    public void setChannelConfigPath(String channelConfigPath) {
        this.channelConfigPath = channelConfigPath;
    }

    public OrderNodeDTO getOrderNodeDTO() {
        return orderNodeDTO;
    }

    public void setOrderNodeDTO(OrderNodeDTO orderNodeDTO) {
        this.orderNodeDTO = orderNodeDTO;
    }
}