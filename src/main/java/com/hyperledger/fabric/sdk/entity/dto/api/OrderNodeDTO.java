package com.hyperledger.fabric.sdk.entity.dto.api;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by L.Answer on 2018-09-03 19:04
 */
public class OrderNodeDTO extends NodeDTO implements Serializable {

    public OrderNodeDTO(String nodeName, String grpcUrl) {
        super(nodeName, grpcUrl);
    }

    public OrderNodeDTO(String nodeName, String grpcUrl, Properties properties) {
        super(nodeName, grpcUrl, properties);
    }
}