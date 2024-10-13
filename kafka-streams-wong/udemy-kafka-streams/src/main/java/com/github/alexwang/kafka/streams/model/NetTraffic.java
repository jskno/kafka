package com.github.alexwang.kafka.streams.model;

import java.util.Objects;

public class NetTraffic {
    private String page;
    private String remoteAddress;

    public NetTraffic() {
    }

    public NetTraffic(String page, String remoteAddress) {
        this.page = page;
        this.remoteAddress = remoteAddress;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetTraffic that = (NetTraffic) o;
        return Objects.equals(getPage(), that.getPage()) && Objects.equals(getRemoteAddress(), that.getRemoteAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPage(), getRemoteAddress());
    }

    @Override
    public String toString() {
        return "NetTraffic{" +
                "page='" + page + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                '}';
    }
}
