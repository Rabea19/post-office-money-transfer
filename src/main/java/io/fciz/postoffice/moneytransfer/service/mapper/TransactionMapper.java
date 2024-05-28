package io.fciz.postoffice.moneytransfer.service.mapper;

import io.fciz.postoffice.moneytransfer.domain.Citizen;
import io.fciz.postoffice.moneytransfer.domain.PostOffice;
import io.fciz.postoffice.moneytransfer.domain.Transaction;
import io.fciz.postoffice.moneytransfer.service.dto.CitizenDTO;
import io.fciz.postoffice.moneytransfer.service.dto.PostOfficeDTO;
import io.fciz.postoffice.moneytransfer.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
    @Mapping(target = "sender", source = "sender", qualifiedByName = "citizenNationalId")
    @Mapping(target = "receiver", source = "receiver", qualifiedByName = "citizenNationalId")
    @Mapping(target = "senderPostOffice", source = "senderPostOffice", qualifiedByName = "postOfficeName")
    @Mapping(target = "receiverPostOffice", source = "receiverPostOffice", qualifiedByName = "postOfficeName")
    TransactionDTO toDto(Transaction s);

    @Named("citizenNationalId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nationalId", source = "nationalId")
    CitizenDTO toDtoCitizenNationalId(Citizen citizen);

    @Named("postOfficeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PostOfficeDTO toDtoPostOfficeName(PostOffice postOffice);
}
