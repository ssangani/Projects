package io.sagar.cs.daa.DataStructuresAnAlgos.Arrays.Sort;

import io.sagar.cs.daa.DataStructuresAnAlgos.Utilities.util;

public class QuickSort {
	private static int partition(Comparable[] a, int lo, int hi) {
		int i = lo, j = hi + 1;
		Comparable p = a[lo], tmp;
		while(true) {
			while(util.less(a[++i], p)) {
				if(i == hi)
					break;
			}
			while(util.less(p, a[--j])) {
				if(j == lo)
					break;
			}
			if(i >= j)
				break;
			tmp = a[i];
			a[i] = a[j];
			a[j] = tmp;
		}
		tmp = a[lo];
		a[lo] = a[hi];
		a[hi] = tmp;
		return j;
	}
	
	private static void sort(Comparable[] a, int lo, int hi) {
		if(lo >= hi) {
			return;
		} else {
			int j = partition(a, lo, hi);
			sort(a, lo, j - 1);
			sort(a, j + 1, hi);
		}
	}
	
	public static void sort(Comparable[] a) {
		sort(a, 0, a.length - 1);
	}
}
