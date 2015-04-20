package com.elderbyte.vidada.domain.security.oauth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name="oauth_approvals")
public class OAuthAprovals {

    @Id
    @Column(length = 256)
    private String userId;
    @Column(length = 256)
    private String clientId;
    @Column(length = 256)
    private String scope;
    @Column(length = 256)
    private String status;

    private Date expiresAt;
    private Date lastModifiedAt;



    public String getUserId() {
        return userId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getScope() {
        return scope;
    }

    public String getStatus() {
        return status;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }
}
