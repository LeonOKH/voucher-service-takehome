package com.prizm.campaign.controller;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @PostMapping("/{code}/redeem")
    public RedeemResponse redeem(@PathVariable String code,
                                 @RequestParam String userId) {
        return voucherService.redeem(code, userId);
    }

    @PostMapping("/{code}/void")
    public RedeemResponse voidVoucher(@PathVariable String code) {
        return voucherService.voidVoucher(code);
    }
}
