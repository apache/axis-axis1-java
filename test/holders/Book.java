package test.holders;

public class Book implements java.io.Serializable {
    private java.lang.String author;
    private java.lang.String title;
    private int isbn;

    public Book() {
    }

    public Book(
            java.lang.String author,
            int isbn,
            java.lang.String title) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    /**
         * Gets the author value for this Book.
         *
         * @return author
         */
    public java.lang.String getAuthor() {
        return author;
    }

    /**
         * Sets the author value for this Book.
         *
         * @param author
         */
    public void setAuthor(java.lang.String author) {
        this.author = author;
    }

    /**
         * Gets the title value for this Book.
         *
         * @return title
         */
    public java.lang.String getTitle() {
        return title;
    }

    /**
         * Sets the title value for this Book.
         *
         * @param title
         */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    /**
         * Gets the isbn value for this Book.
         *
         * @return isbn
         */
    public int getIsbn() {
        return isbn;
    }

    /**
     * Sets the isbn value for this Book.
     * 
     * @param isbn
     */
    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }
}
