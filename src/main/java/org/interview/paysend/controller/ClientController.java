package org.interview.paysend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.interview.paysend.common.ResponseRegistry;
import org.interview.paysend.dto.BalanceResponse;
import org.interview.paysend.dto.Credentials;
import org.interview.paysend.dto.Extra;
import org.interview.paysend.dto.Request;
import org.interview.paysend.service.BalanceService;
import org.interview.paysend.service.BalanceService.BalanceRequestResult;
import org.interview.paysend.service.RegistrationService;
import org.interview.paysend.service.RegistrationService.RegistrationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.interview.paysend.common.ResponseCodes.*;

@RestController
@RequestMapping("/client")
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private static final String CREATE_USER_REQUEST_TYPE = "CREATE-AGT";
    private static final String GET_BALANCE_REQUEST_TYPE = "GET-BALANCE";

    private final RegistrationService registrationService;
    private final BalanceService balanceService;

    private final XmlMapper xmlMapper;
    private final ResponseRegistry responseRegistry;

    @Autowired
    public ClientController(RegistrationService registrationService,
                            BalanceService balanceService,
                            XmlMapper xmlMapper,
                            ResponseRegistry responseRegistry) {
        this.registrationService = registrationService;
        this.balanceService = balanceService;
        this.xmlMapper = xmlMapper;
        this.responseRegistry = responseRegistry;
    }


    @PostMapping(produces = {MediaType.APPLICATION_XML_VALUE})
    public String endpoint(@RequestBody String body) {
        try {
            Request baseRequest = xmlMapper.readValue(body, Request.class);
            Optional<Credentials> credentials = getCredentials(baseRequest);
            if (credentials.isEmpty()) {
                return responseRegistry.getPrebuiltResponse(VALIDATION_FAILED);
            }
            switch (baseRequest.getRequestType()) {
                case CREATE_USER_REQUEST_TYPE: {
                    return handleRegisterRequest(credentials.get());
                }
                case GET_BALANCE_REQUEST_TYPE:
                    return handleGetBalanceRequest(credentials.get());
                default:
                    return responseRegistry.getPrebuiltResponse(METHOD_NOT_ALLOWED);
            }
        } catch (Exception e) {
            logger.error("Error when handling request {}", body, e);
            return responseRegistry.getPrebuiltResponse(INTERNAL_ERROR);
        }
    }

    private String handleGetBalanceRequest(Credentials credentials) throws JsonProcessingException {
        BalanceRequestResult balanceRequestResult = balanceService.getBalance(credentials);
        switch (balanceRequestResult.result) {
            case NoSuchAccount:
                return responseRegistry.getPrebuiltResponse(NO_SUCH_USER);
            case WrongPassword:
                return responseRegistry.getPrebuiltResponse(WRONG_PASSWORD);
            case Success:
                return xmlMapper.writeValueAsString(new BalanceResponse(balanceRequestResult.balance));
            default:
                return responseRegistry.getPrebuiltResponse(INTERNAL_ERROR);
        }
    }

    private String handleRegisterRequest(Credentials credentials) {
        RegistrationResult registrationResult = registrationService.registerAccount(credentials);
        switch (registrationResult) {
            case Success:
                return responseRegistry.getPrebuiltResponse(SUCCESS);
            case UserExists:
                return responseRegistry.getPrebuiltResponse(USER_EXISTS);
            default:
                return responseRegistry.getPrebuiltResponse(INTERNAL_ERROR);
        }
    }

    private Optional<Credentials> getCredentials(Request request) {
        String login = null;
        String password = null;
        for (Extra extra : request.getExtra()) {
            if (extra.getName().equals("login")) {
                login = extra.getValue();
            }
            if (extra.getName().equals("password")) {
                password = extra.getValue();
            }
        }
        if (login == null || password == null) {
            return Optional.empty();
        }
        return Optional.of(new Credentials(login, password));
    }
}
