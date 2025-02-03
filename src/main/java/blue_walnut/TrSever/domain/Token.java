package blue_walnut.TrSever.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long srl;
    private String userCi;
    private Long tokenSrl;
    private Boolean isUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
