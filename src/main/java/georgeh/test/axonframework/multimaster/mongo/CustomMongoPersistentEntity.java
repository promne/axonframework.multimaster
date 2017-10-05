package georgeh.test.axonframework.multimaster.mongo;

import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.util.TypeInformation;

import georgeh.test.axonframework.multimaster.util.IdentityNameFactory;

/**
 * Entity wrapper which requires entity classes to be annotated by {@link DocumentNameParts}
 * to spot changes between different versions. 
 *
 * @param <T>
 */
public class CustomMongoPersistentEntity<T> extends BasicMongoPersistentEntity<T> {

    private final String collection;
    
    public CustomMongoPersistentEntity(final TypeInformation<T> typeInformation) {
        super(typeInformation);

        DocumentNameParts document = this.findAnnotation(DocumentNameParts.class);
        
        collection = IdentityNameFactory.getCollectionName(document.documentType(), document.handlerType());
    }

    @Override
    public String getCollection() {
        return collection;
    }
    
}
