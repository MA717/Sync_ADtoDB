package com.example.ADDB.ldap;

import com.example.ADDB.model.Employee;
import com.example.ADDB.config.LdapAttributeNames;

import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;

@Component
@RequiredArgsConstructor
public class EmployeeAttributeMapper implements ContextMapper<Employee> {

    private final LdapNameUtil ldapNameUtil;

    private final LdapAttributeNames attributeNames;

    @Override
    public Employee mapFromContext(Object ctx) throws NamingException {
        DirContextAdapter context = (DirContextAdapter) ctx;

        return Employee.builder()
                .dn(ldapNameUtil.addLdapBase(context.getDn()))
                .givenName(context.getStringAttribute(attributeNames.getGivenName()))
                .surname(context.getStringAttribute(attributeNames.getSurname()))
                .username(context.getStringAttribute(attributeNames.getUsername()))
                .email(context.getStringAttribute(attributeNames.getEmail()))
                .telephoneNumber(context.getStringAttribute(attributeNames.getTelephoneNumber()))
                .title(context.getStringAttribute(attributeNames.getTitle()))
                .mobileNumber(context.getStringAttribute(attributeNames.getMobileNumber()))
                .department(context.getStringAttribute(attributeNames.getDepartment()))
                .manager(context.getStringAttribute(attributeNames.getManager()))
                .build();
    }
}
