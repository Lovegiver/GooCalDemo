import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class UniMultiTest {

    @Test
    void handleListInUniTest() {

        var list = List.of("A", "B", "C", "D", "E", "F");
        var uni = Uni.createFrom().item(list);
        uni.subscribe().with(
                strings -> strings.forEach(System.out::println),
                error -> System.out.println("Error: " + error)
        );

    }
}
