package httpclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseService {
    private ObjectMapper objectMapper;

    public BaseService() {
        this.objectMapper = new ObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
