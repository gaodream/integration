package com.ai.bdx.cncert.home.monitorManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Solution3 {
	
	public static void main(String[] args) {
		int n = 1;
		List<Map.Entry<Integer, Double>> result = new ArrayList<Map.Entry<Integer, Double>>();
		Map<Integer,Double> map = new HashMap<Integer,Double>();
		int count = 0;
		double max = Math.pow(6 , n);
		while(true){
			String[] six = BigInteger.valueOf(count).toString(6).split("");
			int length = six.length;
			int key = n;
			for(int i=0;i<length;i++){
				key = key + Integer.parseInt(six[i]);
			}
			if(map.containsKey(key)){
				double tmp = map.get(key);
				map.put(key, tmp + 1);
			}else{
				map.put(key, 1.0);
			}
			count ++ ;
			if(count >= max) break;
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