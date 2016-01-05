package io.sagar.cs.daa.DataStructuresAnAlgos.Arrays.Sort;

import io.sagar.cs.daa.DataStructuresAnAlgos.Utilities.util;

public class SelectionSort {	
	public static void sort(Comparable[] a) {
		Comparable tmp;
		for(int i = 0; i < a.length; i++) {
			int min = i;
			for(int j = i + 1; j < a.length; j++) {
				if(util.less(a[j], a[min])) {
					min = j;
				}
			}
			tmp = a[i];
			a[i] = a[min];
			a[min] = tmp;
		}
	}

}
