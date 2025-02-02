package blue_walnut.TrSever.repository;

import blue_walnut.TrSever.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Override
    <T extends Payment> T save(T history);
    Optional<Payment> findBySrl(Long srl);
    Optional<Payment> findByTrTid(String trTid);
    Optional<Payment> findByUserCi(String userCi);
    Optional<Payment> findByCreatedAtBetween(LocalDateTime startDt, LocalDateTime endDt);

    List<Payment> findAll();
}
