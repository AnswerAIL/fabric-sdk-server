package com.hyperledger.fabric.sdk.entity.yaml.tag;

import java.io.Serializable;

/**
 * Created by answer on 2018-08-29 12:28
 */
public class User implements Serializable {

    private String name;
    private String mspid;
    private String mspPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMspid() {
        return mspid;
    }

    public void setMspid(String mspid) {
        this.mspid = mspid;
    }

    public String getMspPath() {
        return mspPath;
    }

    public void setMspPath(String mspPath) {
        this.mspPath = mspPath;
    }
}