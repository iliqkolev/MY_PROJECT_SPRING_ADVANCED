package MyChillZone.web;

import MyChillZone.security.AuthenticationMetadata;
import MyChillZone.transaction.model.Transaction;
import MyChillZone.transaction.model.service.TransactionService;
import MyChillZone.user.model.User;
import MyChillZone.user.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getAllTransactions(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        User user = userService.getById(authenticationMetadata.getUserId());
        List<Transaction> transactions = transactionService.getAllByOwnerId(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transactions");
        modelAndView.addObject("transactions", transactions);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getTransactionById(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        User user = userService.getById(authenticationMetadata.getUserId());
        Transaction transaction = transactionService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transaction-result");
        modelAndView.addObject("transaction", transaction);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
