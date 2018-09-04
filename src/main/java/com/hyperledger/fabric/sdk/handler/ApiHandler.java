package com.hyperledger.fabric.sdk.handler;

import static com.hyperledger.fabric.sdk.logger.Logger.*;
import static com.hyperledger.fabric.sdk.logger.Logger.debug;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getFile;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getPrivateKeyFromBytes;
import static com.hyperledger.fabric.sdk.common.Constants.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.*;
import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by L.Answer on 2018-09-03 11:16
 *
 * 操作区块链链码API
 *
 * 单元测试类: APITest
 */
public class ApiHandler {
    /** 超时时间, 单位: S */
    private static final Integer TIME_OUT = 50;
    /** channel redis中的key的前缀 */
    public static final String CHANNEL_CACHE = REDIS_PREFIX + "channel:";

    private static Jedis jedis = new Jedis(REDIS_IP);
    static {
        jedis = (REDIS_PORT <= 0) ? new Jedis(REDIS_IP) : new Jedis(REDIS_IP, REDIS_PORT);

        jedis.select(REDIS_INDEX);

        if (StringUtils.isNotEmpty(REDIS_PASSWD)) {
            jedis.auth(REDIS_PASSWD);
        }
    }



    /**
     * Star - 构建客户端实例
     * @param buildClientDTO {@link BuildClientDTO}
     * @return HFClient
     * @throws Exception e
     * */
    public static HFClient clientBuild(BuildClientDTO buildClientDTO) throws Exception {
        debug("构建Hyperledger Fabric客户端实例 Start...");
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        String mspPath = buildClientDTO.getMspPath();

        File certFile, keyFile;
        if (StringUtils.isNotEmpty(mspPath)) {
            certFile = getFile(mspPath + "signcerts", null);
            keyFile = getFile(mspPath + "keystore", "_sk");
        } else {
            certFile = new File(buildClientDTO.getCertPath());
            keyFile = new File(buildClientDTO.getKeyPath());
        }

        InputStream certFileIS = new FileInputStream(certFile);
        String cert = new String(IOUtils.toByteArray(certFileIS), UTF_8);

        InputStream keyFileIS = new FileInputStream(keyFile);
        PrivateKey key = getPrivateKeyFromBytes(IOUtils.toByteArray(keyFileIS));

        String name = buildClientDTO.getName();
        String mspid = buildClientDTO.getMspId();
        Enrollment enrollmentDTO = new EnrollmentDTO(cert, key);

        User user = new UserContextDTO(name, mspid, enrollmentDTO);
        client.setUserContext(user);
        debug("构建Hyperledger Fabric客户端实例 End!!!");
        return client;
    }


    /**
     * Star - 创建通道
     * @param client 客户端实例
     * @param channelName 通道名称
     * @param createChannelDTO {@link CreateChannelDTO}
     * @return {@link Channel}
     * @throws Exception e
     * */
    public static Channel createChannel(HFClient client, String channelName, CreateChannelDTO createChannelDTO) throws Exception {
        return createChannel(client, channelName, createChannelDTO, true);
    }


    /**
     * Star - 创建通道, 包含了加入的peer节点
     * @param client 客户端实例
     * @param channelName 通道名称
     * @param createChannelDTO {@link CreateChannelDTO}
     * @param useCache 是否优先使用已缓存的通道对象
     * @return {@link Channel}
     * @throws Exception e
     * */
    public static Channel createChannel(HFClient client, String channelName, CreateChannelDTO createChannelDTO, boolean useCache) throws Exception {
        debug("创建通道 Start, channelName: %s.", channelName);

        Channel channel;
        // 优先使用缓存中已缓存的通道对象
        if (useCache) {
            byte[] bytes = jedis.get((CHANNEL_CACHE + channelName).getBytes());
            if (bytes != null) {
                warn("缓存中已存在通道名称为: %s 的通道对象, 使用缓存中的通道对象.", channelName);
                channel = client.deSerializeChannel(bytes);
                if (!channel.isInitialized()) channel.initialize();
                debug("创建通道 End, channelName: %s, isInitialized: %b.", channelName, channel.isInitialized());
                return channel;
            }
        }

        File channelFile = new File(createChannelDTO.getChannelConfigPath());
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(channelFile);
        Orderer orderer = client.newOrderer(createChannelDTO.getOrderNodeDTO().getNodeName(), createChannelDTO.getOrderNodeDTO().getGrpcUrl(), createChannelDTO.getOrderNodeDTO().getProperties());
        channel = client.newChannel(channelName, orderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, client.getUserContext()));
        // 把 orderer 节点加入通道
        channel.addOrderer(orderer);
        debug("order节点: %s 已成功加入通道.", orderer.getName());

