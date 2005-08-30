package test.faults;

public class BeanFault extends Exception {
    private String message;

    public BeanFault() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
