package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder(builderClassName="Builder", toBuilder=true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Account {
    private String passphrase;
    private String address;
    private long balance;
}
