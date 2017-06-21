package com.integration.algorithm.sort;

public class MergeSort {

	    public int[] A;

	    public MergeSort(int[] array) {
	        this.A = array.clone();
	        sort(0, array.length - 1);
	    }

	    public void sort(int low, int high) {

	        if (low < high) {
	            int mid = (low + high) / 2;
	            sort(low, mid);
	            sort(mid + 1, high);
	            merge(low, mid, high);
	        }
	    }

	    public void merge(int low, int mid, int high) {

	        // 声明新的数组，临时储存归并结果
	        int[] B = new int[high - low + 1];
	        int h = low;
	        int i = 0;
	        int j = mid + 1;

	        while (h <= mid && j <= high) {
	            if (A[h] <= A[j]) {
	                B[i] = A[h];
	                h++;
	            } else {
	                B[i] = A[j];
	                j++;
	            }
	            i++;
	        }

	        // 等号很重要
	        if (h <= mid) {
	            for (int k = h; k <= mid; k++) {
	                B[i] = A[k];
	                i++;
	            }
	        } else {
	            for (int k = j; k <= high; k++) {
	                B[i] = A[k];
	                i++;
	            }
	        }

	        for (int k = low; k < high; k++) {
	            A[k] = B[k - low];
	        }

	    }
	}
