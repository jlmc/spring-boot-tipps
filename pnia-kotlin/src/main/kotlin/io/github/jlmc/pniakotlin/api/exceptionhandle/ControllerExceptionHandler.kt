package io.github.jlmc.pniakotlin.api.exceptionhandle

import io.github.jlmc.pniakotlin.domain.services.blacklist.ForbiddenInputValuesException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebInputException
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ControllerExceptionHandler {

    private val log = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handlerUnhatchedException(exception: Exception): ResponseEntity<*> {
        log.error("Attention: handler Unhatched Exception: {}", exception.message, exception)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Problem("123-500", exception.message))
    }

    @ExceptionHandler(RuntimeException::class)
    fun handlerRuntimeException(exception: RuntimeException): ResponseEntity<*> {
        log.error("Exception caught in handlerRuntimeException : {}", exception.message, exception)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Problem("500", exception.message))
    }

    @ExceptionHandler(
        ConstraintViolationException::class,
        ServerWebInputException::class
    )
    fun handlerValidationExceptions(exception: Exception): ResponseEntity<*> {
        log.error("Exception caught in handlerValidationExceptions : {}", exception.message, exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Problem("400", exception.message))
    }

    @ExceptionHandler(ForbiddenInputValuesException::class)
    fun handlerForbiddenInputValuesException(exception: ForbiddenInputValuesException): ResponseEntity<*> {
        log.error("Exception caught in handlerForbiddenInputValuesException : {}", exception.message, exception)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                Problem(
                    "400",
                    "Unaccepted input values " + exception.invalidValues.joinToString(", ", "[", "]")
                )
            )
    }
}
