package com.ai.bdx.cncert.home.monitorManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Solution2 {
	
	public static void main(String[] args) {
		int n = 3;
		List<Map.Entry<Integer, Double>> result = new ArrayList<Map.Entry<Integer, Double>>();
		Map<Integer,Double> map = new HashMap<Integer,Double>();
		int[] index = new int[n]; 
		Integer[] base = {1,2,3,4,5,6};
		int count = 0;
		double max = Math.pow(6 , n);
		int _count = 1;
		for(int i=n;i<=n*6;i++){
			map.put(i, 0.0);
		}
		while(true){
			int key = 0;
			//System.out.println("key="+JSON.toJSONString(index));
			for(int i =n-1;i>=0;i--){
				key = key + base[index[i]];
				if(_count>0){
					index[i] = _count % 6;
					_count = _count / 6;
				}
			}
			double tmp = map.get(key);
			map.put(key, tmp + 1);
			count ++ ;
			if(count >= max) break;
			_count = count + 1;
		}
		Iterator<Entry<Integer, Double>> itor = map.entrySet().iterator();
		while(itor.hasNext()){
			Entry<Integer, Double> entry = itor.next();
			double rate = BigDecimal.valueOf(entry.getValue()/count).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();  
			entry.setValue(rate);
			result.add(entry);
			System.out.println(entry.getKey()+"-"+entry.getValue());
		}
		
		//return result;
	}

}