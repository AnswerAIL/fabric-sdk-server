package com.hyperledger.fabric.sdk.entity.dto;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by answer on 2018-08-28 15:18
 */
public class UserContextDTO implements User, Serializable {

    private String name;
    private String mspid;
    private Enrollment enrollment;


    public UserContextDTO(String name, String mspid, Enrollment enrollment) {
        this.name = name;
        this.mspid = mspid;
        this.enrollment = enrollment;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override
    public String getAccount() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return null;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    @Override
    public String getMspId() {
        return mspid;
    }
}