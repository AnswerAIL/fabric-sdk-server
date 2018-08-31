package com.hyperledger.fabric.sdk.entity.yaml.tag;

import java.io.Serializable;

/**
 * Created by answer on 2018-08-29 12:27
 */
public class Channel implements Serializable {

    private String channelName;
    private String channelFile;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelFile() {
        return channelFile;
    }

    public void setChannelFile(String channelFile) {
        this.channelFile = channelFile;
    }
}