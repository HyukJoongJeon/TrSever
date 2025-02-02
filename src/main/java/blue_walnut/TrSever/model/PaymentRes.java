package blue_walnut.TrSever.model;

import blue_walnut.TrSever.model.enums.ActionType;

import java.time.LocalDateTime;

public record PaymentRes(
        String token,
        ActionType actionType,
        String trTid,
        Long amount,
        String issuerTid,
        String errCode,
        String errMsg,
        LocalDateTime depositedAt)
{}
