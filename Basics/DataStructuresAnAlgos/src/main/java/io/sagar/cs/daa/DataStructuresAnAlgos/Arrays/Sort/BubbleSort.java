package io.sagar.cs.daa.DataStructuresAnAlgos.Arrays.Sort;

import io.sagar.cs.daa.DataStructuresAnAlgos.Utilities.util;

public class BubbleSort {
	public static void sort(Comparable[] a) {
		boolean swapped = true;
		int j = 0;
		Comparable tmp;
		while(swapped) {
			swapped = false;
			j++;
			for(int i = 0; i < a.length - j; i++) {
				if(util.less(a[i + 1], a[i])) {
					tmp = a[i];
					a[i] = a[i + 1];
					a[i + 1] = tmp;
					swapped = true;
				}
			}
		}
	}
}
