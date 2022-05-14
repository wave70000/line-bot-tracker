package com.line.bot.firebase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class FirebaseCredential {

    @Value("${firebase.type}")
    private String type;
    @Value("${project_id}")
    private String project_id;
    @Value("${private_key_id}")
    private String private_key_id;
    @Value("${private_key}")
    private String private_key;
    @Value("${client_email}")
    private String client_email;
    @Value("${client_id}")
    private String client_id;
    @Value("${auth_uri}")
    private String auth_uri;
    @Value("${token_uri}")
    private String token_uri;
    @Value("${auth_provider_x509_cert_url}")
    private String auth_provider_x509_cert_url;
    @Value("${client_x509_cert_url}")
    private String client_x509_cert_url;

}
