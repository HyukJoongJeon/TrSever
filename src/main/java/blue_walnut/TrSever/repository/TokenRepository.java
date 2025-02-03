package blue_walnut.TrSever.repository;

import blue_walnut.TrSever.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Override
    <T extends Token> T save(T history);
    Optional<Token> findBySrl(Long srl);
    Optional<Token> findByTokenSrl(Long tokenSrl);
    Optional<Token> findByUserCi(String userCi);
    Optional<Token> findByCreatedAtBetween(LocalDateTime startDt, LocalDateTime endDt);

    List<Token> findAll();
}
