package com.example.ADDB.ldap.queries;

import com.example.ADDB.Model.EmployeeModel;
import com.example.ADDB.config.LdapAttributeNames;
import com.example.ADDB.ldap.EmployeeAttributeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j

public class EmployeeRepositoyLdap {

    @Value("${mt.els.ldap.user-ou}")
    private String ldapUserOU;
    private static final String METRIC_NAME = "mt.els.ldap.query";
    private final EmployeeAttributeMapper employeeAttributeMapper;

    private final LdapAttributeNames attributeNames;

    private final LdapTemplate ldapTemplate;


    @Cacheable("employees")
    public List<EmployeeModel> queryMany(PredefinedLdapQuery query) {

        log.debug("Executing LDAP queryMany using {}", query.getClass().getName());

        List<EmployeeModel> searchResults = ldapTemplate.search(
                query.buildQuery(ldapUserOU, attributeNames),
                employeeAttributeMapper
        );


        log.debug("LDAP queryMany using {} returned {} results", query.getClass().getName(), searchResults.size());

        return searchResults;

    }
}
