package com.hyperledger.fabric.sdk.test;

import static com.hyperledger.fabric.sdk.utils.FileUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Security;
import java.util.*;
import java.util.concurrent.*;


/**
 * Created by answer on 2018-08-28 09:27
 */
public class SDKClient {
    static String cxtPath = SDKClient.class.getClassLoader().getResource("").getPath();
    static {
        Security.addProvider(new BouncyCastleProvider());
        System.setProperty(Config.ORG_HYPERLEDGER_FABRIC_SDK_CONFIGURATION, cxtPath + "config.properties");
    }

    public static void main(String[] args) {

        try {
            HFClient client = clientBuild();

            Channel channel = createChannel(client);

            installChainCode(client);

            instantiateChainCode(client, channel);
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    /**
     * 初始化智能合约
     * */
    private static void instantiateChainCode(HFClient client, Channel channel) throws Exception {
        System.out.println("初始化智能合约 Start...");
        String chaincodeName = "mycc";
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        Map<String, byte[]> map = new HashMap<String, byte[]>() {
            {
                put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
                put("method", "InstantiateProposalRequest".getBytes(UTF_8));
            }
        };

        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        // 设置请求超时时间
        instantiateProposalRequest.setProposalWaitTime(30000);
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setFcn("init");
        instantiateProposalRequest.setArgs(new String[] {"a", "500", "b", "" + 200});
        instantiateProposalRequest.setTransientMap(map);
        /**ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File(cxtPath + "chaincodeendorsementpolicy.yaml"));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);*/

        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());

        Collection<ProposalResponse> failed = new ArrayList<>();

        for (ProposalResponse response: responses) {
            System.out.println("response status: " + response.getStatus() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());

            if (response.getStatus() == ProposalResponse.Status.FAILURE) {
                System.out.println("failed message: " + response.getMessage() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());
                failed.add(response);
            }
        }
        System.out.println("初始化账户a余额: 500, 账户b余额: 200.");
        System.out.println("初始化智能合约 End...");

        if (failed.size() > 0) {
            throw new FabricSDKException("instantiateChainCode failed.");
        }

        System.out.println("提交操作数据到orderer节点共识 Start...");
        channel.sendTransaction(responses).thenApply(transactionEvent -> {

            try {
                System.out.println("准备进行转账提议: A->B转150...");
                TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
                transactionProposalRequest.setChaincodeID(chaincodeID);
                transactionProposalRequest.setFcn("invoke");
                transactionProposalRequest.setProposalWaitTime(30000);
                transactionProposalRequest.setArgs(new String[] {"a", "b", "150"});
                /**Map<String, byte[]> tm2 = new HashMap<>();
                tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
                tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
                tm2.put("result", ":)".getBytes(UTF_8));
                transactionProposalRequest.setTransientMap(tm2);*/
                Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
                for (ProposalResponse proposalResponse: transactionPropResp) {
                    System.out.println("response status: " + proposalResponse.getStatus() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());

                    if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                        System.out.println("failed message: " + proposalResponse.getMessage() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());
                    }
                }
                // TODO： 交易交易成功即没有failed响应

                System.out.println("转账提议: A->B转150结束!!!");
                // 提交到orderer共识
                return channel.sendTransaction(transactionPropResp).get(50, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  null;
        }).thenApply(transactionEvent -> {
            String transactionID = transactionEvent.getTransactionID();
            System.out.println("共识结束, 交易响应的TXID: " + transactionID);

            // 进行查询提议
            try {
                System.out.println("准备发起查询账户B的交易提议...");
                QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
                queryByChaincodeRequest.setArgs(new String[] {"b"});
                queryByChaincodeRequest.setFcn("query");
                queryByChaincodeRequest.setChaincodeID(chaincodeID);

                /**Map<String, byte[]> param = new HashMap<String, byte[]>() {
                    {
                        put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
                        put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
                    }
                };
                queryByChaincodeRequest.setTransientMap(param);*/
                Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());

                for (ProposalResponse proposalResponse: queryProposals) {
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    System.out.println("response status: " + proposalResponse.getStatus() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName() + ", payload: " + payload);

                    if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                        System.out.println("failed message: " + proposalResponse.getMessage() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());
                    }
                }
                System.out.println("发起查询账户B的交易提议完成!!!");
            } catch (Exception e) {
                System.out.println("进行查询提议报错," + e.getMessage());
                e.printStackTrace();
            }
            return null;

        }).exceptionally(e -> {
            e.printStackTrace();

            return null;
        }).get(50, TimeUnit.SECONDS);

    }


