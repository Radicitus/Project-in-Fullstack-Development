package com.github.klefstad_teaching.cs122b.gateway.model.Container;

import java.time.Instant;

public class GatewayRequestObject {
    private Integer id;
    private String ipAddress;
    private Instant callTime;
    private String path;


    public Integer getId() {
        return id;
    }

    public GatewayRequestObject setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public GatewayRequestObject setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public Instant getCallTime() {
        return callTime;
    }

    public GatewayRequestObject setCallTime(Instant callTime) {
        this.callTime = callTime;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GatewayRequestObject setPath(String path) {
        this.path = path;
        return this;
    }
}
