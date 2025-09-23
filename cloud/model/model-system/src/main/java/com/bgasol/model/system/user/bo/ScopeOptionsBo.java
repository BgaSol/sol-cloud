package com.bgasol.model.system.user.bo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScopeOptionsBo {
    private boolean department = false;
    private String departmentColumnName;

    private boolean departmentList = false;
    private String departmentListTableName;
    private String departmentListJoinColumnName;
    private String departmentListInverseJoinColumnName;

    private boolean user = false;
    private String userColumnName;

    private boolean userList = false;
    private String userListTableName;
    private String userListJoinColumnName;
    private String userListInverseJoinColumnName;

    public boolean hasTrue() {
        return department || departmentList || user || userList;
    }
}
