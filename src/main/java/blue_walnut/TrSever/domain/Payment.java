package blue_walnut.TrSever.domain;

import blue_walnut.TrSever.model.enums.ActionType;
import blue_walnut.TrSever.model.enums.StatusType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long srl;
    private Long tokenSrl;
    private String userCi;
    private String trTid;
    private String issuerTid;
    private Long amount;
    private String token;
    private String errMsg;
    private String errCode;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime depositedAt;
}
