/**
 * 
 */
package com.eazybytes.accounts.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.accounts.feignclient.CardsClient;
import com.eazybytes.accounts.model.Accounts;
import com.eazybytes.accounts.model.Cards;
import com.eazybytes.accounts.model.Customer;
import com.eazybytes.accounts.repository.AccountsRepository;

/**
 * @author Eazy Bytes
 *
 */

@RestController
public class AccountsController {
	
	@Autowired
	private AccountsRepository accountsRepository;
	
	@Autowired
	private CardsClient cardsClient;
	
	@Value("${accounts.msg}")
	private String msg;

	@PostMapping("/myAccount")
	public Accounts getAccountDetails(@RequestBody Customer customer) {

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (accounts != null) {
			return accounts;
		} else {
			return null;
		}

	}
	
	@GetMapping("/msg")
	public String getMessage() {
		return this.msg;
	}
	
	@PostMapping("/myAccountAndCard")
	public Map<String,Object> getAccountAndCardDetials(@RequestBody Customer customer) {
		Map<String,Object> response= new HashMap<>();
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Cards> cards=cardsClient.getCardDetails(customer);
		response.put("Account", accounts);
		response.put("Cards", cards);
		return response;
		
	}

}
