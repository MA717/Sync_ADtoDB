package com.example.ADDB.entity;

import com.example.ADDB.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.naming.Name;
import java.util.List;

@Mapper(componentModel = "spring")

public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "manager", ignore = true)
    EmployeeEntity employeeModeltoEmployeeEntity(Employee employee);
    default String map(Name dn) {
        return dn.toString();
    }


    @Mapping(target = "manager" , ignore = true )
    @Mapping(target = "dn" , ignore = true )
    Employee employeeEntitytoEmployeeModel(EmployeeEntity employeeEntity);


    List<Employee> mapEmployeeEntityListtoEmployeeModelList(List<EmployeeEntity> employeeList) ;
}
