package georgeh.test.axonframework.multimaster.util;

import java.util.Optional;

import georgeh.test.axonframework.multimaster.query.ProcessingGroupTarget;

public final class IdentityNameFactory {

    /**
     * Generates unique based on handler & the entity it handles.
     * 
     * @see ProcessingGroupTarget
     * 
     * @param handlerType
     * @return
     */
    public static String getTrackingTokenId(Class<?> handlerType) {
        // alternative - serial version uid, but that doesn't include method bodies - ObjectStreamClass.lookup(c).getSerialVersionUID()
        
        String handlerId = String.format("%s-%s", handlerType.getCanonicalName(), ClassHashUtils.getHash(handlerType));
        
        Optional<String> targetEntityId = Optional.ofNullable(handlerType.getAnnotation(ProcessingGroupTarget.class))
            .map(ProcessingGroupTarget::value).map(c -> String.format("%s-%s", c.getCanonicalName(), ClassHashUtils.getHash(c)));
       
        return targetEntityId.map(entityId -> handlerId+"-"+entityId).orElse(handlerId);        
    }
    
    /*
     * There is 120 bytes limit - https://docs.mongodb.com/manual/reference/limits/
     */
    public static String getCollectionName(Class<?> documentType, Class<?> handlerType) {
        return String.format("%s-%s-%s", documentType.getSimpleName(), ClassHashUtils.getHash(documentType), ClassHashUtils.getHash(handlerType));
    }
}
