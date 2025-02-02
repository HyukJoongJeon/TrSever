package blue_walnut.TrSever.controller;

import blue_walnut.TrSever.model.CardInfo;
import blue_walnut.TrSever.model.PaymentReq;
import blue_walnut.TrSever.service.TrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/tr")
public class TrController {

    private final TrService trService;


    @PostMapping("/tokenRegistry")
    public String tokenRegistry( @Valid @RequestBody CardInfo cardInfo, BindingResult bindingResult) {
        return trService.tokenRegistry(cardInfo);
    }

    @PostMapping("/payment")
    public String payment(@Valid @RequestBody PaymentReq request, BindingResult bindingResult) {
        return trService.payment(request);
    }
}