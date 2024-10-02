package org.example;

import org.junit.jupiter.api.*;
import java.util.List;

public class CompanyServiceTest {
    private final Company main = new Company(null, 3);
    private final Company bookkeeping = new Company(main, 2);
    private final Company hr = new Company(main, 1);
    private final Company it = new Company(main, 5);
    private final Company subHr = new Company(hr, 1);
    private final Company subIt = new Company(it, 2);

    List<Company> companies = List.of(main, bookkeeping, hr, it, subHr, subIt);

    private final ICompanyService underTest = new CompanyService();

    @Test
    void whenCompanyIsNull_thenReturnNull() {
        Company result = underTest.getTopLevelParent(null);
        Assertions.assertNull(result, "Expected null when passing null as company.");
    }


    @Test
    void whenSingleCompanyHasNoParent_thenItIsItsOwnTopLevelParent() {
        Company result = underTest.getTopLevelParent(main);
        Assertions.assertEquals(main, result, "The company should be its own top-level parent.");
    }

    @Test
    void whenCompanyIsDeepNested_thenFindCorrectTopLevelParent() {
        Company result = underTest.getTopLevelParent(subHr);
        Assertions.assertEquals(main, result, "Top level parent for deep nested company should be correct.");
    }

    @Test
    void whenCompanyHasSelfAsParent_thenReturnItself() {
        Company selfParent = new Company(null, 3);
        selfParent.setParent(selfParent); // Компанія має себе як батька

        Company result = underTest.getTopLevelParent(selfParent);
        Assertions.assertEquals(selfParent, result, "Company with itself as parent should return itself as top-level parent.");
    }
    // Тести на getEmployeeCountForCompanyAndChildren

    @Test
    void whenNoCompanies_thenEmployeeCountIsZero() {
        long result = underTest.getEmployeeCountForCompanyAndChildren(main, List.of());
        Assertions.assertEquals(0, result, "If there are no companies, employee count should be zero.");
    }

    @Test
    void whenCompanyIsNotInList_thenEmployeeCountIsZero() {
        Company newCompany = new Company(null, 5);
        long result = underTest.getEmployeeCountForCompanyAndChildren(newCompany, companies);
        Assertions.assertEquals(0, result, "Company not in the list should return employee count of zero.");
    }

    @Test
    void whenCompanyHasNoChildren_thenEmployeeCountIsExact() {
        long result = underTest.getEmployeeCountForCompanyAndChildren(bookkeeping, companies);
        Assertions.assertEquals(2, result, "Employee count for company without children should be exact.");
    }

    @Test
    void whenCompanyHasVeryLargeEmployeeCount_thenHandleOverflowCorrectly() {
        Company largeCompany = new Company(null, Long.MAX_VALUE-1);
        Company childCompany = new Company(largeCompany, 1);
        List<Company> largeCompanies = List.of(largeCompany, childCompany);

        long result = underTest.getEmployeeCountForCompanyAndChildren(largeCompany, largeCompanies);
        Assertions.assertEquals(Long.MAX_VALUE, result, "Employee count should handle very large values correctly.");
    }

    @Test
    void whenCompanyHasCyclicDependency_thenHandleGracefully() {
        // Створюємо циклічну залежність
        Company cyclic = new Company(it, 3);
        it.setParent(cyclic);

        Company result = underTest.getTopLevelParent(cyclic);
        Assertions.assertNotNull(result, "Top level parent should be found even in cyclic case.");
    }


    @Test
    void whenCompanyHasOverflowEmployeeCount_thenHandleOverflowCorrectly() {
        Company largeCompany = new Company(null, Long.MAX_VALUE);
        Company childCompany = new Company(largeCompany, 1);
        List<Company> largeCompanies = List.of(largeCompany, childCompany);

        // Тестуємо, що викликається ArithmeticException при переповненні
        Assertions.assertThrows(ArithmeticException.class, () -> {
            long result = underTest.getEmployeeCountForCompanyAndChildren(largeCompany, largeCompanies);
        }, "Employee count should handle very large values correctly.");
    }



    @Test
    void whenCompanyHasMultipleGenerations_thenEmployeeCountIsSummedCorrectly() {
        long result = underTest.getEmployeeCountForCompanyAndChildren(main, companies);
        Assertions.assertEquals(14, result, "Employee count should sum up all generations correctly.");
    }

    @Test
    void whenCompanyHasOnlyGrandchildren_thenEmployeeCountIncludesThem() {
        long result = underTest.getEmployeeCountForCompanyAndChildren(hr, companies);
        Assertions.assertEquals(2, result, "Employee count should include grandchildren correctly.");
    }

    @Test
    void whenOnlyOneCompanyInList_thenCountIsItsOwnEmployees() {
        long result = underTest.getEmployeeCountForCompanyAndChildren(main, List.of(main));
        Assertions.assertEquals(3, result, "Single company in list should return its own employee count.");
    }

    @Test
    void whenEmptyCompanyList_thenReturnZeroEmployees() {
        long result = underTest.getEmployeeCountForCompanyAndChildren(main, List.of());
        Assertions.assertEquals(0, result, "Empty company list should return zero employee count.");
    }

    @Test
    void whenCompanyHasCyclicHierarchy_thenEmployeeCountHandlesIt() {
        // Створюємо циклічну ієрархію
        Company cyclic = new Company(it, 3);
        it.setParent(cyclic);

        List<Company> cyclicCompanies = List.of(it, cyclic);

        long result = underTest.getEmployeeCountForCompanyAndChildren(cyclic, cyclicCompanies);
        Assertions.assertEquals(8, result, "Cyclic hierarchy should handle employee count correctly.");
    }

    @Test
    void whenCompanyHasParentOutsideOfList_thenOnlyChildrenInListAreCounted() {
        Company externalParent = new Company(null, 50);
        Company internalChild = new Company(externalParent, 10);

        long result = underTest.getEmployeeCountForCompanyAndChildren(internalChild, List.of(internalChild));
        Assertions.assertEquals(10, result, "Only employees of the company in the list should be counted.");
    }

    @Test
    void whenMultipleCompaniesAreSame_thenCountThemOnce() {
        Company duplicate = new Company(main, 3);

        long result = underTest.getEmployeeCountForCompanyAndChildren(main, List.of(main, duplicate));
        Assertions.assertEquals(6, result, "Duplicate companies should only be counted once.");
    }

    @Test
    void whenDeepHierarchyExists_thenEmployeeCountIsCorrect() {
        Company deepChild = new Company(subHr, 5);
        List<Company> deepCompanies = List.of(main, bookkeeping, hr, it, subHr, subIt, deepChild);

        long result = underTest.getEmployeeCountForCompanyAndChildren(main, deepCompanies);
        Assertions.assertEquals(19, result, "Employee count for deep hierarchy should be correct.");
    }
}
