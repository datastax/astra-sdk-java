package com.datastax.astra.sdk.iam.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class CreateTokenResponse implements Serializable {
    
  /**  Serial.*/
  private static final long serialVersionUID = -2033488126365806669L;

  private String clientId;
    
  private List<String> roles;
  

}
