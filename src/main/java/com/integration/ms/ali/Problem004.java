package com.integration.ms.ali;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 在一个KFC内，服务员负责生产食物，消费者负责消费食物；
 * 当生产到一定数量可以休息一下，直到消费完食物，再马上生产，一直循环；
 * @author gaogao
 *
 */
public class Problem004 {
	
	private static BlockingQueue<String> foods = new ArrayBlockingQueue<String>(200);
	
	
	static class Customer extends Thread{

		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(2000);
					String food = foods.poll(2, TimeUnit.SECONDS);
					System.out.println("消费："+food);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	static class Producer extends Thread{
		
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(1000);
					if(foods.size()<5){
						String tmp = UUID.randomUUID().toString();
						foods.put(tmp);
						System.out.println("生产"+tmp);
					}else{
						System.out.println("休息中。。。。。。");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		new Producer().start();
		new Customer().start();
	}
}
