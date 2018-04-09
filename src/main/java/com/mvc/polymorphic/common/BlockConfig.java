package com.mvc.polymorphic.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * @author qiyichen
 * @create 2018/4/9 15:33
 */
@ConfigurationProperties(
        prefix = "mvc.block"
)@Component
@Data
public class BlockConfig {

    public String ethService = "http://localhost:8545";
    public Integer timeoutSec = 120;
    public BigInteger ethPrice = BigInteger.valueOf(45000);
    public BigInteger ethLimit = BigInteger.valueOf(45000);

}
