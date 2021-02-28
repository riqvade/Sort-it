package com.riqvade;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TestSort {
    @Test
    public void testDescendingSorting() throws Exception {
        SortManager<Integer> integerSortManager = new SortManager<>(true, Integer::parseInt);
        Collection<List<Integer>> inputs = new ArrayList<>();
        inputs.add(Arrays.asList(2, 5));
        inputs.add(Arrays.asList(4, 3));
        inputs.add(Arrays.asList(8, 9));
        ArrayList<Integer> out = new ArrayList<>();
        integerSortManager.ofCollections(inputs, out);
        integerSortManager.sortData();
        System.out.println(out);
        Assert.assertEquals(Arrays.asList(9,8,5,3,2), out);
    }

    @Test
    public void testAscendingSorting() throws Exception {
        SortManager<Integer> integerSortManager = new SortManager<>(false, Integer::parseInt);
        Collection<List<Integer>> inputs = new ArrayList<>();
        inputs.add(Arrays.asList(1, 8));
        inputs.add(Arrays.asList(3, 4));
        inputs.add(Arrays.asList(8, 5));
        ArrayList<Integer> out = new ArrayList<>();
        integerSortManager.ofCollections(inputs, out);
        integerSortManager.sortData();
        System.out.println(out);
        Assert.assertEquals(Arrays.asList(1,3,4,8,8), out);
    }
}
