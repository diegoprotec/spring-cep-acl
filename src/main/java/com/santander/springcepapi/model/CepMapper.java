package com.santander.springcepapi.model;

import com.santander.springcepapi.model.entity.Cep;
import com.santander.springcepapi.model.vo.CepVo;
import com.santander.springcepapi.util.CepUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CepMapper {

    CepMapper INSTANCE = Mappers.getMapper(CepMapper.class);

    Cep toDocument(CepVo vo);

    @Mapping(target = "cepFormatado", source = "cep", qualifiedByName = "formatarCep")
    CepVo toVo(Cep cep);

    @Named("formatarCep")
    default String formatarCep(String cep) {
        return CepUtil.getFormatado(cep);
    }


}
