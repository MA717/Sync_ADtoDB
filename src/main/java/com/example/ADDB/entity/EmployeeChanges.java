package com.example.ADDB.entity;


import com.example.ADDB.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmployeeChanges {
    Employee employee ;
    Changes changes ;
}
