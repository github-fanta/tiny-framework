package model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 封装请求信息
 * Created by liq on 2018/5/8.
 */
public class Request {
    /**
     * 请求路径
     */
    private String requestPath;

    public Request(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
