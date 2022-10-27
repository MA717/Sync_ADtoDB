package com.example.ADDB.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.naming.Name;

@Data
@Builder
public class EmployeeModel {

    @JsonIgnore
    private Name dn;

    @JsonProperty("firstName")
    private String givenName;

    @JsonProperty("lastName")
    private String surname;

    private String username;
    private String email;
    private String telephoneNumber;
    private String mobileNumber;
    private String title;
    private String department;
    private String manager ;
}
