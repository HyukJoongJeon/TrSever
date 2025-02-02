package blue_walnut.TrSever.domain;

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
public class TokenRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long srl;
    private String userCi;
    private Long tokenSrl;
    private Boolean isUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
