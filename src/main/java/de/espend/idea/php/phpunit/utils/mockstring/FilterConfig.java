package de.espend.idea.php.phpunit.utils.mockstring;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class FilterConfig {
    private final Map<String, Item> config;

    public FilterConfig() {
        config = new HashMap<>();
    }

    public FilterConfig(FilterConfig filterConfig) {
        config = new HashMap<>(filterConfig.config);
    }

    public FilterConfig add(Item filterConfigItem) {
        String hash = createHash(filterConfigItem.getClassName(), filterConfigItem.getMethodName());
        config.put(hash, filterConfigItem);
        return this;
    }

    @Nullable
    public Item getItem(String className, String methodName) {
        String hash = createHash(className, methodName);
        return config.get(hash);
    }

    @Nullable
    public Item getItem(String methodName) {
        String hash = createHash("", methodName);
        for (Map.Entry<String, Item> entry : config.entrySet()) {
            if (entry.getKey().endsWith(hash)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private static String createHash(String className, String methodName) {
        return className + "::" + methodName;
    }

    public record Item(String className, String methodName, int parameterNumber, Class<? extends Filter> filterClass) {
        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public int getParameterNumber() {
            return parameterNumber;
        }

        public Class<? extends Filter> getFilterClass() {
            return filterClass;
        }
    }
}
