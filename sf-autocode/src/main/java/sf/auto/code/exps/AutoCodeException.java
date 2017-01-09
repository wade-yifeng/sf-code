package sf.auto.code.exps;


/**
 * Created by nijianfeng on 16/12/23.
 */
public class AutoCodeException extends RuntimeException {

    private AutoCodeException() {
    }

    private AutoCodeException(String message) {
        super(message);
    }

    private AutoCodeException(String message,Throwable throwable) {
        super(message,throwable);
    }

    public static AutoCodeException valueOf(String message){
        return new AutoCodeException(message);
    }

    public static AutoCodeException valueOf(String message,Throwable throwable){
        return new AutoCodeException(message,throwable);
    }

}
