package shiqifu.plane.exception;

public class DataErrorException extends RuntimeException {
    public DataErrorException(String message) {
        super(message);
    }
    public DataErrorException() {
        super();
    }
}
