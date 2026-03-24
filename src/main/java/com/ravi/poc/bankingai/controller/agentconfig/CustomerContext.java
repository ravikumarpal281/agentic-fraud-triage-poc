package com.ravi.poc.bankingai.controller.agentconfig;

public class CustomerContext {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setCustomerId(String customerId) {
        CONTEXT.set(customerId);
    }

    public static String getCustomerId() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
