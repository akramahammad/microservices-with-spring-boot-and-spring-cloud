/**
 * 
 */
package com.eazybytes.accounts.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.accounts.feignclient.CardsClient;
import com.eazybytes.accounts.feignclient.LoansClient;
import com.eazybytes.accounts.model.Accounts;
import com.eazybytes.accounts.model.Cards;
import com.eazybytes.accounts.model.Customer;
import com.eazybytes.accounts.model.Loans;
import com.eazybytes.accounts.repository.AccountsRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;

/**
 * @author Eazy Bytes
 *
 */

@RestController
public class AccountsController {
	
	private static Logger logger=LoggerFactory.getLogger(AccountsController.class);
	
	@Autowired
	private AccountsRepository accountsRepository;
	
	@Autowired
	private CardsClient cardsClient;
	
	@Autowired
	private LoansClient loansClient;
	
	@Value("${accounts.msg}")
	private String msg;

	@PostMapping("/myAccount")
	@Timed(value = "getAccountDetails.time", description = "Time taken to get Account Details")
	public Accounts getAccountDetails(@RequestBody Customer customer) {

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (accounts != null) {
			return accounts;
		} else {
			return null;
		}

	}
	
	@GetMapping("/msg")
	@RateLimiter(name="getMessage",fallbackMethod = "getDefaultMessage")
	public String getMessage() {
		return this.msg;
	}
	
	@GetMapping("/authmsg")
	public String getAuthenticationMessage() {
		return "Authenticated User";
	}
	
	@GetMapping("/rolemsg")
	public String getAuthorizationMessage() {
		return "Authorized User";
	}
	
	private String getDefaultMessage(Throwable t) {
		return "Default Message";
	}
	
	@PostMapping("/myAccountAndCard")
	@Retry(name= "detailsForCustomerSupport", fallbackMethod = "fetchAccountDetails")
	public Map<String,Object> getAccountAndCardDetails(@RequestBody Customer customer) {
		logger.info("Inside getAccount and Card Details");
		Map<String,Object> response= new HashMap<>();
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Cards> cards=cardsClient.getCardDetails(customer);
		response.put("Account", accounts);
		response.put("Cards", cards);
		return response;
		
	}
	
	@PostMapping("/myDetails")
	@CircuitBreaker(name= "detailsForCustomerSupport", fallbackMethod = "fetchAccountDetails")
	public Map<String,Object> getDetails(@RequestBody Customer customer) {
		logger.info("Inside getDetails");
		Map<String,Object> response= new HashMap<>();
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Cards> cards=cardsClient.getCardDetails(customer);
		List<Loans> loans=loansClient.getLoansDetails(customer);
		response.put("Account", accounts);
		response.put("Cards", cards);
		response.put("Loans", loans);
		return response;
		
	}
	
	private Map<String,Object> fetchAccountDetails(Customer customer,Throwable t){
		Map<String,Object> response= new HashMap<>();
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		response.put("Account", accounts);
		return response; 
	}
	
	

}
