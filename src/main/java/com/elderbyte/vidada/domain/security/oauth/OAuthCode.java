package com.elderbyte.vidada.domain.security.oauth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name= "oauth_code")
public class OAuthCode {

    @Id
    @Column(length = 256)
    private String code;



    public String getCode() {
        return code;
    }
}
