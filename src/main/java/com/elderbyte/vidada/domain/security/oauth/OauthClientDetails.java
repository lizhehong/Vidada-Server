package com.elderbyte.vidada.domain.security.oauth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "oauth_client_details")
public class OauthClientDetails {

    @Id
    @Column(length = 256)
    private String client_id;

    @Column(length = 256)
    private String resource_ids;
    @Column(length = 256)
    private String client_secret;
    @Column(length = 256)
    private String scope;
    @Column(length = 256)
    private String authorized_grant_types;
    @Column(length = 256)
    private String web_server_redirect_uri;
    @Column(length = 256)
    private String authorities;


    private int access_token_validity;
    private int refresh_token_validity;
    @Column(length = 4096)
    private String additional_information;
    @Column(length = 4096)
    private String autoapprove;

}
