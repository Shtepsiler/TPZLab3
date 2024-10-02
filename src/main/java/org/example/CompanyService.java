package org.example;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompanyService implements ICompanyService {
    @Override
    public Company getTopLevelParent(Company child) {
        if (child == null) {
            return null;
        }

        Set<Company> visited = new HashSet<>();
        Company current = child;

        while (current.getParent() != null && current != current.getParent()) {
            if (!visited.add(current)) {
                return current;
            }
            current = current.getParent();
        }

        return current;
    }

    private Set<Company> getAllChildrenAndCompanyToSet(Company company, List<Company> companies) {
        Set<Company> children = new HashSet<>();
        for (Company cmp : companies) {
            Set<Company> set = new HashSet<>();
            Company current = cmp;

            while (current != null) {
                set.add(current);
                if (current.equals(company)) {
                    children.addAll(set);
                    break;
                }
                current = current.getParent();
            }
        }
        return children;
    }

    @Override
    public long getEmployeeCountForCompanyAndChildren(Company company, List<Company> companies) {
        if (company == null) return 0;

        Set<Company> uniqueCompanies = new HashSet<>();
        Set<Company> children = this.getAllChildrenAndCompanyToSet(company, companies);

        long result = 0;
        for (Company cmp : children) {
            if (uniqueCompanies.add(cmp)) {
                result += cmp.getEmployeesCount();
                // Перевірка на переповнення
                if (result < 0) {
                    throw new ArithmeticException("Employee count overflow.");
                }
            }
        }

        return result;
    }

}
