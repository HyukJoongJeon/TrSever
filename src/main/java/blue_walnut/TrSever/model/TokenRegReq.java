package blue_walnut.TrSever.model;

import blue_walnut.TrSever.util.EncryptUtil;

public record TokenRegReq(String userCi, String cardNo, String vldDt, String cardPwd) {
    public TokenRegReq(CardInfo cardInfo, String secretKey) throws Exception {
        this(cardInfo.userCi(),
                EncryptUtil.encryptParam(secretKey, cardInfo.cardNo()),
                EncryptUtil.encryptParam(secretKey, cardInfo.cardPwd()),
                EncryptUtil.encryptParam(secretKey, cardInfo.vldDt()));
    }
}
