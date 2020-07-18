package ztest.datastructure.tree;

import datastructure.tree.binarytree.BinaryTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryTreeTest {

    private BinaryTree<Integer> binaryTree;

    @BeforeEach
    public void init() {

        binaryTree = new BinaryTree<>(4);
        List<BinaryTree.Traverse> traversals = new ArrayList<>();

        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 5);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 7);

        traversals.add(BinaryTree.Traverse.LEFT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 9);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 1);

        traversals.add(BinaryTree.Traverse.RIGHT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 3);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 6);
    }

    @Test
    public void testAdd1() {

        System.out.println("testAdd1");

        int[] expected = new int[]{4, 5, 9, 1, 3, 6, 7, 20, 30};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        traversals.add(BinaryTree.Traverse.RIGHT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 20);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 30);

        System.out.println(binaryTree.printStructure());

        int i = 0;
        for (int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }

    @Test
    public void testAdd2() {

        System.out.println("testAdd2");

        int[] expected = new int[] {4, 5, 9, 1, 3, 6, 20, 7};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 20);

        System.out.println(binaryTree.printStructure());

        int i = 0;
        for(int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }

    @Test
    public void testAdd3() {

        System.out.println("testAdd3");

        int[] expected = new int[] {4, 5, 9, 20, 30, 40, 1, 3, 6, 7};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        traversals.add(BinaryTree.Traverse.LEFT);
        traversals.add(BinaryTree.Traverse.LEFT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 20);
        traversals.add(BinaryTree.Traverse.LEFT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 30);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 40);

        System.out.println(binaryTree.printStructure());

        int i = 0;
        for(int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }

    @Test
    public void testAdd4() {

        System.out.println("testAdd4");

        int[] expected = new int[] {4, 5, 9, 20, 1, 3, 6, 7};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        traversals.add(BinaryTree.Traverse.LEFT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 20);

        System.out.println(binaryTree.printStructure());

        int i = 0;
        for(int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }

    @Test
    public void testAdd5() {

        System.out.println("testAdd5");

        int[] expected = new int[] {4, 5, 9, 1, 3, 20, 30, 6, 40, 7};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        traversals.add(BinaryTree.Traverse.LEFT);
        traversals.add(BinaryTree.Traverse.RIGHT);
        traversals.add(BinaryTree.Traverse.LEFT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 20);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 30);
        traversals.remove(traversals.size() - 1);
        traversals.add(BinaryTree.Traverse.RIGHT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 40);

        System.out.println(binaryTree.printStructure());

        int i = 0;
        for(int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }

    @Test
    public void testRemoveLeaf1() {

        System.out.println("testRemoveLeaf1");

        int[] expected = new int[]{4, 5, 9, 1, 3, 6};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        binaryTree.removeLeafNode(traversals, BinaryTree.Traverse.RIGHT);

        System.out.println(binaryTree.printStructure());

        int i = 0;
        for (int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }

    @Test
    public void testRemoveLeaf2() {

        System.out.println("testRemoveLeaf2");

        int[] expected = new int[]{4, 5, 9, 1, 6, 7};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        traversals.add(BinaryTree.Traverse.LEFT);
        traversals.add(BinaryTree.Traverse.RIGHT);
        binaryTree.removeLeafNode(traversals, BinaryTree.Traverse.LEFT);

        System.out.println(binaryTree.printStructure());

        int i = 0;
        for (int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }
}