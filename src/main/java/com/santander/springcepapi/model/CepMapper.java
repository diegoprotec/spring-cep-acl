package com.santander.springcepapi.model;

import com.santander.springcepapi.model.entity.Cep;
import com.santander.springcepapi.model.vo.CepVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CepMapper {

    CepMapper INSTANCE = Mappers.getMapper(CepMapper.class);

    CepVo toVo(Cep cep);

    Cep toDocument(CepVo vo);
}
