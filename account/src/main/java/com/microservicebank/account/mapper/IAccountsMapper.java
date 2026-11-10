package com.microservicebank.account.mapper;

import com.microservicebank.account.dto.AccountsDto;
import com.microservicebank.account.entity.Accounts;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IAccountsMapper {

    AccountsDto toDto(Accounts accounts);

    Accounts updateEntity(AccountsDto accountsDto, @MappingTarget Accounts accounts);
}
