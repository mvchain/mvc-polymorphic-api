package io.yope.ethereum.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

@Builder(builderClassName="Builder", toBuilder=true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Method {
    @NotNull
    private Object[] args;
    private String name;
    public Object[] getArgs() {
        if (args != null) {
            return args;
        }
        return new Object[0];
    }
}
