package org.example;

import java.util.Objects;
import java.util.UUID;

public class Company {
    private String id;
    private Company parent;
    private long employeesCount;

    public Company(Company parent, long employeesCount) {
        id = UUID.randomUUID().toString();
        this.parent = parent;
        this.employeesCount = employeesCount;
    }

    public Company getParent() {
        return parent;
    }
    public void setParent(Company Parent) {parent = Parent;}

    public long getEmployeesCount() {
        return employeesCount;
    }

    // Додаємо метод equals для порівняння компаній за їх властивостями
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return employeesCount == company.employeesCount &&
                Objects.equals(parent, company.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}