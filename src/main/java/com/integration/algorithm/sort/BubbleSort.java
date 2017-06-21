package com.integration.algorithm.sort;

import com.alibaba.fastjson.JSON;

public class BubbleSort {
	public void sort(int[] list){
		boolean swap = true;
		for(int i=1,len = list.length;i<len && swap;i++){
			swap = false;
			for(int j=0;j<len-1;j++){
				if(list[j]>list[j+1]){
					int temp = list[j];
					list[j] = list[j+1];
					list[j+1] = temp;
					swap = true;
				}
			}
		}
	}
	public static void main(String[] args) {
		int[] list = {3,2,7,4,5,9};
		new BubbleSort().sort(list);
		System.out.println(JSON.toJSONString(list));
	}
}
