package test.holders;

public class ArrayOfBook implements java.io.Serializable {
    private test.holders.Book[] arrayOfBook;

    public ArrayOfBook() {
    }

    public ArrayOfBook(
            test.holders.Book[] arrayOfBook) {
        this.arrayOfBook = arrayOfBook;
    }

    public test.holders.Book[] getArrayOfBook() {
        return arrayOfBook;
    }

    public void setArrayOfBook(test.holders.Book[] arrayOfBook) {
        this.arrayOfBook = arrayOfBook;
    }

//    public test.holders.Book getArrayOfBook(int i) {
//        return this.arrayOfBook[i];
//    }
//
//    public void setArrayOfBook(int i, test.holders.Book _value) {
//        this.arrayOfBook[i] = _value;
//    }
}
