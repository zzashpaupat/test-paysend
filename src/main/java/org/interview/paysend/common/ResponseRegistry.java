package org.interview.paysend.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.interview.paysend.dto.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.interview.paysend.common.ResponseCodes.*;

@Component
public class ResponseRegistry {
    private final XmlMapper xmlMapper;

    private final Map<Integer, String> prebuiltResponses = new HashMap<>();

    @Autowired
    public ResponseRegistry(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    @PostConstruct
    private void init() throws JsonProcessingException {
        prebuiltResponses.put(SUCCESS, xmlMapper.writeValueAsString(new BaseResponse(ResponseCodes.SUCCESS)));
        prebuiltResponses.put(USER_EXISTS, xmlMapper.writeValueAsString(new BaseResponse(USER_EXISTS)));
        prebuiltResponses.put(INTERNAL_ERROR, xmlMapper.writeValueAsString(new BaseResponse(INTERNAL_ERROR)));
        prebuiltResponses.put(NO_SUCH_USER, xmlMapper.writeValueAsString(new BaseResponse(NO_SUCH_USER)));
        prebuiltResponses.put(WRONG_PASSWORD, xmlMapper.writeValueAsString(new BaseResponse(WRONG_PASSWORD)));
        prebuiltResponses.put(METHOD_NOT_ALLOWED, xmlMapper.writeValueAsString(new BaseResponse(METHOD_NOT_ALLOWED)));
        prebuiltResponses.put(VALIDATION_FAILED, xmlMapper.writeValueAsString(new BaseResponse(VALIDATION_FAILED)));
    }

    public String getPrebuiltResponse(int responseCode) {
        return prebuiltResponses.getOrDefault(responseCode, prebuiltResponses.get(INTERNAL_ERROR));
    }
}
