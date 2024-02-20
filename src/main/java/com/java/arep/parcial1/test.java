package com.java.arep.parcial1;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        ArrayList<Integer> test = new ArrayList<>();
        test.add(1);
        test.add(9);
        test.add(6);
        test.add(0);
        test.add(7);
        System.out.println(test);
        System.out.println(quickSortCaller(test));
    }

    private static String quickSortCaller(List<Integer> unsorted_list) {
        return quickSort(unsorted_list, 0, unsorted_list.size() - 1).toString();
    }

    private static List<Integer> quickSort(List<Integer> list, int start, int end) {
        if (start < end) {
            int p = partition(list, start, end);
            System.out.println(list.subList(start, end) + " " + p);
            quickSort(list, start, p - 1);
            quickSort(list, p + 1, end);
        }
        return list;
    }

    private static int partition(List<Integer> A, int start, int end) {
        int pivot = A.get(end);
        System.out.println("pivot: " + pivot);
        int pivotIndex = end;
        int swap = start;

        while (swap < end) {
            if (A.get(swap) > pivot) {
                int swapValue = A.get(swap);
                int swapIndex = A.indexOf(swap);
                A.set(swapIndex, pivot);
                A.set(pivotIndex, swapValue);
                pivotIndex = swapIndex;
                swap += 1;
            } else {
                swap += 1;
            }
        }

        return pivotIndex;
    }
}
