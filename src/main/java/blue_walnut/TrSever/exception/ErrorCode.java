package blue_walnut.TrSever.exception;

public enum ErrorCode {
    // 재시도 가능 에러
    SERVER_500_ERR("5XX SERVER_ERROR_RETRY", "서버 에러로 재시도"),
    TOO_MANY_REQUESTS("TOO_MANY_REQUEST", "너무 많은 요청으로 재시도"),
    NETWORK_TIMEOUT("NETWORK_TIMEOUT", "타임아웃으로 재시도"),
    IO_ERROR("IO_ERROR", "I/O 에러로 재시도"),


    // 토큰 서버 관련 에러
    CARDINFO_DECRYPTION_FAILED("TKN_001", "카드 정보 복호화에 실패했습니다"),
    CARDINFO_ENCRYPTION_FAILED("TKN_002", "카드 정보 암호화에 실패했습니다"),
    CARDINFO_VALIDATE_FAILED("TKN_003", "유효하지 않은 카드입니다"),
    TOKEN_REGISTRATION_FAILED("TKN_004", "토큰 발급 실패"),
    TOKEN_VERIFICATION_FAILED("TKN_005", "토큰 처리 실패"),
    TOKEN_ALREADY_USED("TKN_006", "이미 사용된 토큰입니다"),
    TOKEN_NOT_FOUND("TKN_007", "토큰을 찾을 수 없습니다"),
    TOKEN_EXPIRED("TKN_008", "토큰의 유효기간이 만료되었습니다"),


    // 결제 서버 관련 에러
    PAY_DUPLICATE("PAY_001", "이미 처리된 결제 요청입니다"),
    PAY_FAILED("PAY_002", "결제 승인에 실패했습니다"),

    // 자체 에러
    TKN_REG_FAILED("TKN_000", "토큰 발급 처리중 오류 발생"),
    PAY_DUP("PAY_000", "처리가 완료된 건입니다"),
    ENC_FAILED("ENC_000", "카드 정보 암호화에 실패했습니다"),
    SERVER_ERROR("SERVER_ERROR", "처리 중 오류 발생"),
    UNKNOWN_ERROR("9999", "알 수 없는 오류");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorCode fromErrCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equalsIgnoreCase(code)) {
                return errorCode;
            }
        }
       return ErrorCode.UNKNOWN_ERROR;
    }
}