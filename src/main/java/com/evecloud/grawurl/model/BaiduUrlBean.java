package com.evecloud.grawurl.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author: zhaoyk
 * @date: 2019-5-7 16:35
 * @description:
 */
@Data
public class BaiduUrlBean {
    private String companyName;// 公司名称

    private String domainName;//域名

    private String link;//链接

    public boolean success() {
        if (!Objects.isNull(domainName) && domainName.contains(".")) {
            return true;
        }

        return false;
    }
}
