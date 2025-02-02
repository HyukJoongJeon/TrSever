package blue_walnut.TrSever.service;

import blue_walnut.TrSever.client.IssuerClient;
import blue_walnut.TrSever.client.TspClient;
import blue_walnut.TrSever.domain.Payment;
import blue_walnut.TrSever.domain.TrLog;
import blue_walnut.TrSever.exception.ErrorCode;
import blue_walnut.TrSever.exception.PaymentException;
import blue_walnut.TrSever.model.*;
import blue_walnut.TrSever.model.enums.StatusType;
import blue_walnut.TrSever.repository.PaymentRepository;
import blue_walnut.TrSever.repository.TokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TrServiceTest {
    @Mock
    private TspClient tspClient;  // 외부 API 호출 Mock
    private IssuerClient issuerClient;  // 외부 API 호출 Mock
    private static MockWebServer mockWebServer;

    @Mock
    private TokenRepository tokenRepository;  // Repository Mock
    @Mock
    private PaymentRepository paymentRepository;  // 결제 Repository Mock
    @Mock
    private TrLogService trLogService;  // 트랜잭션 로그 서비스 Mock

    @InjectMocks
    private TrService trService;  // 테스트 대상 클래스

    @Test
    void testTokenRegistry_Success() throws Exception {
        // Given
        CardInfo cardInfo = new CardInfo("ZAKK1123", "1434432156338765","1224", "22");
        TokenRegReq tokenRegReq = new TokenRegReq(cardInfo, "SxY2DtIE8CVB1DTabHqfZTTHIPybPJFg");
        Long tokenSrl = 123L;
        TokenReq token = new TokenReq(tokenSrl, "ZAKK1123");

        when(tspClient.tokenRegistry(tokenRegReq)).thenReturn(tokenSrl);  // 외부 API 호출 Mock
        when(tokenRepository.findByTokenSrl(tokenSrl)).thenReturn(Optional.empty());  // 토큰이 존재하지 않음

        // When
        String result = trService.tokenRegistry(cardInfo);

        // Then
        assertEquals("토큰 발급에 성공했습니다! Card_Ref_ID : [123]", result);
    }
/*
    @Test
    void testTokenRegistry_ExistingToken() throws Exception {
        // Given
        CardInfo cardInfo = new CardInfo("userCi123", "1434432156338765","1224", "22");
        Long tokenSrl = 123L;
        TokenRegReq tokenRegReq = new TokenRegReq(cardInfo, SECRET_KEY);
        TokenReq existingToken = new TokenReq(tokenSrl, "userCi123");

        when(tspClient.tokenRegistry(cardInfo)).thenReturn(tokenSrl);  // 외부 API 호출 Mock
        when(tokenRepository.findByTokenSrl(tokenSrl)).thenReturn(Optional.of(existingToken));  // 이미 존재하는 토큰

        // When
        String result = trService.tokenRegistry(cardInfo);

        // Then
        assertEquals("미사용 토큰이 존재합니다! Card_Ref_ID : [123]", result);
        verify(trLogService).tokenLogSave(StatusType.FL, "userCi123", "Token already exists");
    }

    @Test
    void testTokenRegistry_Exception() {
        // Given
        CardInfo cardInfo = new CardInfo("userCi123", "cardNumber");

        when(tspClient.tokenRegistry(cardInfo)).thenThrow(new RuntimeException("API error"));  // 외부 API 호출 예외

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> trService.tokenRegistry(cardInfo));

        // Then
        assertEquals("API error", exception.getMessage());
        verify(trLogService).tokenLogSave(StatusType.FL, "userCi123", "API error");
    }*/
@Test
void testPayment_Success() {
    // Given
/*    PaymentReq paymentReq = PaymentReq.builder().userCi("userCi123").amount(1000L).cardRefId(123L).build();
    Payment payment = Payment.builder().trTid("trTid123").userCi("userCi123").amount(1000L).build();
    TrLog trLog = TrLog.builder().trTid("trTid123").userCi("userCi123").build();

    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);  // 결제 저장 Mock
    when(trLogService.paymentLogSave(StatusType.WT, payment, trLog, null, null)).thenReturn(null);  // 로그 저장 Mock
    when(issuerClient.payment(any(PaymentReq.class))).thenReturn(new PaymentRes("SUCCESS", null));  // 외부 결제 처리 Mock

    // When
    String result = trService.payment(paymentReq);

    // Then
    assertEquals("[주문번호 : trTid123] - 결제가 완료되었습니다", result);
    verify(trLogService).paymentLogSave(StatusType.DN, payment, trLog, new PaymentRes("SUCCESS", null), null);*/
}

    @Test
    void testPayment_DuplicatePayment() {
        // Given
        PaymentReq paymentReq = PaymentReq.builder().userCi("ZAKK1123").amount(1000L).cardRefId(952L).build();

        when(trService.isDuplicatePayment(paymentReq)).thenReturn(true);  // 중복 결제 체크 Mock

        // When
        Exception exception = assertThrows(PaymentException.class, () -> trService.payment(paymentReq));

        // Then
        assertEquals(ErrorCode.PAY_DUP, ((PaymentException) exception).getCode());
        verify(trLogService).paymentLogSave(StatusType.FL, any(Payment.class), any(TrLog.class), null, "Duplicate payment");
    }

    @Test
    void testPayment_Exception() {
        // Given
        PaymentReq paymentReq = PaymentReq.builder().userCi("userCi123").amount(1000L).cardRefId(123L).build();
        Payment payment = Payment.builder().trTid("trTid123").userCi("userCi123").amount(1000L).build();
        TrLog trLog = TrLog.builder().trTid("trTid123").userCi("userCi123").build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);  // 결제 저장 Mock
        when(issuerClient.payment(paymentReq)).thenThrow(new RuntimeException("Payment API error"));  // 결제 API 예외 Mock

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> trService.payment(paymentReq));

        // Then
        assertEquals("Payment API error", exception.getMessage());
        verify(trLogService).paymentLogSave(StatusType.FL, payment, trLog, null, "Payment API error");
    }
}
