package datastructures.rulegraph.component;

public class RuleNode {

    private final String data;
    private final boolean mutable;
    private final int id;
    private RuleNode[] children;

    public RuleNode(String data, boolean mutable, int id) {

        this.data = data;
        this.mutable = mutable;
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public boolean isMutable() {
        return mutable;
    }

    public int getId() {
        return id;
    }

    public void setChildren(RuleNode... children) {
        this.children = children;
    }

    public RuleNode[] getChildren() {
        return children;
    }

    public RuleNode getChild(int index) {
        return children[index];
    }

    public boolean hasChildren() {
        return (children == null) || children.length == 0;
    }

    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();

        print.append("Data: ").append(print).append("\n");
        print.append("Mutable: ").append(mutable).append("\n");
        print.append("ID: ").append(id).append("\n");

        return print.toString();
    }
}
