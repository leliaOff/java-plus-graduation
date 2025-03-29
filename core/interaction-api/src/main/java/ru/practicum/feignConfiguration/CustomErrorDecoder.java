package ru.practicum.feignConfiguration;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.InternalServerErrorException;
import ru.practicum.exceptions.NotFoundException;

@Slf4j
@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        log.error("FEIGN: method {}, status {}, cause {}", methodKey, response.status(), response.reason());
        assert httpStatus != null;
        return switch (httpStatus) {
            case NOT_FOUND -> new NotFoundException("Not Found");
            case BAD_REQUEST -> new BadRequestException("Bad Request");
            case INTERNAL_SERVER_ERROR -> new InternalServerErrorException("Internal Server Error");
            case FORBIDDEN -> new ForbiddenException("Forbidden");
            case CONFLICT -> new ForbiddenException("Conflict");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}
