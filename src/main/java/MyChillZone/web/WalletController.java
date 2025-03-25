package MyChillZone.web;

import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.transaction.model.Transaction;
import MyChillZone.user.model.User;
import MyChillZone.user.model.service.UserService;
import MyChillZone.wallet.model.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping()
    public ModelAndView getWalletPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());

        Map<UUID, List<Transaction>> lastFourTransactionsPerWallet = walletService.getLastFourTransactions(user.getWallets());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("wallet");
        modelAndView.addObject("user", user);
        modelAndView.addObject("lastFourTransactions", lastFourTransactionsPerWallet);

        return  modelAndView;
    }

    @PutMapping("/{id}/status")
    public String switchWalletStatus(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        walletService.switchStatus(id, authenticationMetadata.getUserId());

        return "redirect:/wallet";
    }

    @PutMapping("/{id}/balance/top-up")
    public String topUpWalletBalance(@PathVariable UUID id){

        Transaction transaction = walletService.topUp(id, BigDecimal.valueOf(20));

        return "redirect:/transactions/" + transaction.getId();
    }



}
