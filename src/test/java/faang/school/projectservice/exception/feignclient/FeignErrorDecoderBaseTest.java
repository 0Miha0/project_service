package faang.school.projectservice.exception.feignclient;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.ServiceCallException;
import faang.school.projectservice.exception.UnauthorizedException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FeignErrorDecoderBaseTest {

    private FeignErrorDecoderBase decoder;

    @BeforeEach
    void setUp() {
        decoder = new FeignErrorDecoderBase();
    }

    @Test
    void decodeBadRequestCodeTest() {
        String responseBody = "Validation error occurred";
        Response response = createResponse(404, responseBody);

        Exception exception = decoder.decode("methodKey", response);

        assertInstanceOf(DataValidationException.class, exception);
    }

    @Test
    void decodeUnauthorizedCodeTest() {
        String responseBody = "Unauthorized";
        Response response = createResponse(401, responseBody);

        Exception exception = decoder.decode("methodKey", response);

        assertInstanceOf(UnauthorizedException.class, exception);
    }

    @Test
    void decodeForbiddenCodeTest() {
        String responseBody = "Forbidden";
        Response response = createResponse(403, responseBody);

        Exception exception = decoder.decode("methodKey", response);

        assertInstanceOf(ForbiddenException.class, exception);
    }

    @Test
    void decodeUnexpectedCodeTest() {
        String responseBody = "Unexpected";
        Response response = createResponse(500, responseBody);

        Exception exception = decoder.decode("methodKey", response);

        assertInstanceOf(ServiceCallException.class, exception);
    }

    private Response createResponse(int status, String body) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost/api",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        Response.Builder responseBuilder = Response.builder()
                .request(request)
                .status(status);
        if (body != null) {
            responseBuilder.body(body, StandardCharsets.UTF_8);
        }
        return responseBuilder.build();
    }

}