package com.elderbyte.vidada.domain.security.oauth;

import javax.persistence.*;

/**
 * Created by IsNull on 20.04.15.
 */
@Entity
@Table(name="oauth_refresh_token")
public class OAuthRefreshToken {

    @Id
    @Column(length = 256)
    private String token_id;
    @Lob
    private byte[] token;
    @Lob
    private byte[] authentication;
}
