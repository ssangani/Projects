package io.sagar.cs.daa.DataStructuresAnAlgos.Arrays.Sort;

import io.sagar.cs.daa.DataStructuresAnAlgos.Utilities.util;

public class ShellSort {
	public static void sort(Comparable[] a) {
		int h = 1, N = a.length;
		Comparable tmp;
		while(h < N/3)
			h = 3*h + 1;
		
		while(h > 0) {
			for(int i = h; i < N; i++) {
				int j = i;
				while(j >= h && util.less(a[j], a[j - h])) {
					tmp = a[j];
					a[j] = a[j - h];
					a[j - h] = tmp;
					j -= h;
				}
			}
			h /= 3;
		}
	}
}
