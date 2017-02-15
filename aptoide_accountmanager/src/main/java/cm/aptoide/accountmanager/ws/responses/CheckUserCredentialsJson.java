/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * Created by brutus on 09-12-2013.
 */
@Data public class CheckUserCredentialsJson {

  public String status;
  public String token;
  public int id;
  public String avatar;
  //default avatar
  public String ravatarHd;
  public String username;
  @JsonProperty("queueName") public String queueName;
  public List<ErrorResponse> errors;
  public Settings settings;
  public String access;
  @JsonProperty("access_confirmed") public Boolean accessConfirmed;
  public RepoDescription repoDescription;
  String repo;

  public boolean hasErrors() {
    return errors != null && errors.size() > 0;
  }

  @Data public static class RepoDescription {
    public long id_store;
    public String description;
    public String theme;
    public String items;
  }

  @Data public static class Settings {

    public String timeline;
    @JsonProperty("matureswitch") public String matureswitch;
  }
}
