package ztest.datastructure.tree;

import datastructure.tree.binarytree.BinaryTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BinaryTreeTest {

    private BinaryTree<Integer> binaryTree;

    @BeforeEach
    public void init() {

        binaryTree = new BinaryTree<>(4);
        binaryTree.setDebugging(true);
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
    public void testAdding() {

        int[] expected = new int[] {4, 5, 9, 1, 3, 6, 7, 20, 30};
        List<BinaryTree.Traverse> traversals = new ArrayList<>();
        traversals.add(BinaryTree.Traverse.RIGHT);
        binaryTree.addChild(traversals, BinaryTree.Traverse.LEFT, 20);
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 30);
        for(int x : binaryTree) {System.out.println(x);}

        int i = 0;
        for(int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }

        expected = new int[] {4, 5, 9, 1, 3, 6, 40, 7, 20, 30};
        traversals = new ArrayList<>();
        binaryTree.addChild(traversals, BinaryTree.Traverse.RIGHT, 40);

        System.out.println();
        for(int x : binaryTree) {System.out.println(x);}

        i = 0;
        for(int actual : binaryTree) {
            assertEquals(expected[i], actual);
            i++;
        }
    }
}