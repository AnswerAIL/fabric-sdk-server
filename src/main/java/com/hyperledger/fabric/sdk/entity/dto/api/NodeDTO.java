package com.hyperledger.fabric.sdk.entity.dto.api;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by L.Answer on 2018-09-03 19:01
 */
public class NodeDTO implements Serializable {

    private String nodeName;
    private String grpcUrl;
    private Properties properties;

    public NodeDTO() {}

    public NodeDTO(String nodeName, String grpcUrl) {
        this.nodeName = nodeName;
        this.grpcUrl = grpcUrl;
        this.properties = commonProperties(nodeName);
    }

    public NodeDTO(String nodeName, String grpcUrl, Properties properties) {
        this.nodeName = nodeName;
        this.grpcUrl = grpcUrl;
        this.properties = properties;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getGrpcUrl() {
        return grpcUrl;
    }

    public void setGrpcUrl(String grpcUrl) {
        this.grpcUrl = grpcUrl;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private Properties commonProperties(String nodeName) {
        Properties properties = new Properties();
        properties.setProperty("hostnameOverride", nodeName);
        properties.setProperty("trustServerCertificate", "true");
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
        properties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
        return properties;
    }
}