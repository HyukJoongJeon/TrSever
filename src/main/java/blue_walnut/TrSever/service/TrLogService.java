package blue_walnut.TrSever.service;

import blue_walnut.TrSever.domain.Payment;
import blue_walnut.TrSever.domain.TokenRegistry;
import blue_walnut.TrSever.domain.TrLog;
import blue_walnut.TrSever.model.CardInfo;
import blue_walnut.TrSever.model.PaymentRes;
import blue_walnut.TrSever.model.enums.ServiceType;
import blue_walnut.TrSever.model.enums.StatusType;
import blue_walnut.TrSever.repository.PaymentRepository;
import blue_walnut.TrSever.repository.TokenRepository;
import blue_walnut.TrSever.repository.TrLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrLogService {
    private final TokenRepository tokenRepository;
    private final TrLogRepository trLogRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void tokenLogSave(StatusType statusType, String userCi, String errMsg) {
        try {
            TrLog trLog = TrLog.builder()
                    .userCi(userCi)
                    .errMsg(errMsg)
                    .statusType(statusType)
                    .serviceType(ServiceType.TK_REG)
                    .createdAt(LocalDateTime.now())
                    .build();

            trLogRepository.save(trLog);
        } catch (Exception e) {
            log.error("트랜잭션 로그 저장 중 오류 발생: {}", e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void paymentLogSave(StatusType statusType, Payment payment, TrLog trLog, PaymentRes result, String errMsg) {
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setStatusType(statusType);
        payment.setErrMsg(errMsg);

        if (ObjectUtils.isNotEmpty(result)) {
            payment.setIssuerTid(result.issuerTid());
            payment.setToken(result.token());
            payment.setDepositedAt(result.depositedAt());
            payment.setErrCode(result.errCode());

            TokenRegistry token = tokenRepository.findByTokenSrl(payment.getTokenSrl()).get();
            token.setUpdatedAt(LocalDateTime.now());
            token.setIsUsed(true);
            tokenRepository.save(token);
        }

        paymentRepository.save(payment);

        trLog.setStatusType(statusType);
        trLog.setErrMsg(errMsg);
        trLog.setUpdatedAt(LocalDateTime.now());
        trLogRepository.save(trLog);

    }
}
