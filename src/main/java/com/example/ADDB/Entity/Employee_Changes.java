package com.example.ADDB.Entity;

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
