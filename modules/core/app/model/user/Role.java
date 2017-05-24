package model.user;

public enum Role {

  USER, ADMIN, SUPERADMIN;

  public boolean isAdminRole() {
    return compareTo(ADMIN) >= 0;
  }

}
