package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yope.ethereum.utils.EthereumUtil;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Builder(builderClassName="Builder", toBuilder=true)
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(of= {"contractAddress", "accountAddr"}, includeFieldNames = false)
public class Receipt implements Serializable {
    @Getter
    private String transactionHash;
    @Getter
    private String contractAddress;
    @Getter
    private String blockHash;
    private String transactionIndex;
    private String blockNumber;
    private String cumulativeGasUsed;
    private String gasUsed;
    private Type type;
    @Getter
    @Setter
    private String accountAddr;

    public enum Type {CREATE, MODIFY};

    public long getTransactionIndex() {
        return decrypt(transactionIndex);
    }

    public long getBlockNumber() {
        return decrypt(blockNumber);
    }

    public long getCumulativeGasUsed() {
        return decrypt(cumulativeGasUsed);
    }

    public long getGasUsed() {
        return decrypt(gasUsed);
    }

    private static long decrypt(final String data) {
        if (StringUtils.isNotBlank(data)) {
            return EthereumUtil.decryptQuantity(data);
        }
        return 0;

    }


}
