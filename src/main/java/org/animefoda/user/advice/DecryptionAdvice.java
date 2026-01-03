package org.animefoda.user.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.BadRequestException;
import org.animefoda.user.annotation.DecryptedBody;
import org.animefoda.user.grpc.DecryptGrpcClient;
import org.animefoda.user.request.EncryptedRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class DecryptionAdvice implements RequestBodyAdvice {
    private final DecryptGrpcClient decryptGrpcClient;
    private final ObjectMapper objectMapper;

    public DecryptionAdvice(DecryptGrpcClient decryptGrpcClient, ObjectMapper objectMapper) {
        this.decryptGrpcClient = decryptGrpcClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type type,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasParameterAnnotation(DecryptedBody.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        try {
            EncryptedRequest encryptedBody = objectMapper.readValue(inputMessage.getBody(), EncryptedRequest.class);

            // TODO: Adicionar validação de reCAPTCHA se necessário
            // if (encryptedBody.recaptchaToken() == null) {
            // throw new BadRequestException("Recaptcha token is missing",
            // "RECAPTCHA_TOKEN_MISSING");
            // }

            // Chama o auth-server via gRPC para desencriptar
            String decryptedJson = decryptGrpcClient.decrypt(encryptedBody.encryptedInfo());

            return new DecryptedInputMessage(decryptedJson, inputMessage.getHeaders());

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Decryption or deserialization failed", e.getMessage());
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
            Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        throw new BadRequestException("No body", "NO_BODY");
    }

    private static class DecryptedInputMessage implements HttpInputMessage {
        private final InputStream body;
        private final HttpHeaders headers;

        public DecryptedInputMessage(String body, HttpHeaders headers) {
            this.body = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            this.headers = headers;
        }

        @Override
        public InputStream getBody() {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}
