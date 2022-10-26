package com.example.ADDB.Entity;

import com.example.ADDB.Model.EmployeeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.naming.Name;


@Mapper
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "manager", ignore = true)
    Employee employeeModeltoEmployee(EmployeeModel employeeModel);

    default String map(Name dn) {
        return dn.toString();
    }
}
