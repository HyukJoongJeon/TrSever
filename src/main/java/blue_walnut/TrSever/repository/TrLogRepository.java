package blue_walnut.TrSever.repository;

import blue_walnut.TrSever.domain.TrLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrLogRepository extends JpaRepository<TrLog, Long> {
    @Override
    <T extends TrLog> T save(T history);
    Optional<TrLog> findBySrl(Long srl);
    Optional<TrLog> findByUserCi(String userCi);
    Optional<TrLog> findByCreatedAtBetween(LocalDateTime startDt, LocalDateTime endDt);

    List<TrLog> findAll();
}