    /**
     * 安装智能合约
     * */
    private static void installChainCode(HFClient client/*, Channel channel*/) throws Exception {
        System.out.println("安装智能合约 Start...");
        /** 给哪些节点部署智能合约 */
        Collection<Peer> peers = new ArrayList<>();
        Properties peerProperties0 = new Properties();
//        File peerFile0 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
//        peerProperties0.setProperty("pemFile", peerFile0.getAbsolutePath());
        peerProperties0.setProperty("hostnameOverride", "peer0.org1.example.com");
        peerProperties0.setProperty("trustServerCertificate", "true");
        peerProperties0.setProperty("sslProvider", "openSSL");
        peerProperties0.setProperty("negotiationType", "TLS");
        peerProperties0.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
        Peer peer0 = client.newPeer("peer0.org1.example.com", "grpc://119.23.106.146:7051", peerProperties0);
        peers.add(peer0);

        Properties peerProperties1 = new Properties();
//        File peerFile1 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt");
//        peerProperties1.setProperty("pemFile", peerFile1.getAbsolutePath());
        peerProperties1.setProperty("hostnameOverride", "peer1.org1.example.com");
        peerProperties1.setProperty("trustServerCertificate", "true");
        peerProperties1.setProperty("sslProvider", "openSSL");
        peerProperties1.setProperty("negotiationType", "TLS");
        peerProperties1.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
        Peer peer1 = client.newPeer("peer1.org1.example.com", "grpc://119.23.106.146:8051", peerProperties1);
        peers.add(peer1);

        String chaincodeName = "mycc";
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/chaincode_example02";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(new File(cxtPath + "chaincodes/sample"));
        installProposalRequest.setChaincodeVersion(chaincodeVersion);

        Collection<ProposalResponse> responses = client.sendInstallProposal(installProposalRequest, peers);

        for (ProposalResponse response: responses) {
            System.out.println("response status: " + response.getStatus() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());

            if (response.getStatus() == ProposalResponse.Status.FAILURE) {
                System.out.println("failed message: " + response.getMessage() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());
            }
        }
        System.out.println("安装智能合约 End...");
    }


    /**
     * 创建客户端实例
     * */
    private static HFClient clientBuild() throws Exception {
        System.out.println("创建客户端实例 Start...");
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        InputStream certFileIS = new FileInputStream(getFile(mspPath + "signcerts", null));
        String cert = new String(IOUtils.toByteArray(certFileIS), StandardCharsets.UTF_8);

        InputStream keyFileIS = new FileInputStream(getFile(mspPath + "keystore", "_sk"));
        PrivateKey key = getPrivateKeyFromBytes(IOUtils.toByteArray(keyFileIS));

        String orgName = "org1.example.com";
        String mspid = "Org1MSP";
        Enrollment enrollmentDTO = new EnrollmentDTO(cert, key);

        User user = new UserContextDTO(orgName, mspid, enrollmentDTO);
        client.setUserContext(user);

        System.out.println("创建客户端实例 End...");
        return client;
    }



