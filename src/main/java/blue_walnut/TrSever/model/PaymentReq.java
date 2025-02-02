package blue_walnut.TrSever.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class PaymentReq {
    @NotBlank(message = "사용자 CI는 필수 값입니다")
    @Schema(description = "사용자 정보")
    private String userCi;

    @NotNull(message = "Card_Ref_ID를 입력해 주세요")
    @Schema(description = "Card_Ref_ID")
    private Long cardRefId;

    @Schema(description = "토큰")
    private String token;

    @Schema(description = "결제 주문번호")
    private String trTid;

    @NotNull(message = "결제 금액을 입력해 주세요")
    @Min(value = 1, message = "결제 금액은 1원 이상이어야 합니다.")
    @Schema(description = "결제금액")
    private Long amount;
}
