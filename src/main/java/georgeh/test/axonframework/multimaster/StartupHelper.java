package georgeh.test.axonframework.multimaster;

import georgeh.test.axonframework.multimaster.cdi.ApplicationStartup;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.axonframework.config.Configuration;

@ApplicationScoped
@ApplicationStartup
public class StartupHelper {

    @Inject
    private Configuration configuration;

    @PostConstruct
    public void startup() {
        configuration.toString();
    }
}
