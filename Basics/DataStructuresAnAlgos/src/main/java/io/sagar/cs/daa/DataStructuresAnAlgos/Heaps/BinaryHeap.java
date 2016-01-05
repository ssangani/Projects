package io.sagar.cs.daa.DataStructuresAnAlgos.Heaps;

import io.sagar.cs.daa.DataStructuresAnAlgos.Utilities.util;

public class BinaryHeap {
	int N;
	int size;
	Comparable sentinel;
	Comparable[] a;
	
	public BinaryHeap(int size, Comparable sentinel) {
		this.N = 0;
		this.size = size;
		this.a = new Comparable[size];
		this.sentinel = sentinel;
	}
	
	public void init(Comparable[] arr) {
		if(arr.length == 0) {
			System.out.println("Empty Array!");
			return;
		}
		else if(arr.length > this.size) {
			System.out.println("Input exceeds capacity!");
			return;
		}
		else {
			for(int i = 0; i < arr.length; i++, N++) {
				this.a[i] = arr[i];
			}
		}
	}
	
	public static int parent(int i) {
		return (i/2);
	}
	
	public static int left(int i) {
		return (2*i);
	}
	
	public static int right(int i) {
		return (2*i + 1);
	}
	
	public void maxHeapify(int i) {
		int l = left(i), r = right(i), max = i;
		if(l < this.N && util.less(this.a[max], this.a[l])) {
			max = l;
		}
		if(r < this.N && util.less(this.a[max], this.a[r])) {
			max = r;
		}
		if(max != i) {
			Comparable tmp = a[max];
			a[max] = a[i];
			a[i] = tmp;
			this.maxHeapify(max);
		}
	}
	
	public void buildMaxHeap() {
		for(int i = (this.N - 1)/2; i >= 0; i--) {
			this.maxHeapify(i);
		}
	}
	
	public Comparable extractMax() {
		if(this.N < 1) {
			System.out.println("Heap Underflow!");
			return null;
		} else {
			Comparable tmp = this.a[0];
			this.a[0] = this.a[this.N - 1];
			this.N--;
			maxHeapify(0);
			return tmp;
		}
	}
	
	public void increaseKey (int i, Comparable key) {
		if(this.a[i].compareTo(key) > 0) {
			System.out.println("Key smaller than current key");
			return;
		} else {
			this.a[i] = key;
			Comparable tmp;
			while (i > 0 && util.less(this.a[parent(i)], this.a[i])) {
				tmp = this.a[i];
				this.a[i] = this.a[parent(i)];
				this.a[parent(i)] = tmp;
				i = parent(i);
			}
			return;
		}
	}
	
	public void add(Comparable key) {
		if(this.N >= this.size) {
			System.out.println("Heap Overflow!");
			return;
		} else {
			this.N++;
			this.a[this.N - 1] = this.sentinel;
			increaseKey(this.N - 1, key);
		}
	}
	
	public Comparable[] sort() {
		Comparable[] sortedArr = new Comparable[this.N];
		int n = this.N;
		this.buildMaxHeap();		
		for(int i = 0; i < n; i++) {
			sortedArr[n - 1 - i] = extractMax();
		}
		//sortedArr[0] = this.a[0];
		return sortedArr;
	}
	
	public void display() {
		String str = "";
	    for(int i = 0; i < this.N; i++)
	    	str += this.a[i] + ", ";
	    str = str.substring(0, str.length() - 2);
	    System.out.println ("Sorted arr: [" + str + "]");
	}
}
