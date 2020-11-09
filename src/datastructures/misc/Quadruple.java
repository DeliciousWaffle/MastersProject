package datastructures.misc;

public class Quadruple<A, B, C, D> extends Triple<A, B, C> {

    private D fourth;

    public Quadruple(A first, B second, C third, D fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }

    public D getFourth() {
        return fourth;
    }

    public void setFourth(D fourth) {
        this.fourth = fourth;
    }

    @Override
    public String toString() {
        return "<" + super.getFirst() + ", " + super.getSecond() + ", " + super.getThird() + ", " + fourth + ">";
    }
}