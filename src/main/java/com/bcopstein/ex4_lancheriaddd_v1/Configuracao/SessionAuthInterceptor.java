package com.bcopstein.ex4_lancheriaddd_v1.Configuracao;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    private final AuthHelper authHelper;

    public SessionAuthInterceptor(AuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        System.out.println("Path: " + path);
        System.out.println("Method: " + method);

        // URLs públicas - não precisam autenticação
        if (isPublicUrl(path)) {
            System.out.println("URL PÚBLICA");
            return true;
        }

        // Verificar se está autenticado
        if (!authHelper.isAuthenticated(request)) {
            System.out.println("NÃO AUTENTICADO");
            response.setStatus(401);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"erro\": \"Usuário não autenticado\"}");
            return false;
        }

        System.out.println("AUTENTICADO ");
        return true;
    }

    private boolean isPublicUrl(String path) {
        return path.equals("/") ||
               path.startsWith("/cardapio") ||
               path.startsWith("/usuarios/registro") ||
               path.startsWith("/usuarios/login");
    }
}