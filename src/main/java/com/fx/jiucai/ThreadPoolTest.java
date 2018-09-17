package com.fx.jiucai;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTest {
	
	private static volatile int a = 0;
	private static volatile boolean flag = true;
	
	
	public static void main(String[] args) throws Exception {
		while(flag) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					int nextInt = new Random().nextInt(10);
					System.out.println(nextInt);
					if(nextInt == 3) {
						if(a != 3) {
							flag = false;
							System.out.println("set>>>>");
							a = 3;
						}
					}
				}
			}).start();
		}
		System.out.println("end");
	}

}
