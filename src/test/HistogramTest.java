package test;

import datastructures.table.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HistogramTest {

    private static Histogram actual;
    private static ArrayList<String> evenValues, oddValues;

    @BeforeAll
    public static void init() {

        evenValues = new ArrayList<>();
        oddValues  = new ArrayList<>();

        // 26 letters of alphabet
        evenValues.add("a");
        evenValues.add("a");
        evenValues.add("a");
        evenValues.add("b");
        evenValues.add("b");
        evenValues.add("c");
        evenValues.add("c");
        evenValues.add("c");
        evenValues.add("c");
        evenValues.add("d");
        evenValues.add("e");
        evenValues.add("f");
        evenValues.add("f");
        evenValues.add("f");
        evenValues.add("g");
        evenValues.add("h");
        evenValues.add("h");
        evenValues.add("h");
        evenValues.add("h");
        evenValues.add("h");
        evenValues.add("h");
        evenValues.add("i");
        evenValues.add("i");
        evenValues.add("j");
        evenValues.add("j");
        evenValues.add("j");
        evenValues.add("j");
        evenValues.add("j");
        evenValues.add("k");
        evenValues.add("k");
        evenValues.add("l");
        evenValues.add("m");
        evenValues.add("m");
        evenValues.add("m");
        evenValues.add("n");
        evenValues.add("o");
        evenValues.add("o");
        evenValues.add("p");
        evenValues.add("q");
        evenValues.add("q");
        evenValues.add("q");
        evenValues.add("r");
        evenValues.add("s");
        evenValues.add("s");
        evenValues.add("s");
        evenValues.add("s");
        evenValues.add("t");
        evenValues.add("u");
        evenValues.add("v");
        evenValues.add("v");
        evenValues.add("w");
        evenValues.add("x");
        evenValues.add("x");
        evenValues.add("y");
        evenValues.add("z");
        evenValues.add("z");
        evenValues.add("z");

        // 25 letters of alphabet (omit z)
        oddValues.add("a");
        oddValues.add("a");
        oddValues.add("a");
        oddValues.add("b");
        oddValues.add("b");
        oddValues.add("c");
        oddValues.add("c");
        oddValues.add("c");
        oddValues.add("c");
        oddValues.add("d");
        oddValues.add("e");
        oddValues.add("f");
        oddValues.add("f");
        oddValues.add("f");
        oddValues.add("g");
        oddValues.add("h");
        oddValues.add("h");
        oddValues.add("h");
        oddValues.add("h");
        oddValues.add("h");
        oddValues.add("h");
        oddValues.add("i");
        oddValues.add("i");
        oddValues.add("j");
        oddValues.add("j");
        oddValues.add("j");
        oddValues.add("j");
        oddValues.add("j");
        oddValues.add("k");
        oddValues.add("k");
        oddValues.add("l");
        oddValues.add("m");
        oddValues.add("m");
        oddValues.add("m");
        oddValues.add("n");
        oddValues.add("o");
        oddValues.add("o");
        oddValues.add("p");
        oddValues.add("q");
        oddValues.add("q");
        oddValues.add("q");
        oddValues.add("r");
        oddValues.add("s");
        oddValues.add("s");
        oddValues.add("s");
        oddValues.add("s");
        oddValues.add("t");
        oddValues.add("u");
        oddValues.add("v");
        oddValues.add("v");
        oddValues.add("w");
        oddValues.add("x");
        oddValues.add("x");
        oddValues.add("y");

        Collections.shuffle(evenValues);
        Collections.shuffle(oddValues);
    }

    @Test
    public void testEvenNoChange() {

        actual = new Histogram(evenValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a", 3);
        expected.put("b", 2);
        expected.put("c", 4);
        expected.put("d", 1);
        expected.put("e", 1);
        expected.put("f", 3);
        expected.put("g", 1);
        expected.put("h", 6);
        expected.put("i", 2);
        expected.put("j", 5);
        expected.put("k", 2);
        expected.put("l", 1);
        expected.put("m", 3);
        expected.put("n", 1);
        expected.put("o", 2);
        expected.put("p", 1);
        expected.put("q", 3);
        expected.put("r", 1);
        expected.put("s", 4);
        expected.put("t", 1);
        expected.put("u", 1);
        expected.put("v", 2);
        expected.put("w", 1);
        expected.put("x", 2);
        expected.put("y", 1);
        expected.put("z", 3);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testEvenDecrementOnce() {

        actual = new Histogram(evenValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-b", 5);
        expected.put("c-d", 5);
        expected.put("e-f", 4);
        expected.put("g-h", 7);
        expected.put("i-j", 7);
        expected.put("k-l", 3);
        expected.put("m-n", 4);
        expected.put("o-p", 3);
        expected.put("q-r", 4);
        expected.put("s-t", 5);
        expected.put("u-v", 3);
        expected.put("w-x", 3);
        expected.put("y-z", 4);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        actual.decrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testEvenDecrementTwice() {

        actual = new Histogram(evenValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-d", 10);
        expected.put("e-h", 11);
        expected.put("i-l", 10);
        expected.put("m-p", 7);
        expected.put("q-t", 9);
        expected.put("u-x", 6);
        expected.put("y-z", 4);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        actual.decrementNumBars();
        actual.decrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testMaxEvenDecrement() {

        actual = new Histogram(evenValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-z", 57);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    // starting from max decrement
    @Test
    public void testEvenIncrementOnce() {

        actual = new Histogram(evenValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-m", 34);
        expected.put("n-z", 23);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        actual.incrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    // starting from max decrement
    @Test
    public void testEvenIncrementTwice() {

        actual = new Histogram(evenValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-f", 14);
        expected.put("g-l", 17);
        expected.put("m-r", 11);
        expected.put("s-x", 11);
        expected.put("y-z", 4);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        actual.incrementNumBars();
        actual.incrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    // starting from max decrement
    @Test
    public void testMaxEvenIncrement() {

        actual = new Histogram(evenValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a", 3);
        expected.put("b", 2);
        expected.put("c", 4);
        expected.put("d", 1);
        expected.put("e", 1);
        expected.put("f", 3);
        expected.put("g", 1);
        expected.put("h", 6);
        expected.put("i", 2);
        expected.put("j", 5);
        expected.put("k", 2);
        expected.put("l", 1);
        expected.put("m", 3);
        expected.put("n", 1);
        expected.put("o", 2);
        expected.put("p", 1);
        expected.put("q", 3);
        expected.put("r", 1);
        expected.put("s", 4);
        expected.put("t", 1);
        expected.put("u", 1);
        expected.put("v", 2);
        expected.put("w", 1);
        expected.put("x", 2);
        expected.put("y", 1);
        expected.put("z", 3);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        for(int i = 0; i < 5; i++) {
            actual.incrementNumBars();
        }

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testOddNoChange() {

        actual = new Histogram(oddValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a", 3);
        expected.put("b", 2);
        expected.put("c", 4);
        expected.put("d", 1);
        expected.put("e", 1);
        expected.put("f", 3);
        expected.put("g", 1);
        expected.put("h", 6);
        expected.put("i", 2);
        expected.put("j", 5);
        expected.put("k", 2);
        expected.put("l", 1);
        expected.put("m", 3);
        expected.put("n", 1);
        expected.put("o", 2);
        expected.put("p", 1);
        expected.put("q", 3);
        expected.put("r", 1);
        expected.put("s", 4);
        expected.put("t", 1);
        expected.put("u", 1);
        expected.put("v", 2);
        expected.put("w", 1);
        expected.put("x", 2);
        expected.put("y", 1);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testOddDecrementOnce() {

        actual = new Histogram(oddValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-b", 5);
        expected.put("c-d", 5);
        expected.put("e-f", 4);
        expected.put("g-h", 7);
        expected.put("i-j", 7);
        expected.put("k-l", 3);
        expected.put("m-n", 4);
        expected.put("o-p", 3);
        expected.put("q-r", 4);
        expected.put("s-t", 5);
        expected.put("u-v", 3);
        expected.put("w-x", 3);
        expected.put("y", 1);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        actual.decrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testOddDecrementTwice() {

        actual = new Histogram(oddValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-d", 10);
        expected.put("e-h", 11);
        expected.put("i-l", 10);
        expected.put("m-p", 7);
        expected.put("q-t", 9);
        expected.put("u-x", 6);
        expected.put("y", 1);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        actual.decrementNumBars();
        actual.decrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testOddMaxDecrement() {

        actual = new Histogram(oddValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-y", 54);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    // starting from max decrement
    @Test
    public void testOddIncrementOnce() {

        actual = new Histogram(oddValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-l", 31);
        expected.put("m-x", 22);
        expected.put("y", 1);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        actual.incrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    // starting from max decrement
    @Test
    public void testOddIncrementTwice() {

        actual = new Histogram(oddValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a-f", 14);
        expected.put("g-l", 17);
        expected.put("m-r", 11);
        expected.put("s-x", 11);
        expected.put("y", 1);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        actual.incrementNumBars();
        actual.incrementNumBars();

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    // starting from max decrement
    @Test
    public void testOddIncrementMax() {

        actual = new Histogram(oddValues);

        TreeMap<String, Integer> expected = new TreeMap<>();

        expected.put("a", 3);
        expected.put("b", 2);
        expected.put("c", 4);
        expected.put("d", 1);
        expected.put("e", 1);
        expected.put("f", 3);
        expected.put("g", 1);
        expected.put("h", 6);
        expected.put("i", 2);
        expected.put("j", 5);
        expected.put("k", 2);
        expected.put("l", 1);
        expected.put("m", 3);
        expected.put("n", 1);
        expected.put("o", 2);
        expected.put("p", 1);
        expected.put("q", 3);
        expected.put("r", 1);
        expected.put("s", 4);
        expected.put("t", 1);
        expected.put("u", 1);
        expected.put("v", 2);
        expected.put("w", 1);
        expected.put("x", 2);
        expected.put("y", 1);

        ArrayList<Map.Entry<String, Integer>> expectedEntries = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> actualEntries   = new ArrayList<>();

        for(Map.Entry<String, Integer> expectedEntry : expected.entrySet()) {
            expectedEntries.add(expectedEntry);
        }

        // 5 is arbitrary
        for(int i = 0; i < 5; i++) {
            actual.decrementNumBars();
        }

        for(int i = 0; i < 5; i++) {
            actual.incrementNumBars();
        }

        for(Map.Entry<String, Integer> actualEntry : actual.getHistogram().entrySet()) {
            actualEntries.add(actualEntry);
        }

        assertEquals(expectedEntries.size(), actualEntries.size());

        for(int i = 0; i < expectedEntries.size(); i++) {

            System.out.println("Expected Key:   " + expectedEntries.get(i).getKey());
            System.out.println("Actual Key:     " + actualEntries.get(i).getKey());
            System.out.println("Expected Value: " + expectedEntries.get(i).getValue());
            System.out.println("Actual Value:   " + actualEntries.get(i).getValue());

            assertEquals(expectedEntries.get(i).getKey(), actualEntries.get(i).getKey());
            assertEquals(expectedEntries.get(i).getValue(), actualEntries.get(i).getValue());
        }
    }

    @Test
    public void testVerticalValues() {

        int[] expected = {0, 2, 4, 6, 8, 10};

        actual = new Histogram(evenValues);

        for(int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.getVerticalValues()[i]);
        }
    }
}