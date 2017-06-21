package com.integration.ms.ali;

/**
 * 
 * 3.对于一个有序数组，我们通常采用二分查找的方式来定位某一元素，请编写二分查找的算法，在数组中查找指定元素。
 * 给定一个整数数组A及它的大小n，同时给定要查找的元素val，请返回它在数组中的位置(从0开始)，
 * 若不存在该元素，返回-1。若该元素出现多次，请返回第一次出现的位置。
 * 分析：重点在返回第一次出现的位置。
 * 
 * @author gaogao
 *
 */
public class Problem002 {
	
	private static class LinkNode{
		
		private int data;
		private LinkNode next;
		
		public LinkNode next(){
			return next;
		}

		public LinkNode(int data, LinkNode next) {
			this.data = data;
			this.next = next;
		}
		
		
	}
	
	public static LinkNode deleteMultiNode(LinkNode node){
		delete(node);
		return node;
	}
	
	private static void delete(LinkNode node){
		if(null!=node&& null!=node.next()){
			if(node.data==node.next.data){
				if(null!=node.next.next) {
					node.next = node.next.next();
					delete(node);
				}else{
					node.next = null;
				}
			}else{
				delete(node.next);
			}
		}
	}
	
	public static void main(String[] args) {
		LinkNode n5 = new LinkNode(5,null);
		LinkNode n4 = new LinkNode(3,n5);
		LinkNode n3 = new LinkNode(4,n4);
		LinkNode n2 = new LinkNode(4,n3);
		LinkNode n1 = new LinkNode(1,n2);
		LinkNode result = deleteMultiNode(n1);
		LinkNode tmp = result;
		while(true){
			if(null==tmp) break;
			System.out.println(tmp.data);
			if(null==tmp.next) break;
			tmp = tmp.next;
		}
	}
}
