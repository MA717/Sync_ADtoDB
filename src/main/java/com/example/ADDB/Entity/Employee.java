package com.example.ADDB.Entity;

import com.example.ADDB.Model.EmployeeModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

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

    private String dn ;

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
    Employee manager;


    public List<Changes> equals(EmployeeModel employee, Employee manager) {
        List<Changes> changesList = new ArrayList<>();

        if (!this.getGivenName().equals(employee.getGivenName())){
            this.setGivenName(employee.getGivenName());
            changesList.add(Changes.FIRSTNAME_Change);
        }

        if(!this.getSurname().equals(employee.getSurname())){
            this.setSurname(employee.getSurname());
            changesList.add(Changes.SURNAME_Change);
        }


        if (!this.getDepartment().equals(employee.getDepartment())) {
            this.setDepartment(employee.getDepartment());
            //Veränderung in Department passiert
            changesList.add(Changes.DEPARTMENT_Change);
        }


        if (this.getEmail() != null) {
            if (!this.getEmail().equals(employee.getEmail())) {
                this.setEmail(employee.getEmail());
                changesList.add(Changes.EMAIL_Change);
            }
        } else if (employee.getEmail() != null) {
            this.setEmail(employee.getEmail());
            changesList.add(Changes.EMAIL_Change);

        }


        if (this.getTelephoneNumber() != null) {
            if (!this.getTelephoneNumber().equals(employee.getTelephoneNumber())) {
                this.setTelephoneNumber(employee.getTelephoneNumber());
                changesList.add(Changes.TELEPHONENUMBER_Change);
            }
        } else if (employee.getTelephoneNumber() != null) {
            this.setTelephoneNumber(employee.getTelephoneNumber());
            changesList.add(Changes.TELEPHONENUMBER_Change);
        }


        if (!this.getTitle().equals(employee.getTitle())) {
            this.setTitle(employee.getTitle());
            changesList.add(Changes.TITLE_Change);
        }


        if (this.getMobileNumber() != null) {
            if (!(this.getMobileNumber() == employee.getMobileNumber())) {
                this.setMobileNumber(employee.getMobileNumber());
                changesList.add(Changes.MOBILENUMBER_Change);
            }
        } else if (employee.getMobileNumber() != null) {
            this.setMobileNumber(employee.getMobileNumber());
            changesList.add(Changes.MOBILENUMBER_Change);
        }

        if (this.getManager() != null) {
            if (this.getManager().getId() != manager.getId()) {
                this.setManager(manager);
                changesList.add(Changes.MANAGER_Change);
            }
        } else if (manager != null) {
            this.setManager(manager);
            changesList.add(Changes.MANAGER_Change);
        }
        return changesList;

    }
}
