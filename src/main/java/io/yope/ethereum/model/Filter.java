package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder(builderClassName="Builder", toBuilder=true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Filter {
    private String fromBlock = "0x0";
    private String toBlock = "lastest";
    private String address;
    private String topics;
}
