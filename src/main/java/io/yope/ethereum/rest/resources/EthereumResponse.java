package io.yope.ethereum.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EthereumResponse<T> {

    private T response;
    private int responseCode;
    private String message;

    public EthereumResponse(T response, int responseCode, String message) {
        this.response = response;
        this.responseCode = responseCode;
        this.message = message;
    }
}
