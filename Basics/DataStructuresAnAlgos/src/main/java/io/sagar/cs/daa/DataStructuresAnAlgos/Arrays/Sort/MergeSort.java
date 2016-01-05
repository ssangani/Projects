package io.sagar.cs.daa.DataStructuresAnAlgos.Arrays.Sort;

import io.sagar.cs.daa.DataStructuresAnAlgos.Utilities.util;

public class MergeSort {
	
	public static void sort(Comparable[] a) {
		mergeSort(a, 0, a.length - 1);
	}
	
	public static void mergeSort(Comparable[] a, int lo, int hi) {
		if(lo >= hi) {
			return;
		} else {
			int mid = lo + (hi - lo)/2;
			mergeSort(a, lo, mid);
			mergeSort(a, mid + 1, hi);
			merge(a, lo, mid, hi);
		}
	}
	
	public static void merge(Comparable[] a, int lo, int mid, int hi) {
		int i = lo, j = mid + 1;
		Comparable[] aux = new Comparable[a.length];
		for(int k = lo; k <= hi; k++) {
			aux[k] = a[k];
		}
		
		for(int k = lo; k <= hi; k++) {
			if(i > mid) {
				a[k] = aux[j++];
			}
			else if(j > hi) {
				a[k] = aux[i++];
			}
			else if(util.less(aux[i], aux[j])) {
				a[k] = aux[i++];
			}
			else {
				a[k] = aux[j++];
			}
		}
	}
}
