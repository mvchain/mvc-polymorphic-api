package com.mvc.polymorphic.common.bean;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;

import java.io.Serializable;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * BtcTransaction
 *
 * @author qiyichen
 * @create 2018/3/1 11:43
 */
@Data
public class BtcTransaction implements Serializable {
    private static final long serialVersionUID = -8380806472534356994L;
    private String hash;
    private Date updatedAt;
    private Long value;
    private String valueStr;
    private String feeStr;
    private Long fee;
    private Long version;
    private Integer depth;
    private String fromAddress;
    private String toAddress;

    public static BtcTransaction build(Transaction trans, Wallet wallet) {
        BtcTransaction transaction = new BtcTransaction();
        transaction.setHash(trans.getHashAsString());
        transaction.setFeeStr(null == trans.getFee() ? "0" : trans.getFee().toFriendlyString());
        transaction.setFee(null == trans.getFee() ? 0 : trans.getFee().getValue());
        transaction.setValueStr(trans.getValue(wallet).toFriendlyString());
        transaction.setVersion(trans.getVersion());
        // lamda expression, transform input streams to a list, then to a JSON string.
        String from = JSON.toJSONString(trans.getInputs().stream().map(obj -> obj.getFromAddress().toString()).collect(Collectors.toList()));
        transaction.setFromAddress(from);
        String to = JSON.toJSONString(trans.getOutputs().stream().map(obj -> obj.getAddressFromP2PKHScript(wallet.getParams()).toString()).collect(Collectors.toList()));
        transaction.setToAddress(to);
        // conformation count
        transaction.setDepth(trans.getConfidence().getDepthInBlocks());
        transaction.setValue(trans.getValue(wallet).getValue());
        transaction.setUpdatedAt(trans.getUpdateTime());
        return transaction;
    }

}
