package com.dtsx.astra.sdk.ann;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = "sw.*")
public @interface EnableIfOpenAI {
}


