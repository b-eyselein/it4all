package model.user;

public enum Role {

  USER, ADMIN, SUPERADMIN;

  public boolean isAdminRole() {
    return this != USER;
  }

}
