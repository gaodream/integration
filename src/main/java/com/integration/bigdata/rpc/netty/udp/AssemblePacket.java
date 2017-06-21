package com.integration.bigdata.rpc.netty.udp;

public class AssemblePacket {
    private int count = 0;
    private byte[] data;
    // 数据包索引的标记位数组
    private int[] indexArray;

    public AssemblePacket(int length, int count) {
        this.data = new byte[length];
        this.indexArray = new int[count];
    }

    public void copyData(byte[] data, int begin, int length, int index, int offset) throws Exception {
        if (length > this.data.length - offset) {
            String errMsg = "copy to data error, src array length:" + data.length + "src begin:" +begin + ", dist array length:" + this.data.length + ", offset:" + offset + "copy length:" + length;
            throw new Exception(errMsg);
        }
        System.arraycopy(data, begin, this.data, offset, length);
        if (indexArray[index-1] == 0) {
            // 每次数据包组装后将索引表对应位置置位1，并将count自增，用count表示有多少数据包已经组装好
            indexArray[index-1] = 1;
            count++;
        }
    }

    public int getCount() {
        return count;
    }

    public byte[] getData() {
        return data;
    }
}
