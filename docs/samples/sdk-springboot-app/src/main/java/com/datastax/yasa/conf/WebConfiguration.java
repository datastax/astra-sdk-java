package com.datastax.yasa.conf;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.datastax.astra.sdk.AstraClient;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	@Autowired
	private AstraClient astraClient;
	
	@Bean
    public ClassLoaderTemplateResolver templateResolver() {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("views/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }
	
	@Bean
    public SpringTemplateEngine templateEngine() {
        var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }
	
	@Bean
    public ViewResolver viewResolver() {
        var viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }
	
	/** {@inheritDoc} */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }
    
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        return messageSource;
    }
    
	/** {@inheritDoc} */
	@Override
	public void addInterceptors(InterceptorRegistry interceptorRegistry) {
		interceptorRegistry.addInterceptor(localeChangeInterceptor());
		interceptorRegistry.addInterceptor(exposeNamespaceInterceptor());
	}

	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		localeResolver.setDefaultLocale(Locale.ENGLISH);
		return localeResolver;
	}

	/**
	 * If we put the ?lang in the URL we will get our locale changed.
	 *
	 * @return
	 *         current locale change
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	@Bean
	public HandlerInterceptor exposeNamespaceInterceptor() {
		return new HandlerInterceptor() {
		    
		    /** {@inheritDoc} */
		    public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler)
		    throws Exception { 
		        return true;
		    }
            
			/** {@inheritDoc} */
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) 
			throws Exception {
			        astraClient.cqlSession()
			                   .getKeyspace()
			                   .ifPresent(namespace -> {
                					if (modelAndView != null) {
                						modelAndView.addObject("namespace", namespace.asInternal());
                					}
			                   });
			}
			
			/** {@inheritDoc} */
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
			    
			}
		};
	}

	@Bean
    public SimpleMappingExceptionResolver exposeExtraErrorInfoExceptionResolver() {
		return new SimpleMappingExceptionResolver() {
		   	 @Override
			  protected ModelAndView doResolveException(HttpServletRequest req,
			        HttpServletResponse resp, Object handler, Exception ex) {

			    req.setAttribute("msgType", "error");
			    req.setAttribute("msgInfo", ex.getMessage());
			    return super.doResolveException(req, resp, handler, ex);
			  }
		};
    }
}
