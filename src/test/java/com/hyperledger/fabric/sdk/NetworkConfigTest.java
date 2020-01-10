package com.hyperledger.fabric.sdk;


import com.alibaba.fastjson.JSON;
import com.hyperledger.fabric.sdk.common.Constants;
import com.hyperledger.fabric.sdk.entity.OptTypeEnum;
import com.hyperledger.fabric.sdk.entity.RequestEnum;
import com.hyperledger.fabric.sdk.entity.TxInfo;
import com.hyperledger.fabric.sdk.entity.dto.api.BuildClientDTO;
import com.hyperledger.fabric.sdk.handler.ApiHandler;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.helper.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.hyperledger.fabric.sdk.entity.RequestEnum.QUERYBY_CHAINCODE_REQUEST;
import static com.hyperledger.fabric.sdk.utils.HFSDKUtils.hexStringToBytes;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

/**
 * Created by L.Answer on 2018-12-28 14:17
 */
public class NetworkConfigTest {

    public static void main(String[] args) throws Exception {
        String channelName = "mychannel";
        // 使用admin 用户证书登录
        String certPath = "src/test/resources/signcerts/Admin@org1.aiqkl.com-cert.pem";
        String keyPath = "src/test/resources/keystore/adminkeystore_sk";
        // 使用普通用户证书登录
        certPath = "src/test/resources/msp/signcerts/User1@org1.aiqkl.com-cert.pem";
        keyPath = "src/test/resources/msp/keystore/2e775feb91c4e65cd0b5c9c072b740fbde5e89a8d4ce4c64e9cf9faa2f587e6c_sk";
        String yamlFilePath = "src/test/resources/network-config.yaml";

        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.aiqkl.com")
                .mspId("Org1MSP")
                .certPath(certPath)
                .keyPath(keyPath).build();

        HFClient hfClient = ApiHandler.clientBuild(buildClientDTO);


        NetworkConfig networkConfig = NetworkConfig.fromYamlFile(new File(yamlFilePath));
        Channel channel = hfClient.loadChannelFromConfig(channelName, networkConfig);
        channel.initialize();


        Collection<Peer> peers = channel.getPeers();
        for (Peer peer : peers) {
            System.out.println(peer.getName());
        }

        System.out.println("\n\n\n");
        System.out.println("查询块信息");

        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();
        long height = blockchainInfo.getHeight();

        for (int num = 1; num < height; num++) {
            BlockInfo blockInfo = channel.queryBlockByNumber(num);

            List<TxInfo> txInfos = new ArrayList<>();
            analysisEnvlope(blockInfo, txInfos);

            System.out.println("=======>" + JSON.toJSONString(txInfos));
            System.out.println();
        }

        System.out.println("\n\n\n");
        System.out.println("查询交易");
        query(hfClient, channel);
        channel.shutdown(false);

        TimeUnit.SECONDS.sleep(3);

        channel = hfClient.loadChannelFromConfig(channelName, networkConfig);
        channel.initialize();
        System.out.println("\n\n\n");
        System.out.println("查询交易");
        query(hfClient, channel);
    }


    private static void query(HFClient hfClient, Channel mychannel) throws Exception {
        ChaincodeID chaincodeId = ChaincodeID.newBuilder().setName("mycc").setVersion("1.0").setPath("github.com/chaincode_example02").build();
        QueryByChaincodeRequest queryByChaincodeRequest = hfClient.newQueryProposalRequest();
        // 设置查询的 key
        queryByChaincodeRequest.setArgs("answer");
        queryByChaincodeRequest.setFcn(OptTypeEnum.QUERY.getName());
        queryByChaincodeRequest.setChaincodeID(chaincodeId);
        queryByChaincodeRequest.setProposalWaitTime(30000);
        queryByChaincodeRequest.setTransientMap(transientMap(QUERYBY_CHAINCODE_REQUEST));

        Collection<ProposalResponse> queryProposals = mychannel.queryByChaincode(queryByChaincodeRequest);
        String result = queryProposals.iterator().next().getProposalResponse().getResponse().getPayload().toStringUtf8();
        System.out.println(format("查询结果=%s", result));
    }

    private static Map<String, byte[]> transientMap(RequestEnum requestEnum) {
        return new HashMap<String, byte[]>() {
            private static final long serialVersionUID = 8296621201221876405L;
            {
                put("HyperLedgerFabric", format("%s:JavaSDK", requestEnum.getName()).getBytes(UTF_8));
                put("method", format("%s", requestEnum.getName()).getBytes(UTF_8));
            }
        };
    }

    private static void analysisEnvlope(BlockInfo blockInfo, List<TxInfo> txInfos) {
        try {
            TxInfo txInfo;
            for (BlockInfo.EnvelopeInfo envelopeInfo: blockInfo.getEnvelopeInfos()) {
                if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
                    txInfo = new TxInfo();
                    txInfo.setTxID(envelopeInfo.getTransactionID());
                    txInfo.setBlockNum(blockInfo.getBlockNumber());
                    // Fabric 1.2支持
                    txInfo.setMspID(envelopeInfo.getCreator().getMspid());
                    txInfo.setCreateTime(Constants.SDF.format(envelopeInfo.getTimestamp()));

                    BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                    for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo: transactionEnvelopeInfo.getTransactionActionInfos()) {
                        txInfo.setBusinessType(new String(transactionActionInfo.getChaincodeInputArgs(0), UTF_8));
                        txInfo.setParamsLen(transactionActionInfo.getChaincodeInputArgsCount());
                        txInfo.setEpLen(transactionActionInfo.getEndorsementsCount());

                        for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : transactionActionInfo.getTxReadWriteSet().getNsRwsetInfos()) {
                            String namespace = nsRwsetInfo.getNamespace();
                            txInfo.setNamespace(namespace);
                            List<TxInfo.Pair> pairs = new ArrayList<>();

                            if ("delete".equals(txInfo.getBusinessType())) {
                                for (KvRwset.KVRead kvRead: nsRwsetInfo.getRwset().getReadsList()) {
                                    TxInfo.Pair pair = new TxInfo.Pair();
                                    pair.setKey(kvRead.getKey());
                                    pairs.add(pair);
                                }
                            } else {
                                for (KvRwset.KVWrite kvWrite: nsRwsetInfo.getRwset().getWritesList()) {
                                    // 排除掉系统链码的操作信息
                                    if (!"lscc".equals(namespace)) {
                                        byte[] bytes = hexStringToBytes(Utils.toHexString(kvWrite.getValue()));
                                        if (bytes != null) {
                                            TxInfo.Pair pair = new TxInfo.Pair();
                                            pair.setKey(kvWrite.getKey());
                                            pair.setValue(new String(bytes, UTF_8));
                                            pairs.add(pair);
                                        }
                                    }
                                }
                            }

                            txInfo.setPairs(pairs);
                        }
                    }
                    txInfos.add(txInfo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}