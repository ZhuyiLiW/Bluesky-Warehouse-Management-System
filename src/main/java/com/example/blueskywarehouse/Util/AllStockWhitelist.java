package com.example.blueskywarehouse.Util;

import java.util.Collections;
import java.util.List;

public class AllStockWhitelist {
    public static final List<String> ALLOWED = Collections.unmodifiableList(
            List.of(
                    "corona test",
                    "mask",
                    "paper bag",
                    "solar panel",
                    "Huawei",
                    "powerway Set",
                    "Liu",
                    "APS",
                    "Anker",
                    "BC",
                    "RJW",
                    "5PAIRS BAG",
                    "solar cabel",
                    "rail",
                    "bluesky",
                    "deepblue"
            )
    );

    private AllStockWhitelist() {
        // 防止实例化
    }

    /**
     * 检查传入的分组是否在白名单中
     */
    public static boolean isAllowed(String group) {
        return ALLOWED.contains(group);
    }
}
