package org.example;

import java.util.List;

public interface ICompanyService {
    Company getTopLevelParent(Company child);
    long getEmployeeCountForCompanyAndChildren(Company company, List<Company> companies);
}
