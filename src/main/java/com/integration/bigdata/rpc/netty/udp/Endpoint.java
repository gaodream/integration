package com.integration.bigdata.rpc.netty.udp;

import java.net.InetSocketAddress;

public interface Endpoint  {
    /**
     * 发送数据，默认不需要监控超时重发
     * @param data 二进制数组
     * @param destSockAddr 目标地址
     * @throws Exception
     */
    public void sendData(byte[] data, InetSocketAddress destSockAddr) throws Exception;

    /**
     * 发送数据，允许设置超时重发，超时重发需要使用消息中的id来识别应答消息
     * @param data 二进制数组
     * @param destSockAddr 目标地址
     * @param needMonitor 是否需要监控的标志，设置了监控，则会进行超时重发
     * @throws Exception
     */
    public void sendData(byte[] data, InetSocketAddress destSockAddr, boolean needMonitor) throws Exception;
}
