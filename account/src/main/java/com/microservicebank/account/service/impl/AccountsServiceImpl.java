package com.microservicebank.account.service.impl;

import com.microservicebank.account.constants.AccountsConstants;
import com.microservicebank.account.dto.AccountsDto;
import com.microservicebank.account.dto.CustomerDto;
import com.microservicebank.account.entity.Accounts;
import com.microservicebank.account.entity.Customer;
import com.microservicebank.account.exception.CustomerAlreadyExistsException;
import com.microservicebank.account.exception.ResourceNotFoundException;
import com.microservicebank.account.mapper.IAccountsMapper;
import com.microservicebank.account.mapper.ICustomerMapper;
import com.microservicebank.account.repository.IAccountsRepository;
import com.microservicebank.account.repository.ICustomerRepository;
import com.microservicebank.account.service.IAccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private final IAccountsRepository accountsRepository;
    private final ICustomerRepository customerRepository;
    private final IAccountsMapper accountsMapper;
    private final ICustomerMapper customerMapper;

    @Override
    public void createAccount(CustomerDto customerDto) {
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.mobileNumber());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobileNumber "
                    + customerDto.mobileNumber());
        }
        Customer customer = customerMapper.updateEntity(customerDto, new Customer());
        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );
        AccountsDto accountsDto = accountsMapper.toDto(accounts);
        return customerMapper.toDto(customer, accountsDto);
    }

    /**
     * @param customerDto - CustomerDto Object
     * @return boolean indicating if the update of Account details is successful or not
     */
    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        AccountsDto accountsDto = customerDto.accountsDto();
        if (accountsDto == null) {
            return false;
        }

        Accounts accounts = accountsRepository.findById(accountsDto.accountNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "AccountNumber", accountsDto.accountNumber().toString())
        );
        accountsMapper.updateEntity(accountsDto, accounts);
        accounts = accountsRepository.save(accounts);

        Long customerId = accounts.getCustomerId();
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
        );
        customerMapper.updateEntity(customerDto, customer);
        customerRepository.save(customer);
        return true;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return boolean indicating if the delete of Account details is successful or not
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}