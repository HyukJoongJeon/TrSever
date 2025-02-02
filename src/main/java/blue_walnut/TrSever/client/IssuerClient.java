package blue_walnut.TrSever.client;

import blue_walnut.TrSever.exception.ErrorCode;
import blue_walnut.TrSever.exception.PaymentException;
import blue_walnut.TrSever.exception.RetryableException;
import blue_walnut.TrSever.exception.TokenException;
import blue_walnut.TrSever.model.PaymentReq;
import blue_walnut.TrSever.model.PaymentRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Slf4j
@Component("IssuerClient")
@RequiredArgsConstructor
public class IssuerClient {
    private final RestClient restClient = RestClient.create("http://localhost:8090/issuer");
    private final ObjectMapper objectMapper;

    //결제 요청을 처리
    @Retryable(value = { RetryableException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 1.5))
    public PaymentRes payment(PaymentReq request) {
        return exchange("/payment", request, PaymentRes.class);
    }
    //
    private <T> T exchange(String path, Object requestBody, Class<T> responseType) {
        try {
            return restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(responseType)
                    .getBody();
        } catch (HttpClientErrorException e) {
            // 4xx 에러는 클라이언트 측의 오류로 재시도할 필요가 없지만 429 Too Many Requests일 경우 재시도
            if (e.getStatusCode().is4xxClientError()) {
                if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                    throw new RetryableException(ErrorCode.TOO_MANY_REQUESTS);
                }
                throw new PaymentException(ErrorCode.SERVER_ERROR);
            }
            // 5xx 서버 오류는 재시도 필요
            if (e.getStatusCode().is5xxServerError()) {
                throw new RetryableException(ErrorCode.SERVER_500_ERR);
            }
        } catch (ResourceAccessException e) {
            // 네트워크 오류(타임아웃)도 재시도해야 하므로 RetryableException 던짐
            throw new RetryableException(ErrorCode.NETWORK_TIMEOUT);
        } catch (RestClientResponseException rcre) {
            throw new PaymentException(ErrorCode.fromErrCode(MapUtils.getString(extractErrorMessage(rcre), "code")));
        }
        throw new PaymentException(ErrorCode.UNKNOWN_ERROR);
    }

    private Map<String, Object> extractErrorMessage(RestClientResponseException rcre) {
        try {
            return objectMapper.readValue(rcre.getResponseBodyAsString(), Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error processing response body", e);
            return null;
        }
    }
}
