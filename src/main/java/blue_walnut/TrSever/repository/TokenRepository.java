package blue_walnut.TrSever.repository;

import blue_walnut.TrSever.domain.TokenRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenRegistry, Long> {
    @Override
    <T extends TokenRegistry> T save(T history);
    Optional<TokenRegistry> findBySrl(Long srl);
    Optional<TokenRegistry> findByTokenSrl(Long tokenSrl);
    Optional<TokenRegistry> findByUserCi(String userCi);
    Optional<TokenRegistry> findByCreatedAtBetween(LocalDateTime startDt, LocalDateTime endDt);

    List<TokenRegistry> findAll();
}
