package scratch.exception;

public class AuthenException extends RuntimeException{
    @Override
    public String getMessage() {
        return "账号密码错误";
    }
}
