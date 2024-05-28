package io.fciz.postoffice.moneytransfer.service.mapper;

import io.fciz.postoffice.moneytransfer.domain.Citizen;
import io.fciz.postoffice.moneytransfer.service.dto.CitizenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Citizen} and its DTO {@link CitizenDTO}.
 */
@Mapper(componentModel = "spring")
public interface CitizenMapper extends EntityMapper<CitizenDTO, Citizen> {}
