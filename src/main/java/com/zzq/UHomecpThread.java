package com.zzq;

public class UHomecpThread implements Runnable{
	public User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public UHomecpThread(User user) {
		super();
		this.user = user;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Uhomecp_flow flow = new Uhomecp_flow();
			flow.flow(user);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("线程报错了，捕获："+e.getMessage());
		}
//		Thread.currentThread().notifyAll();
	}
	
	

}
