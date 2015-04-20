package com.elderbyte.vidada.domain.security.oauth;

import javax.persistence.*;


@Entity
@Table(name = "oauth_client_token")
public class OAuthClientToken {

    @Id
    @Column(length = 256)
    private String token_id;

    @Lob
    private byte[] token;

    @Column(length = 256)
    private String authentication_id;
    @Column(length = 256)
    private String user_name;
    @Column(length = 256)
    private String client_id;

}
