package com.example.ADDB.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.Name;
import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @JsonProperty("firstName")
    @Column(name = "first_name")
    private String givenName;

    @Column(name = "last_name")
    @JsonProperty("lastName")
    private String surname;

    private String username;
    private String email;

    @Column(name = "telephone_number")
    private String telephoneNumber;

    @Column(name = "mobile_number")
    private String mobileNumber;

    private String title;
    private String department;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id", nullable = true)
    Employee manager ;


}
