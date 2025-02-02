package blue_walnut.TrSever.service;

import blue_walnut.TrSever.client.IssuerClient;
import blue_walnut.TrSever.client.TspClient;
import blue_walnut.TrSever.domain.Payment;
import blue_walnut.TrSever.domain.TokenRegistry;
import blue_walnut.TrSever.domain.TrLog;
import blue_walnut.TrSever.exception.ErrorCode;
import blue_walnut.TrSever.exception.PaymentException;
import blue_walnut.TrSever.exception.TokenException;
import blue_walnut.TrSever.model.CardInfo;
import blue_walnut.TrSever.model.PaymentReq;
import blue_walnut.TrSever.model.PaymentRes;
import blue_walnut.TrSever.model.TokenRegReq;
import blue_walnut.TrSever.model.enums.ActionType;
import blue_walnut.TrSever.model.enums.ServiceType;
import blue_walnut.TrSever.model.enums.StatusType;
import blue_walnut.TrSever.repository.PaymentRepository;
import blue_walnut.TrSever.repository.TokenRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.InitBinder;

import java.time.LocalDateTime;
import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrService {
    @Value("${aes.secret-key}")
    private String SECRET_KEY;
    private final String TID_PREFIX = "TR-SERVICE";

    private final TspClient tspClient;
    private final IssuerClient issuerClient;

    private final TrLogService trLogService;

    private final TokenRepository tokenRepository;
    private final PaymentRepository paymentRepository;
    @InitBinder

    @Transactional
    public String tokenRegistry(CardInfo cardInfo) {
        log.info("토큰 발급 시작: userCi={}", cardInfo.userCi());
        try {
            TokenRegReq tokenRegReq = new TokenRegReq(cardInfo, SECRET_KEY);
            Long tokenSrl = tspClient.tokenRegistry(tokenRegReq);

            if (tokenRepository.findByTokenSrl(tokenSrl).isPresent())
                return "미사용 토큰이 존재합니다! Card_Ref_ID : [" + tokenSrl + "]";

            tokenRepository.save(createTokenRegistry(cardInfo.userCi(), tokenSrl));
            trLogService.tokenLogSave(StatusType.DN, cardInfo.userCi(), "토큰 발급 성공");

            return "토큰 발급에 성공했습니다! Card_Ref_ID : [" + tokenSrl + "]";
        } catch (Exception e) {
            trLogService.tokenLogSave(StatusType.FL, cardInfo.userCi(), e.getMessage());
        }
        return "토큰 발급 실패";
    }

    public String payment(PaymentReq request) {
        request.setTrTid(generateTrTid());
        Payment payment = createPayment(request);
        TrLog trLog = createdTrLog(request);

        trLogService.paymentLogSave(StatusType.WT, payment, trLog, null, null);

        try {
            if (isDuplicatePayment(request)) {
                log.warn("중복 결제 요청, userCi = {} - trTid = {}", request.getUserCi(), request.getTrTid());
                throw new PaymentException(ErrorCode.PAY_DUP);
            }

            PaymentRes paymentRes = processPaymentRequest(request);
            trLogService.paymentLogSave(StatusType.DN, payment, trLog, paymentRes, paymentRes.errMsg());

            return String.format("[주문번호 : %s] - 결제가 완료되었습니다", request.getTrTid());
        } catch (Exception e) {
            trLogService.paymentLogSave(StatusType.FL, payment, trLog, null, e.getMessage());
            throw e;
        }
    }

    public PaymentRes processPaymentRequest(PaymentReq request) {
        request.setToken(tspClient.requestToken(request.getCardRefId(), request.getUserCi()));
        return issuerClient.payment(request);
    }

    private String generateTrTid() {
        return TID_PREFIX + String.format("%015d", Calendar.getInstance().getTimeInMillis());
    }

    private TokenRegistry createTokenRegistry(String userCi, Long tokenSrl) {
        return TokenRegistry.builder()
                .userCi(userCi)
                .tokenSrl(tokenSrl)
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    private Payment createPayment(PaymentReq request) {
        return Payment.builder()
                .tokenSrl(request.getCardRefId())
                .userCi(request.getUserCi())
                .amount(request.getAmount())
                .trTid(request.getTrTid())
                .actionType(ActionType.PAY)
                .statusType(StatusType.WT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private TrLog createdTrLog(PaymentReq req) {
        return TrLog.builder()
                .userCi(req.getUserCi())
                .trTid(req.getTrTid())
                .serviceType(ServiceType.PAYMENT)
                .statusType(StatusType.WT)
                .amount(req.getAmount())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean isDuplicatePayment(PaymentReq request) {
        return paymentRepository.findByTrTid(request.getTrTid())
                .map(payment -> payment.getStatusType() == StatusType.DN || payment.getStatusType() == StatusType.FL)
                .orElse(false);
    }

    private void test() {
        throw new TokenException(ErrorCode.UNKNOWN_ERROR);
    }
}
