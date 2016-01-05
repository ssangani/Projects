package io.sagar.cs.daa.DataStructuresAnAlgos;

import io.sagar.cs.daa.DataStructuresAnAlgos.Arrays.Sort.*;
import io.sagar.cs.daa.DataStructuresAnAlgos.Heaps.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //System.out.println( "Hello World!" );
    	String str = "";
    	Integer[] a = {1, 4, 0, 12, 9, 3, 15, 26, 2, 29, 17, -1};
    	for(int i = 0; i < a.length; i++)
    		str += a[i] + ", ";
	    str = str.substring(0, str.length() - 2);
	    System.out.println ("Unsorted arr: [" + str + "]");
	    
	    //SelectionSort.sort(a);
	    //InsertionSort.sort(a);
	    //BubbleSort.sort(a);
	    //ShellSort.sort(a);
	    //MergeSort.sort(a);
	    QuickSort.sort(a);
	    
	    Comparable[] arr = a;
	    /*
	    BinaryHeap sorter = new BinaryHeap(20, Integer.MIN_VALUE);
	    sorter.init(a);
	    //sorter.add(18);
	    Comparable[] arr = sorter.sort();
	    */
	    
	    str = "";
	    for(int i = 0; i < arr.length; i++)
	    	str += arr[i] + ", ";
	    str = str.substring(0, str.length() - 2);
	    System.out.println ("Sorted arr: [" + str + "]");
    }
}
