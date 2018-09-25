package scanner;


import java.util.LinkedList;
import java.util.List;


public class Tree<T> {

	public T data;
    public Tree<T> parent;
    public List<Tree<T>> children;

    public Tree(T data) {
        this.data = data;
        this.children = new LinkedList<Tree<T>>();
    }

    public Tree<T> addChild(T child) {
        Tree<T> childNode = new Tree<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

}