export interface User {
  _type: 'model.RegisteredUser' | 'model.LtiUser';
  stdRole: 'RoleUser' | 'RoleAdmin' | 'RoleSuperAdmin';
  username: string;
  token: string;
}
