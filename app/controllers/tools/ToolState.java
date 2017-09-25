package controllers.tools;

import model.user.Role;

public enum ToolState {

  // @formatter:off
  LIVE("Verfügbare Tools", Role.USER),
  ALPHA("Tools in Alpha-Status", Role.ADMIN),
  BETA("Tools in Beta-Status",Role.ADMIN),
  EXPERIMENTAL("Experimentelle Tools", Role.SUPERADMIN);
  // @formatter:on

  private String description;
  private Role requiredRole;

  private ToolState(String theDescription, Role theRequiredRole) {
    description = theDescription;
    requiredRole = theRequiredRole;
  }

  public String getDescription() {
    return description;
  }

  public Role getRequiredRole() {
    return requiredRole;
  }

}