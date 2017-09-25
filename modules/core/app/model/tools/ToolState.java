package model.tools;

import model.user.Role;
import play.twirl.api.Html;

public enum ToolState {
  
  // @formatter:off
  LIVE("Verfügbare Tools", Role.USER),
  ALPHA("Tools in Alpha-Status", Role.ADMIN),
  BETA("Tools in Beta-Status",Role.ADMIN);
  // @formatter:on
  
  private String description;
  private Role requiredRole;
  
  private ToolState(String theDescription, Role theRequiredRole) {
    description = theDescription;
    requiredRole = theRequiredRole;
  }
  
  public Html getBadge() {
    if(this == LIVE)
      return new Html("");
    else
      return new Html("<sup>" + name() + "</sup>");
  }
  
  public String getDescription() {
    return description;
  }
  
  public Role getRequiredRole() {
    return requiredRole;
  }
  
}