package com.hyperledger.fabric.sdk.utils;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.ByteArrayOutputStream;

/**
 * Created by L.Answer on 2018-09-04 14:49
 */
public class HFSDKUtils {

    public static byte[] calculateBlockHash(long blockNumber, byte[] previousHash, byte[] dataHash) throws Exception {
        if (previousHash == null) {
            throw new InvalidArgumentException("previousHash parameter is null.");
        }
        if (dataHash == null) {
            throw new InvalidArgumentException("dataHash parameter is null.");
        }
        CryptoSuite suite = CryptoSuite.Factory.getCryptoSuite();

        ByteArrayOutputStream s = new ByteArrayOutputStream();
        DERSequenceGenerator seq = new DERSequenceGenerator(s);
        seq.addObject(new ASN1Integer(blockNumber));
        seq.addObject(new DEROctetString(previousHash));
        seq.addObject(new DEROctetString(dataHash));
        seq.close();
        return suite.hash(s.toByteArray());
    }
}