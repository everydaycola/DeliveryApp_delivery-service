package be.kdg.sa.deliveryservice;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.jmolecules.archunit.JMoleculesDddRules;

@AnalyzeClasses(packages = "be.kdg.delivery-service")
class DddArchTest {
    @ArchTest
    void whenCheckingAllClasses_thenCodeFollowsAllDddPrinciples(final JavaClasses classes) {
        JMoleculesDddRules.all().check(classes);
    }
}
