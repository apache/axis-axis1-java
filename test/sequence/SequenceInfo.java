/**
 * SequenceInfo.java
 *
 * Hand modified to add code that checks for proper serialization order.
 * @author: Rich Scheuerle (scheu@us.ibm.com)
 */

package test.sequence;

import java.util.Vector;

public class SequenceInfo implements java.io.Serializable {
    private int zero;
    private int one;
    private int two;
    private int three;
    private int four;
    private int five;
    private Vector v;

    public SequenceInfo() {
        v = new Vector();
    }

    public SequenceInfo(int zero, int one, int two, int three, int four, int five) {
        this.zero = zero;
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
    }

    public int getZero() {
        return zero;
    }

    public void setZero(int zero) {
        this.zero = zero;
        v.add("zero");
    }

    public int getOne() {
        return one;
    }

    public void setOne(int one) {
        this.one = one;
        v.add("one");
    }

    public int getTwo() {
        return two;
    }

    public void setTwo(int two) {
        this.two = two;
        v.add("two");
    }

    public int getThree() {
        return three;
    }

    public void setThree(int three) {
        this.three = three;
        v.add("three");
    }

    public int getFour() {
        return four;
    }

    public void setFour(int four) {
        this.four = four;
        v.add("four");
    }

    public int getFive() {
        return five;
    }

    public void setFive(int five) {
        this.five = five;
        v.add("five");
    }

    public Vector order() { 
        return v;
    }
}
