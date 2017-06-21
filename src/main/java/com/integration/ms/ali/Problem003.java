package com.integration.ms.ali;

/**
 * 
 * 2.给定一个排好序的链表，删除链表中重复的结点，返回链表头指针。 分析： (1)链表中重复的结点不需要保留一个，要全部删除。
 * (2)因为相同的结点全部要删除，所以我们设定三个指针，node指向当前节点,prev指向前驱，还有一个指向后继结点。一旦遇到node和后继结点相等，
 * 就node++,知道没有重复的再移动prev. (3)注意：头结点也可能有重复，所以也可能被删除，所以需要定义一个root指向头结点。
 * 
 * @author gaogao
 *
 */
public class Problem003 {

	/**
	 * @param srcArray
	 *            有序数组
	 * @param key
	 *            查找元素
	 * @return key的数组下标，没找到返回-1
	 */
	public static void main(String[] args) {
		int srcArray[] = {3,5,11,17,21,23,28,30,32,81,81,81,81,95,101};   
		System.out.println(binSearch(srcArray, 0, srcArray.length - 1, 81,-1));
	}

	// 二分查找递归实现
	public static int binSearch(int srcArray[], int start, int end, int key,int location) {
		int mid = (end - start) / 2 + start;
		if (start >= end) {   
            return location;  
		}
		if (srcArray[mid] == key) {
			location=(-1==location)?mid:(mid<location?mid:location);
			return binSearch(srcArray, start, mid-1, key,location);
		}else if (key > srcArray[mid]) {
			return binSearch(srcArray, mid + 1, end, key,location);
		} else if (key < srcArray[mid]) {
			return binSearch(srcArray, start, mid - 1, key,location);
		}
		System.err.println("11-"+location);
		return location;
	}

	// 二分查找普通循环实现
	public static int binSearch(int srcArray[], int key) {
		int mid = srcArray.length / 2;
		if (key == srcArray[mid]) {
			return mid;
		}

		int start = 0;
		int end = srcArray.length - 1;
		while (start <= end) {
			mid = (end - start) / 2 + start;
			if (key < srcArray[mid]) {
				end = mid - 1;
			} else if (key > srcArray[mid]) {
				start = mid + 1;
			} else {
				return mid;
			}
		}
		return -1;
	}
}
