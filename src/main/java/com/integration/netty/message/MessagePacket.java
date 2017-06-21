package com.integration.netty.message;

import java.io.Serializable;

/**
 * �ְ��ṹ
 * @author gaogao
 *
 */
public class MessagePacket implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int count = 0;
	private int[] indexs;// ���ݰ������ı��λ����
	private byte[] data;
	
	
	public MessagePacket(int length, int count) {
        this.data = new byte[length];
        this.indexs = new int[count];
    }
	
    public void copyData(byte[] data, int begin, int length, int index, int offset) throws Exception {
        if (length > this.data.length - offset) {
            String errMsg = "copy to data error, src array length:" + data.length + "src begin:" +begin + ", dist array length:" + this.data.length + ", offset:" + offset + "copy length:" + length;
            throw new Exception(errMsg);
        }
        System.arraycopy(data, begin, this.data, offset, length);
        if (indexs[index-1] == 0) {
            // ÿ�����ݰ���װ���������Ӧλ����λ1������count��������count��ʾ�ж������ݰ��Ѿ���װ��
        	indexs[index-1] = 1;
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
