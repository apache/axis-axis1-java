package test.holders.holders;

public final class BookHolder implements javax.xml.rpc.holders.Holder {
    public test.holders.Book value;

    public BookHolder() {
    }

    public BookHolder(test.holders.Book value) {
        this.value = value;
    }

}
