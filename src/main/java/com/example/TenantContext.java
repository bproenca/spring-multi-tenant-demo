package com.example;

public class TenantContext {
    private static InheritableThreadLocal<Object> currentTenant = new InheritableThreadLocal<>();

    public static void setCurrentTenant(Object tenant) {
        currentTenant.set(tenant);
    }

    public static Object getCurrentTenant() {
        return currentTenant.get();
    }
}
