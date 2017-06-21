package com.integration.common;

import java.io.Serializable;

public class Pair<T1, T2> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private T1 first;
	private T2 second;

	public Pair() {}

	public Pair(T1 t1, T2 t2) {
		this.first = t1;
		this.second = t2;
	}
	
	public T1 getFirst() {
		return this.first;
	}
	
	public T2 getSecond() {
		return this.second;
	}
	
	public void setFirst(T1 t1) {
		this.first = t1;
	}
	
	public void setSecond(T2 t2) {
		this.second = t2;
	}

	@Override
	public String toString() {
		return "Pair{" +
				"first=" + first +
				", second=" + second +
				'}';
	}
}
