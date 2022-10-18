package com.example.ADDB.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

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

    private String dn;

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


    public List<Changes> compareChanges(Employee employee) {
        List<Changes> changes = new ArrayList<>();

        Javers javers = JaversBuilder.javers().withListCompareAlgorithm(LEVENSHTEIN_DISTANCE).build();
        employee.setId(this.getId());
        proceedManagerChanger(changes, employee.getManager());

        Diff diff = javers.compare(this, employee);
        List<ValueChange> changedValue = diff.getChangesByType(ValueChange.class);

        return proceedChange(changedValue, changes);


    }


    private List<Changes> proceedManagerChanger(List<Changes> changes, Employee manager) {
        if (this.getManager() != null) {
            if (! this.getManager().getDn().toString().equals(manager.getDn().toString())) {
                this.setManager(manager);
                changes.add(Changes.MANAGER_Change);
            }
        } else if (manager != null) {
            this.setManager(manager);
            changes.add(Changes.MANAGER_Change);
        }

        return changes;
    }

    private List<Changes> proceedChange(List<ValueChange> changeList, List<Changes> changes) {


        for (ValueChange valueChange : changeList) {
            if (valueChange.getPropertyName().equals("givenName")) {
                this.setGivenName((String) valueChange.getRight());
                changes.add(Changes.FIRSTNAME_Change);
            }
            if (valueChange.getPropertyName().equals("surname")) {
                this.setSurname((String) valueChange.getRight());
                changes.add(Changes.SURNAME_Change);

            }
            if (valueChange.getPropertyName().equals("email")) {
                this.setEmail((String) valueChange.getRight());
                changes.add(Changes.EMAIL_Change);

            }
            if (valueChange.getPropertyName().equals("telephoneNumber")) {
                this.setTelephoneNumber((String) valueChange.getRight());
                changes.add(Changes.TELEPHONENUMBER_Change);
            }
            if (valueChange.getPropertyName().equals("title")) {
                this.setTitle((String) valueChange.getRight());
                changes.add(Changes.TITLE_Change);
            }
            if (valueChange.getPropertyName().equals("department")) {
                this.setDepartment((String) valueChange.getRight());
                changes.add(Changes.DEPARTMENT_Change);
            }
            if (valueChange.getPropertyName().equals("mobileNumber")) {
                this.setMobileNumber((String) valueChange.getRight());
                changes.add(Changes.MOBILENUMBER_Change);
            }

        }
        return changes;

    }
//
//
//    public List<Changes> changes(EmployeeModel employee, Employee manager) {
//        List<Changes> changesList = new ArrayList<>();
//
//        if (!this.getGivenName().equals(employee.getGivenName())) {
//            this.setGivenName(employee.getGivenName());
//            changesList.add(Changes.FIRSTNAME_Change);
//        }
//
//        if (!this.getSurname().equals(employee.getSurname())) {
//            this.setSurname(employee.getSurname());
//            changesList.add(Changes.SURNAME_Change);
//        }
//
//
//        if (!this.getDepartment().equals(employee.getDepartment())) {
//            this.setDepartment(employee.getDepartment());
//            //Veränderung in Department passiert
//            changesList.add(Changes.DEPARTMENT_Change);
//        }
//
//
//        if (this.getEmail() != null) {
//            if (!this.getEmail().equals(employee.getEmail())) {
//                this.setEmail(employee.getEmail());
//                changesList.add(Changes.EMAIL_Change);
//            }
//        } else if (employee.getEmail() != null) {
//            this.setEmail(employee.getEmail());
//            changesList.add(Changes.EMAIL_Change);
//
//        }
//
//
//        if (this.getTelephoneNumber() != null) {
//            if (!this.getTelephoneNumber().equals(employee.getTelephoneNumber())) {
//                this.setTelephoneNumber(employee.getTelephoneNumber());
//                changesList.add(Changes.TELEPHONENUMBER_Change);
//            }
//        } else if (employee.getTelephoneNumber() != null) {
//            this.setTelephoneNumber(employee.getTelephoneNumber());
//            changesList.add(Changes.TELEPHONENUMBER_Change);
//        }
//
//
//        if (!this.getTitle().equals(employee.getTitle())) {
//            this.setTitle(employee.getTitle());
//            changesList.add(Changes.TITLE_Change);
//        }
//
//
//        if (this.getMobileNumber() != null) {
//            if (!(this.getMobileNumber() == employee.getMobileNumber())) {
//                this.setMobileNumber(employee.getMobileNumber());
//                changesList.add(Changes.MOBILENUMBER_Change);
//            }
//        } else if (employee.getMobileNumber() != null) {
//            this.setMobileNumber(employee.getMobileNumber());
//            changesList.add(Changes.MOBILENUMBER_Change);
//        }
//
//        if (this.getManager() != null) {
//            if (this.getManager().getId() != manager.getId()) {
//                this.setManager(manager);
//                changesList.add(Changes.MANAGER_Change);
//            }
//        } else if (manager != null) {
//            this.setManager(manager);
//            changesList.add(Changes.MANAGER_Change);
//        }
//        return changesList;
//
//    }


}
