package com.example.ADDB.ldap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.naming.Name;
import javax.naming.ldap.LdapName;

@Component
public class LdapNameUtil {
    @Value("${spring.ldap.base}")
    private String ldapBase;
    private LdapName ldapBaseName;

    @PostConstruct
    public void init() {
        this.ldapBaseName = LdapNameBuilder.newInstance(this.ldapBase).build();
    }

    public Name addLdapBase(Name name) {
        LdapNameBuilder builder = LdapNameBuilder.newInstance();

        builder.add(ldapBaseName);
        builder.add(name);

        return builder.build();
    }

    public Name removeLdapBase(Name name) {
        LdapNameBuilder builder = LdapNameBuilder.newInstance();

        for (int i = ldapBaseName.size(); i < name.size(); i++) {
            builder.add(name.get(i));
        }

        return builder.build();
    }
}
