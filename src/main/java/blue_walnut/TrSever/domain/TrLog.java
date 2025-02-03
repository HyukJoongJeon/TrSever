package blue_walnut.TrSever.domain;

import blue_walnut.TrSever.model.enums.ServiceType;
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
@ToString
public class TrLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long srl;
    private String userCi;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    private String trTid;
    private String issuerTid;
    private Long tokenSrl;

    private Long amount;
    private String errMsg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
