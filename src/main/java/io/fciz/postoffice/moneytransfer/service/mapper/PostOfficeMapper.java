package io.fciz.postoffice.moneytransfer.service.mapper;

import io.fciz.postoffice.moneytransfer.domain.PostOffice;
import io.fciz.postoffice.moneytransfer.service.dto.PostOfficeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PostOffice} and its DTO {@link PostOfficeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostOfficeMapper extends EntityMapper<PostOfficeDTO, PostOffice> {}
