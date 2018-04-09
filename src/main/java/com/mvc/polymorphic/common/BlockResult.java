package com.mvc.polymorphic.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * block result
 *
 * @author qiyichen
 * @create 2018/4/9 16:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockResult {
    private String type;
    private Boolean success;
    private Object error;
    private Object result;
}
