package com.nepxion.discovery.plugin.strategy.starter.agent.transformer;

import com.nepxion.discovery.plugin.strategy.starter.agent.match.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 *
 * @author zifeihan
 * @version 1.0
 */
public class TransformTemplate {
    private final Map<ClassMatcher, TransformCallback> register = new HashMap<>();
    private final Map<String, ClassMatcher> classMatcherFinder = new HashMap<>();
    private final Map<MatcherOperator, ClassMatcher> interfaceMatcherFinder = new HashMap<>();

    public void transform(ClassMatcher classMatcher, TransformCallback transformCallback) {
        register.put(classMatcher, transformCallback);
        if (classMatcher instanceof ClassNameMatcher) {
            ClassNameMatcher classNameMatcher = (ClassNameMatcher) classMatcher;
            classMatcherFinder.put(classNameMatcher.getClassName(), classMatcher);
        } else if (classMatcher instanceof InterfaceMatcher) {
            InterfaceMatcher interfaceMatcher = (InterfaceMatcher) classMatcher;
            MatcherOperator matcherOperator = new MatcherOperator(
                    new PackageOperator(interfaceMatcher.getBasePackageName()), interfaceMatcher);
            interfaceMatcherFinder.put(matcherOperator, classMatcher);
        }
    }

    public ClassMatcher findClassMatcher(String className) {
        return classMatcherFinder.get(className);
    }

    public ClassMatcher findInterfaceMatcher(String className, ClassLoader loader, byte[] classfileBuffer) {
        for (Map.Entry<MatcherOperator, ClassMatcher> entry : interfaceMatcherFinder.entrySet()) {
            MatcherOperator matcherOperator = entry.getKey();
            boolean match = matcherOperator.match(className, loader, classfileBuffer);
            if (match) {
                return entry.getValue();
            }
        }
        return classMatcherFinder.get(className);
    }

    public TransformCallback findTransformCallback(ClassMatcher classMatcher) {
        return register.get(classMatcher);
    }
}
