package jp.kobe_u.cs27.app.meetingroomreservation.domain.exception;

/**
 * 予約アプリのビジネス例外
 */
public class YoyakuAppException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    /* ユーザ系エラーコード */
    public static final int USER_NOT_FOUND = 100;
    public static final int INVALID_USER_ROLE = 101;
    public static final int USER_ALREADY_EXISTS = 102;
    public static final int INVALID_USER_UPDATE = 103;
    /* 会議室系エラーコード */
    public static final int ROOM_NOT_FOUND = 200;
    public static final int INVALID_ROOM_FORM = 201;
    public static final int ROOM_ALREADY_EXISTS = 202;
    public static final int INVALID_ROOM_UPDATE = 203;
    /* 予約系エラーコード */
    public static final int RESERVATION_NOT_FOUND = 300;
    public static final int INVALID_RESERVATION_MONTH = 301;
    public static final int INVALID_RESERVATION_DATE = 302;
    public static final int INVALID_RESERVATION_FORM = 303;
    public static final int ROOM_ALREADY_BOOKED = 304;
    public static final int RESERVATION_NOT_PERMITTED = 305;
    /* とにかくエラー */
    public static final int ERROR = 999;

    private int code; //この例外のエラーコード
    //コンストラクタ
    public YoyakuAppException(int code, String message) {
        super(message);
        this.code = code;
    }
    //例外をラップするコンストラクタ
    public YoyakuAppException(int code, String message, Exception cause) {
        super(message, cause);
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
