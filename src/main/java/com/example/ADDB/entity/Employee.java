package com.example.ADDB.entity;

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

        if (this.getManager() != null && manager != null ) {
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
            if(valueChange.getPropertyName().equals("username")) {
                this.setUsername( (String) valueChange.getRight());
                changes.add(Changes.USERNAME_Change);
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



}
