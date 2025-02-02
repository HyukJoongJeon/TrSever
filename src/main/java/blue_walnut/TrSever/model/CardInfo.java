package blue_walnut.TrSever.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record CardInfo(
    @NotBlank(message = "사용자 CI는 필수 값입니다")
    String userCi,

    @Pattern(regexp = "\\d{16}", message = "카드번호는 16자리 숫자로 입력해야 합니다.")
    @NotBlank(message = "카드번호는 필수 값입니다")
    String cardNo,

    @Pattern(regexp = "\\d{4}", message = "카드 유효기간은 4자리 년월 형식으로 입력해야 합니다.")
    @NotBlank(message = "카드 유효기간은 필수 값입니다")
    String vldDt,

    @Pattern(regexp = "\\d{2}", message = "카드 앞자리 2자리는 숫자로 입력해야 합니다.")
    @NotBlank(message = "카드 비밀번호는 필수 값입니다")
    String cardPwd) {
}
