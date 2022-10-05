package com.example.ADDB.ldap.queries;

import com.example.ADDB.config.LdapAttributeNames;
import org.springframework.ldap.query.LdapQuery;

public class LdapQueryAllEmployees implements PredefinedLdapQuery {
    @Override
    public LdapQuery buildQuery(String searchBase, LdapAttributeNames attributeNames) {
        return baseQuery(searchBase, attributeNames, Integer.MAX_VALUE);
    }

    @Override
    public String getQueryName() {
        return "ALL_EMPLOYEES";
    }
}