        // 把 peer 节点加入通道
        Collection<PeerNodeDTO> peerNodeDTOS = createChannelDTO.getPeerNodeDTOS();
        joinPeers(client, channel, peerNodeDTOS);

        debug("创建通道 End, channelName: %s, isInitialized: %b.", channelName, channel.isInitialized());
        // 如果已连接redis, 则将 channel 对象存储进缓存
        if (jedis.isConnected()) {
            String key = CHANNEL_CACHE + channelName;
            jedis.set(key.getBytes(), channel.serializeChannel());
            info("通道对象已放入redis缓存, key: %s.", key);
        }
        return channel;
    }


    /**
     * Star - 将指定peer节点加入通道
     * @param client 客户端实例
     * @param channel 通道对象
     * @param peerNodeDTOS 节点信息
     * */
    public static void joinPeers(HFClient client, Channel channel, Collection<PeerNodeDTO> peerNodeDTOS) throws Exception {
        for (PeerNodeDTO peerNodeDTO : peerNodeDTOS) {
            Peer peer = client.newPeer(peerNodeDTO.getNodeName(), peerNodeDTO.getGrpcUrl(), peerNodeDTO.getProperties());
            channel.joinPeer(peer);
            debug("peer节点: %s 已成功加入通道.", peerNodeDTO.getNodeName());

            EventHub eventHub = client.newEventHub(peerNodeDTO.getNodeName(), peerNodeDTO.getEvenHubUrl(), peerNodeDTO.getProperties());
            channel.addEventHub(eventHub);
            debug("eventHub节点: %s 已成功加入通道.", peerNodeDTO.getNodeName());
        }
        if (!channel.isInitialized()) {
            channel.initialize();
        }
    }


    /**
     * Star - 安装智能合约
     * @param client 客户端实例
     * @param installCCDTO {@link InstallCCDTO}
     * @throws Exception e
     * */
    public static void installChainCode(HFClient client, InstallCCDTO installCCDTO) throws Exception {
        ChaincodeID chaincodeID = installCCDTO.getChaincodeID();
        debug("安装智能合约 Start, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(installCCDTO.getChaincodeID());
        installProposalRequest.setChaincodeSourceLocation(new File(installCCDTO.getChaincodeSourceLocation()));
        installProposalRequest.setChaincodeVersion(installCCDTO.getChaincodeID().getVersion());

        Collection<Peer> peers = new ArrayList<>();
        Collection<PeerNodeDTO> peerNodeDTOS = installCCDTO.getPeerNodeDTOS();
        /* 给指定节点部署智能合约 */
        for (PeerNodeDTO peerNodeDTO : peerNodeDTOS) {
            Peer peer0 = client.newPeer(peerNodeDTO.getNodeName(), peerNodeDTO.getGrpcUrl(), peerNodeDTO.getProperties());
            peers.add(peer0);
        }

        Collection<ProposalResponse> installProposals = client.sendInstallProposal(installProposalRequest, peers);

        if (checkResult(installProposals, false)) {
            debug("安装智能合约 End, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
        } else {
            debug("安装智能合约失败啦, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
        }

    }


    /**
     * Star - 初始化智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param initCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static void initializeChainCode(HFClient client, Channel channel, ExecuteCCDTO initCCDTO) throws Exception {
        debug("初始化智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), initCCDTO.getFuncName(), Arrays.asList(initCCDTO.getParams()));
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(initCCDTO.getProposalWaitTime());
        instantiateProposalRequest.setChaincodeID(initCCDTO.getChaincodeID());
        instantiateProposalRequest.setFcn(initCCDTO.getFuncName());
        instantiateProposalRequest.setArgs(initCCDTO.getParams());
        instantiateProposalRequest.setTransientMap(transientMap("InstantiateProposalRequest"));

        Collection<ProposalResponse> initProposals = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());

        orderConsensus(channel, initProposals, false);

        debug("初始化智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), initCCDTO.getFuncName(), Arrays.asList(initCCDTO.getParams()));
    }


    /**
     * Star - 查询智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param queryCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static void queryChainCode(HFClient client, Channel channel, ExecuteCCDTO queryCCDTO) throws Exception {
        debug("查询智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(queryCCDTO.getParams());
        queryByChaincodeRequest.setFcn(queryCCDTO.getFuncName());
        queryByChaincodeRequest.setChaincodeID(queryCCDTO.getChaincodeID());
        queryByChaincodeRequest.setProposalWaitTime(queryCCDTO.getProposalWaitTime());
        queryByChaincodeRequest.setTransientMap(transientMap("QueryByChaincodeRequest"));

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());

        checkResult(queryProposals);

        debug("查询智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
    }


    /**
     * Star - 交易智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param invokeCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static void invokeChainCode(HFClient client, Channel channel, ExecuteCCDTO invokeCCDTO) throws Exception {
        debug("交易智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(invokeCCDTO.getChaincodeID());
        transactionProposalRequest.setFcn(invokeCCDTO.getFuncName());
        transactionProposalRequest.setProposalWaitTime(invokeCCDTO.getProposalWaitTime());
        transactionProposalRequest.setArgs(invokeCCDTO.getParams());
        transactionProposalRequest.setTransientMap(transientMap("TransactionProposalRequest"));

        Collection<ProposalResponse> invokeProposals = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

        orderConsensus(channel, invokeProposals);

        debug("交易智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));
    }







    private static void orderConsensus(Channel channel, Collection<ProposalResponse> proposalResponses) throws Exception {
        orderConsensus(channel, proposalResponses, true);
    }
    /**
     * 将响应结果提交到orderer节点进行共识
     * @param channel 通道对象
     * @param proposalResponses 提议响应集
     * @param usePayload 是否需要显示账户余额
     * @throws Exception e
     * */
    private static void orderConsensus(Channel channel, Collection<ProposalResponse> proposalResponses, boolean usePayload) throws Exception {
        if (checkResult(proposalResponses, usePayload)) {
            debug("提交到orderer节点进行共识 Start...");
            // 将背书结果提交到 orderer 节点进行排序打块
            BlockEvent.TransactionEvent transactionEvent = channel.sendTransaction(proposalResponses).get(TIME_OUT, TimeUnit.SECONDS);
            debug("提交到orderer共识 End, Type: %s, TransactionActionInfoCount: %d, isValid: %b, ValidationCode: %d.",
                    transactionEvent.getType().name(), transactionEvent.getTransactionActionInfoCount(), transactionEvent.isValid(), transactionEvent.getValidationCode());
        } else {
            error("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
            throw new FabricSDKException("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
        }
    }


    private static boolean checkResult(Collection<ProposalResponse> proposalResponses) {
        return checkResult(proposalResponses, true);
    }

    /**
     * 校验链码响应结果
     * @param proposalResponses 提议响应集
     * @param usePayload 是否需要显示账户余额
     * @return flag boolean
     * */
    private static boolean checkResult(Collection<ProposalResponse> proposalResponses, boolean usePayload) {
        boolean flag = true;
        for (ProposalResponse proposalResponse: proposalResponses) {
            String payload;
            if (usePayload) payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
            else payload = "【nil】";
            payload = StringUtils.isEmpty(payload) ? "nil" : payload;
            info("response status: %s, isVerified: %b from peer: %s, payload: %s.", proposalResponse.getStatus(), proposalResponse.isVerified(), proposalResponse.getPeer().getName(), payload);

            if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                info("failed message: %s, isVerified: %b from peer: %s.", proposalResponse.getMessage(), proposalResponse.isVerified(), proposalResponse.getPeer().getName());
                flag = false;
            }
        }
        return flag;
    }

    private static Map<String, byte[]> transientMap(String typeName) {
        return new HashMap<String, byte[]>() {
            {
                put("HyperLedgerFabric", String.format("%s:JavaSDK", typeName).getBytes(UTF_8));
                put("method", String.format("%s", typeName).getBytes(UTF_8));
            }
        };
    }


}