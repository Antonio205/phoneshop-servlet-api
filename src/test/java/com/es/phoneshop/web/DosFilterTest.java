package com.es.phoneshop.web;

import com.es.phoneshop.security.DosProtectionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DosFilterTest {

    private DosFilter dosFilter;

    @Mock
    private DosProtectionService dosProtectionService;

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private ServletRequest servletRequest;

    @Mock
    private ServletResponse servletResponse;

    @Mock
    private FilterChain filterChain;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        dosFilter = new DosFilter();
    }

    @Test
    public void givenIp_whenDoFilter_thenInvokeFilterChain() throws Exception {
        String remoteAddr = "127.0.0.2";
        when(dosProtectionService.isAllowed(remoteAddr)).thenReturn(true);
        when(servletRequest.getRemoteAddr()).thenReturn(remoteAddr);

        dosFilter.init(filterConfig);
        dosFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(filterChain).doFilter(servletRequest, servletResponse);
    }
}
