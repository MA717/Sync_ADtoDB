package com.example.ADDB.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Employee_Changes {

    Employee employee ;
    List<Changes> changesList ;
}
