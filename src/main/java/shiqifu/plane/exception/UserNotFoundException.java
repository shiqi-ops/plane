package shiqifu.plane.exception;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(){

    }
    public UserNotFoundException(String message){
        super(message);
    }
}
