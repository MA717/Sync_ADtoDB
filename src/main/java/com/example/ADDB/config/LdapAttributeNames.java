package com.example.ADDB.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "mt.els.ldap.attribute-names")
@Getter
@Setter
public class LdapAttributeNames {

    @NotBlank
    private String objectClass;

    @NotBlank
    private String directReports;

    @NotBlank
    private String manager;

    @NotBlank
    private String givenName;

    @NotBlank
    private String surname;

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotBlank
    private String telephoneNumber;

    @NotBlank
    private String title;

    @NotBlank
    private String mobileNumber;

    @NotBlank
    private String department;

    /**
    These attributes are the ones that should always be included in a ldap query. By specifying this limited set,
    the ldap server can control that only these attributes will be included in the response, thus lowering the overall
    times. With just over 300 employees, the response time of querying all of them could be lowered from 1.5s to about
    250ms - just by selecting only these attributes.
     */
    public String[] getAttributesToIncludeInQueryResponses() {
        return new String[] {
                givenName,
                surname,
                username,
                email,
                telephoneNumber,
                title,
                mobileNumber,
                department,
                manager
        };
    }
}
