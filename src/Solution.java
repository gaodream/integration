package com.ai.bdx.cncert.home.monitorManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Solution {
	
	public static void main(String[] args) {
		int n = 3;
		List<Map.Entry<Integer, Double>> result = new ArrayList<Map.Entry<Integer, Double>>();
		Map<Integer,Double> map = new HashMap<Integer,Double>();
		int[] index = new int[n]; 
		Integer[] base = {1,2,3,4,5,6};
		int count = 0;
		double max = Math.pow(6 , n);
		double total = Math.pow(6 , n);
		long start = System.currentTimeMillis();
		while(true){
			int key = 0;
			for(int i =0;i<n;i++){
				key = key + base[index[i]];
			}
			if(map.containsKey(key)){
				double tmp = map.get(key);
				map.put(key, tmp + 1);
			}else{
				map.put(key,1.0);
			}
			count ++ ;
			if(count >= max) break;
			int _count = count;
			int _n = n-1;
			while(true){
				index[_n] = _count % 6;
				_count = _count / 6;
				_n --; 
				if(_count==0||_n<0) break;
				//System.out.println("---sub---");
			}
			//System.out.println("------");
		}
		System.err.println(System.currentTimeMillis()-start);
		Iterator<Entry<Integer, Double>> itor = map.entrySet().iterator();
		while(itor.hasNext()){
			Entry<Integer, Double> entry = itor.next();
			double rate = new BigDecimal(entry.getValue()/total).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
			entry.setValue(rate);
			result.add(entry);
			System.out.println(entry.getKey()+"-"+entry.getValue());
		}
		
		//return result;
	}

}