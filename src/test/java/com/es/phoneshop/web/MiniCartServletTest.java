package com.es.phoneshop.web;

import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MiniCartServletTest {

    private MiniCartServlet miniCartServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext servletContext;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private HttpSession session;

    @Before
    public void setup() throws ServletException {
        MockitoAnnotations.initMocks(this);
        miniCartServlet = new MiniCartServlet();
        miniCartServlet.init(servletConfig);

        when(request.getServletContext()).thenReturn(servletContext);
        when(request.getRequestDispatcher("/WEB-INF/pages/minicart.jsp")).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void givenValidCart_whenDoGet_thenCorrectIncludeAndCart() throws ServletException, IOException {
        miniCartServlet.doGet(request, response);

        verify(requestDispatcher).include(request, response);
    }

}
