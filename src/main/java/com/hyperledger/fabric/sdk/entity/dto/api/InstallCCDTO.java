package com.hyperledger.fabric.sdk.entity.dto.api;

import com.hyperledger.fabric.sdk.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.ChaincodeID;

import java.util.Collection;

/**
 * Created by L.Answer on 2018-09-04 10:09
 */
public class InstallCCDTO {

    /**
     * 智能合约代码位置
     * C:\Users\answer\IdeaProjects\answer-fabric-sdk\src\main\resources\chaincodes\sample\src\github.com\chaincode_example02\chaincode_example02.go
     * chaincodeSourceLocation = "C:\Users\answer\IdeaProjects\answer-fabric-sdk\src\main\resources\chaincodes\sample"
     * */
    private String chaincodeSourceLocation;
    // 智能合约ID
    private ChaincodeID chaincodeID;

    private Collection<PeerNodeDTO> peerNodeDTOS;
    // 单位: ms
    private Integer proposalWaitTime;

    private InstallCCDTO(Builder builder) {
        this.chaincodeSourceLocation = builder.chaincodeSourceLocation;
        this.chaincodeID = builder.chaincodeID;
        this.peerNodeDTOS = builder.peerNodeDTOS;
        this.proposalWaitTime = builder.proposalWaitTime;
    }

    public String getChaincodeSourceLocation() {
        return chaincodeSourceLocation;
    }

    public ChaincodeID getChaincodeID() {
        return chaincodeID;
    }

    public Integer getProposalWaitTime() {
        if (proposalWaitTime == null || proposalWaitTime <= 0) {
            proposalWaitTime = Constants.PROPOSAL_WAIT_TIME;
        }
        return proposalWaitTime;
    }

    public Collection<PeerNodeDTO> getPeerNodeDTOS() {
        return peerNodeDTOS;
    }

    public static class Builder {
        private String chaincodeSourceLocation;
        private ChaincodeID chaincodeID;
        private Integer proposalWaitTime;
        private Collection<PeerNodeDTO> peerNodeDTOS;

        public Builder chaincodeSourceLocation(String chaincodeSourceLocation) {
            this.chaincodeSourceLocation = chaincodeSourceLocation;
            return this;
        }

        public Builder chaincodeID(ChaincodeID chaincodeID) {
            this.chaincodeID = chaincodeID;
            return this;
        }

        public Builder proposalWaitTime(Integer proposalWaitTime) {
            this.proposalWaitTime = proposalWaitTime;
            return this;
        }

        public Builder peerNodeDTOS(Collection<PeerNodeDTO> peerNodeDTOS) {
            this.peerNodeDTOS = peerNodeDTOS;
            return this;
        }

        public InstallCCDTO build() {
            if (StringUtils.isEmpty(chaincodeSourceLocation) || chaincodeID == null || peerNodeDTOS == null || peerNodeDTOS.size() == 0) {
                throw new IllegalArgumentException("chaincodeSourceLocation | chaincodeID | peerNodeDTOS must not be empty.");
            }
            return new InstallCCDTO(this);
        }

    }
}