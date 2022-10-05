package com.example.ADDB.ldap.queries;

import com.example.ADDB.config.LdapAttributeNames;

import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;

public interface PredefinedLdapQuery {

    LdapQuery buildQuery(String searchBase, LdapAttributeNames attributeNames);
    String getQueryName();

    default ContainerCriteria baseQuery(String searchBase, LdapAttributeNames attributeNames, int countLimit) {
        return LdapQueryBuilder.query()
                .base(searchBase)
                .searchScope(SearchScope.ONELEVEL)
                .attributes(attributeNames.getAttributesToIncludeInQueryResponses())
                .countLimit(countLimit)
                .where(attributeNames.getObjectClass()).is("organizationalPerson")
                .and(attributeNames.getSurname()).isPresent();
    }
}
