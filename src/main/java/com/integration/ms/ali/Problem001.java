package com.integration.ms.ali;

/**
 * 
 * 1.输入一个正数n，输出所有和为n的连续正数序列。
 * 分析：设定两个指针，min指向和为n的连续正数序列的最小值，max指向和为n的连续正数序列最大值。sum表示真正的和。
 * 初始情况下,min、max和sum 都指向1.
 * 当sum小于n时，max++；
 * 当sum大于n时，min++;
 * 当sum等于n时，输出整个序列。
 * 
 * @author gaogao
 *
 * 公约数和求和公式
 */
public class Problem001 {
	
	public void getSeq(int n){
		int i = 2;
		while(i<n/2+1){
			if(2*n%i==0){
				int tmp = (2*n/i + 1 -i);
				int start=tmp/2;
				if(tmp%2==0&&start>0){
					String result = "";
					for(int j=0;j<i;j++){
						result = result + (start+j)+" ";
					}
					if(!"".equals(result)){
						System.out.println(result);
					}
				}
			}
			i++;
		}
	}
	
	public static void main(String[] args) {
		new Problem001().getSeq(15);
	}
}
