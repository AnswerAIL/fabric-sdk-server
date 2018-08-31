package com.hyperledger.fabric.sdk.entity.yaml;

import com.hyperledger.fabric.sdk.entity.yaml.tag.Channel;
import com.hyperledger.fabric.sdk.entity.yaml.tag.OrderNode;
import com.hyperledger.fabric.sdk.entity.yaml.tag.PeerNode;
import com.hyperledger.fabric.sdk.entity.yaml.tag.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by answer on 2018-08-29 12:33
 */
public class CreateChannelEO implements Serializable {

    private Channel channel;
    private User user;
    private List<OrderNode> orderNodes;
    private List<PeerNode> peerNodes;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderNode> getOrderNodes() {
        return orderNodes;
    }

    public void setOrderNodes(List<OrderNode> orderNodes) {
        this.orderNodes = orderNodes;
    }

    public List<PeerNode> getPeerNodes() {
        return peerNodes;
    }

    public void setPeerNodes(List<PeerNode> peerNodes) {
        this.peerNodes = peerNodes;
    }
}