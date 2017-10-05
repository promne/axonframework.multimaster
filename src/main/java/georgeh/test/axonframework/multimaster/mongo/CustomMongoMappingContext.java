package georgeh.test.axonframework.multimaster.mongo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.util.TypeInformation;

public class CustomMongoMappingContext extends MongoMappingContext {

    private ApplicationContext context;
    
    public CustomMongoMappingContext() {
        super();
    }

    @Override
    protected <T> BasicMongoPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
        CustomMongoPersistentEntity<T> entity = new CustomMongoPersistentEntity<T>(typeInformation);

        if (context != null) {
            entity.setApplicationContext(context);
        }

        return entity;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        super.setApplicationContext(applicationContext);
    }                        
    
}
