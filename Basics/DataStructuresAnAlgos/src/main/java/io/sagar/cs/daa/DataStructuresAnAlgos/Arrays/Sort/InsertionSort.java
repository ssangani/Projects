package io.sagar.cs.daa.DataStructuresAnAlgos.Arrays.Sort;

import io.sagar.cs.daa.DataStructuresAnAlgos.Utilities.util;

public class InsertionSort {
	
	public static void sort(Comparable[] a) {
		Comparable tmp;
		for(int i = 1; i < a.length; i++) {
			int j = i;
			while(j > 0 && util.less(a[j], a[j - 1])) {
				tmp = a[j];
				a[j] = a[j - 1];
				a[j -1] = tmp;
				j--;
			}
		}
	}
}
