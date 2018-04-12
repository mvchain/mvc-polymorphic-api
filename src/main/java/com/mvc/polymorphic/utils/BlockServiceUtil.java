package com.mvc.polymorphic.utils;

import com.mvc.tools.context.BaseContextHandler;

/**
 * block service util
 *
 * @author qiyichen
 * @create 2018/4/9 17:24
 */
public class BlockServiceUtil {

    public enum BlockService {
        ETH("ETH", "EthService"),
        BTC("BTC", "BtcService"),
        BCH("BCH", "BchService"),
        ;
        private String type;
        private String serviceName;

        private BlockService(String type, String serviceName) {
            this.type = type;
            this.serviceName = serviceName;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getType() {
            return type;
        }

        public static String fromType(String type) {
            return valueOf(type).getServiceName();
        }
    }

    public static String getServiceName(String type) {
        BaseContextHandler.set("type", type);
        return BlockService.fromType(type.toUpperCase());
    }

}
