package model.lti;

public abstract class LTIRoles {

    // FIXME: use enumeratum!

    public enum SystemRole {

        SysAdmin("urn:lti:sysrole:ims/lis/SysAdmin"),
        SysSupport("urn:lti:sysrole:ims/lis/SysSupport"),
        Creator("urn:lti:sysrole:ims/lis/Creator"),
        AccountAdmin("urn:lti:sysrole:ims/lis/AccountAdmin"),
        User("urn:lti:sysrole:ims/lis/User"),
        Administrator("urn:lti:sysrole:ims/lis/Administrator"),
        None("urn:lti:sysrole:ims/lis/None");

        public final String fullUrn;

        SystemRole(String fullUrn) {
            this.fullUrn = fullUrn;
        }

    }

    public enum InstitutionRole {

        Student("urn:lti:instrole:ims/lis/Student"),
        Faculty("urn:lti:instrole:ims/lis/Faculty"),
        Member("urn:lti:instrole:ims/lis/Member"),
        Learner("urn:lti:instrole:ims/lis/Learner"),
        Instructor("urn:lti:instrole:ims/lis/Instructor"),
        Mentor("urn:lti:instrole:ims/lis/Mentor"),
        Staff("urn:lti:instrole:ims/lis/Staff"),
        Alumni("urn:lti:instrole:ims/lis/Alumni"),
        ProspectiveStudent("urn:lti:instrole:ims/lis/ProspectiveStudent"),
        Guest("urn:lti:instrole:ims/lis/Guest"),
        Other("urn:lti:instrole:ims/lis/Other"),
        Administrator("urn:lti:instrole:ims/lis/Administrator"),
        Observer("urn:lti:instrole:ims/lis/Observer"),
        None("urn:lti:instrole:ims/lis/None");

        public final String fullUrn;

        InstitutionRole(String fullUrn) {
            this.fullUrn = fullUrn;
        }

    }

}