    /**
     * 创建通道
     *  channel.tx
     *  userContext
     *  orderer node
     * */
    private static Channel createChannel(HFClient client) throws Exception {
        System.out.println("创建通道 Start...");
        /**
         * ############################################################################################################################################################
         *                                          创建通道
         * ############################################################################################################################################################
         * */
        File channelFile = new File(cxtPath + "channel-artifacts/channel.tx");
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(channelFile);

        String channelName = "mychannel";
        // orderer 节点的配置文件信息
        Properties orderProperties = new Properties();
//        File ordererFile = new File(cxtPath + "crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt");
//        orderProperties.setProperty("pemFile", ordererFile.getAbsolutePath());
        orderProperties.setProperty("hostnameOverride", "orderer.example.com");
        orderProperties.setProperty("trustServerCertificate", "true");
        orderProperties.setProperty("sslProvider", "openSSL");
        orderProperties.setProperty("negotiationType", "TLS");
        orderProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        orderProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
        Orderer orderer = client.newOrderer("orderer.example.com", "grpc://119.23.106.146:7050", orderProperties);

        Channel channel = client.newChannel(channelName, orderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, client.getUserContext()));
        System.out.println("通道创建成功");

        // 加入 peer 节点
        Properties peerProperties0 = new Properties();
//        File peerFile0 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
//        peerProperties0.setProperty("pemFile", peerFile0.getAbsolutePath());
        peerProperties0.setProperty("hostnameOverride", "peer0.org1.example.com");
        peerProperties0.setProperty("trustServerCertificate", "true");
        peerProperties0.setProperty("sslProvider", "openSSL");
        peerProperties0.setProperty("negotiationType", "TLS");
        peerProperties0.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

        Peer peer0 = client.newPeer("peer0.org1.example.com", "grpc://119.23.106.146:7051", peerProperties0);
        channel.joinPeer(peer0);
        System.out.println("peer0 join channel");

        Properties peerProperties1 = new Properties();
//        File peerFile1 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt");
//        peerProperties1.setProperty("pemFile", peerFile1.getAbsolutePath());
        peerProperties1.setProperty("hostnameOverride", "peer1.org1.example.com");
        peerProperties1.setProperty("trustServerCertificate", "true");
        peerProperties1.setProperty("sslProvider", "openSSL");
        peerProperties1.setProperty("negotiationType", "TLS");
        peerProperties1.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

        Peer peer1 = client.newPeer("peer1.org1.example.com", "grpc://119.23.106.146:8051", peerProperties1);
        channel.joinPeer(peer1);
        System.out.println("peer1 join channel");


        // 把 order 节点加入通道
        channel.addOrderer(orderer);


        // 加入 peer - eventHub 节点
        Properties peerHubProperties0 = new Properties();
//        File peerHubFile0 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
//        peerHubProperties0.setProperty("pemFile", peerHubFile0.getAbsolutePath());
        peerHubProperties0.setProperty("hostnameOverride", "peer0.org1.example.com");
        peerHubProperties0.setProperty("trustServerCertificate", "true");
        peerHubProperties0.setProperty("sslProvider", "openSSL");
        peerHubProperties0.setProperty("negotiationType", "TLS");
        peerHubProperties0.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        peerHubProperties0.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});

        EventHub peerHub0 = client.newEventHub("peer0.org1.example.com", "grpc://119.23.106.146:7053", peerHubProperties0);
        channel.addEventHub(peerHub0);
        System.out.println("peer0 eventHub join channel");

        Properties peerHubProperties1 = new Properties();
//        File peerHubFile1 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt");
//        peerHubProperties1.setProperty("pemFile", peerHubFile1.getAbsolutePath());
        peerHubProperties1.setProperty("hostnameOverride", "peer1.org1.example.com");
        peerHubProperties1.setProperty("trustServerCertificate", "true");
        peerHubProperties1.setProperty("sslProvider", "openSSL");
        peerHubProperties1.setProperty("negotiationType", "TLS");
        peerHubProperties1.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        peerHubProperties1.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});

        EventHub peerHub1 = client.newEventHub("peer1.org1.example.com", "grpc://119.23.106.146:8053", peerHubProperties1);
        channel.addEventHub(peerHub1);
        System.out.println("peer1 eventHub join channel");


        channel.initialize();
        System.out.println("通道初始化完成");
        /** ############################################################################################################################################################ */

        System.out.println("创建通道 End...");
        return channel;
    }



    private static PrivateKey getPrivateKeyFromBytes(byte[] data) throws Exception {
        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        return new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
    }

}