package org.jglr.flows.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> {

    private final TreeNode<T> parent;
    private final String name;
    private final List<TreeNode<T>> children;
    private T content;

    public TreeNode(TreeNode<T> parent, String name) {
        this.parent = parent;
        this.name = name;
        children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public void addChild(TreeNode<T> node) {
        children.add(node);
    }

}
