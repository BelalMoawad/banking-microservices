package com.microservicebank.account.mapper;

import com.microservicebank.account.dto.AccountsDto;
import com.microservicebank.account.dto.CustomerDto;
import com.microservicebank.account.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICustomerMapper {

    CustomerDto toDto(Customer customer, AccountsDto accountsDto);

    Customer updateEntity(CustomerDto customerDto, @MappingTarget Customer customer);
}
