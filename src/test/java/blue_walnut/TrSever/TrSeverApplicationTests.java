package blue_walnut.TrSever;

import blue_walnut.TrSever.util.EncryptUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TrSeverApplication.class)
class TrSeverApplicationTests {
	@Value("${aes.secret-key}")
	private static String SECRET_KEY;

	@Test
	void contextLoads() {
        try {
            System.err.println(SECRET_KEY);
            System.err.println(EncryptUtil.encryptParam("SxY2DtIE8CVB1DTabHqfZTTHIPybPJFg", "1234432156788765"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
