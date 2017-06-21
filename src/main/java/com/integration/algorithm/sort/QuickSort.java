package com.integration.algorithm.sort;

import com.alibaba.fastjson.JSON;

/**
 * 快排
 * @author gaogao
 *
 */
public class QuickSort {
	    public void quick_sort(int[] arrays, int lenght) {
	        if (null == arrays || lenght < 1) {
	            System.out.println("input error!");
	            return;
	        }
	        _quick_sort(arrays, 0, lenght - 1);
	    }

	    public void _quick_sort(int[] arrays, int start, int end) {
	        if(start>=end){
	            return;
	        }
	        
	        int i = start;
	        int j = end;
	        int value = arrays[i];
	        boolean flag = true;
	        while (i != j) {
	            if (flag) {
	            	//从左往右找到第一个比value大的数字
	                if (value > arrays[j]) {
	                    swap(arrays, i, j);
	                    flag=false;
	                } else {
	                    j--;
	                }
	            }else{
	            	 //从右往左找到第一个小于等于value的数字
	                if(value<arrays[i]){
	                    swap(arrays, i, j);
	                    flag=true;
	                }else{
	                    i++;
	                }
	            }
	        }
	        _quick_sort(arrays, start, j-1);
	        _quick_sort(arrays, i+1, end);
	        
	    }


	    private void swap(int[] arrays, int i, int j) {
	        int temp;
	        temp = arrays[i];
	        arrays[i] = arrays[j];
	        arrays[j] = temp;
	    }

	    public static void main(String args[]) {
	    	QuickSort q = new QuickSort();
	        int[] a = { 49, 38, 65,12,45,5 };
	        q.quick_sort(a,6);
	        System.out.println(JSON.toJSONString(a));
	    } 

	}
