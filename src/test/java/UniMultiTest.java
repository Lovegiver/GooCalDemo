import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class UniMultiTest {

    private static final Logger LOG = Logger.getLogger(UniMultiTest.class);

    @Test
    void handleListInUniTest() {

        var list = List.of("A", "B", "C", "D", "E", "F");
        var uni = Uni.createFrom().item(list);
        uni.subscribe().with(
                strings -> strings.forEach(LOG::debug),
                error -> LOG.debug("Error: " + error)
        );

    }
}
