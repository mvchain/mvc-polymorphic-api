package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder(builderClassName="Builder", toBuilder=true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)public class Block {
    private String nonce;
}
