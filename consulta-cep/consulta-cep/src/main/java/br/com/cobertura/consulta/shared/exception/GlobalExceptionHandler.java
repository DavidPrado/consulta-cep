package br.com.cobertura.consulta.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CepDuplicadoException.class)
    public ProblemDetail handleCepDuplicado(CepDuplicadoException ex, HttpServletRequest request) {
        log.warn("CEP duplicado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("CEP duplicado");
        problem.setType(URI.create("https://api.consulta-cep/errors/cep-duplicado"));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(CepInvalidoException.class)
    public ProblemDetail handleCepInvalido(CepInvalidoException ex, HttpServletRequest request) {
        log.warn("CEP inválido: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("CEP inválido");
        problem.setType(URI.create("https://api.consulta-cep/errors/cep-invalido"));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhes);
        problem.setTitle("Erro de validação");
        problem.setType(URI.create("https://api.consulta-cep/errors/validacao"));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenerico(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Contate o suporte."
        );
        problem.setTitle("Erro interno");
        problem.setType(URI.create("https://api.consulta-cep/errors/interno"));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(UfInvalidaException.class)
    public ProblemDetail handleUfInvalida(UfInvalidaException ex, HttpServletRequest request) {
        log.warn("UF inválida: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("UF inválida");
        problem.setType(URI.create("https://api.consulta-cep/errors/uf-invalida"));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ProblemDetail handleCepNaoEncontrado(CepNaoEncontradoException ex, HttpServletRequest request) {
        log.warn("CEP não encontrado: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("CEP não encontrado");
        problem.setType(URI.create("https://api.consulta-cep/errors/cep-nao-encontrado"));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    @ExceptionHandler(ExternalApiException.class)
    public ProblemDetail handleExternalApi(ExternalApiException ex, HttpServletRequest request) {
        log.error("Erro na API externa: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                "Não foi possível consultar o serviço externo de CEP. Tente novamente mais tarde."
        );
        problem.setTitle("Erro na API externa");
        problem.setType(URI.create("https://api.consulta-cep/errors/external-api"));
        problem.setProperty("timestamp", LocalDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}
