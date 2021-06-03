package com.udacity.examples.Testing;

import org.junit.*;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HelperTest {
    @Test
    public void verify_getCount() {
        List<String> empNames = Arrays.asList("sareeta", "", "john", "");
        final long actual = Helper.getCount(empNames);
        assertEquals(2, actual);
    }

    @Test
    public void verify_getStats() {
        List<Integer> yrsOfExperience = Arrays.asList(13, 4, 15, 6, 17, 8, 19, 1, 2, 3);
        List<Integer> actualList = Arrays.asList(13, 4, 15, 6, 17, 8, 19, 1, 2, 3);
        IntSummaryStatistics stats = Helper.getStats(yrsOfExperience);
        assertEquals(19, stats.getMax());
        assertEquals(actualList, yrsOfExperience);
    }

    @Test
    public void compare_arrays() {
        int[] yrs = {10, 14, 2};
        int[] expectedYrs = {10, 14, 2};
        assertArrayEquals(expectedYrs, yrs);
    }

    @Test
    public void verify_merged_list() {
        List<String> empNames = Arrays.asList("sareeta", "", "john", "");
        assertEquals("sareeta, john", Helper.getMergedList(empNames));
    }

    @Before
    public void init() {
        System.out.println("each method");
    }

    @BeforeClass
    public static void setup() {
        System.out.println("before class");
    }

    @After
    public void initEnd() {
        System.out.println("each method");
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("after class");
    }

}
