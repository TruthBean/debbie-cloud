package com.truthbean.debbie.spring;

import com.truthbean.Console;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.event.AbstractDebbieEvent;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.proxy.BeanProxyType;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/02/03 09:18.
 */
public class SpringApplicationFactory implements ApplicationFactory, ApplicationContext, GlobalBeanFactory, BeanInfoManager {

    private Class<?> applicationClass;
    private ApplicationArgs applicationArgs;

    private DefaultListableBeanFactory beanFactory;
    private AnnotationConfigApplicationContext applicationContext;

    private final ResourceResolver resourceResolver;
    private final Set<BeanLifecycle> beanLifecycles = new HashSet<>();

    public SpringApplicationFactory() {
        this.resourceResolver = new ResourceResolver();
        Console.info("debbie is replaced by spring!");
    }

    @Override
    public ApplicationFactory preInit(String... args) {
        applicationArgs = new ApplicationArgs(args);
        return this;
    }

    @Override
    public ApplicationFactory preInit(Class<?> applicationClass, String... args) {
        this.applicationClass = applicationClass;
        this.applicationArgs = new ApplicationArgs(args);
        return this;
    }

    @Override
    public ApplicationFactory registerModuleStarter(DebbieModuleStarter moduleStarter) {
        return this;
    }

    @Override
    public ApplicationFactory init(Class<?>... beanClasses) {
        this.beanFactory = new DefaultListableBeanFactory();
        this.applicationContext = new AnnotationConfigApplicationContext(beanFactory);
        if (beanClasses != null && beanClasses.length > 0) {
            for (Class<?> beanClass : beanClasses) {
                this.applicationContext.registerBean(beanClass);
            }
        }
        if (applicationClass != null) {
            this.applicationContext.register(applicationClass);
            this.applicationContext.scan(applicationClass.getPackageName());
        }
        return this;
    }

    @Override
    public ApplicationFactory init(ClassLoader classLoader, Class<?>... beanClasses) {
        this.beanFactory = new DefaultListableBeanFactory();
        this.applicationContext = new AnnotationConfigApplicationContext(beanFactory);
        if (beanClasses != null && beanClasses.length > 0) {
            for (Class<?> beanClass : beanClasses) {
                this.applicationContext.registerBean(beanClass);
            }
        }
        if (classLoader != null) {
            this.applicationContext.setClassLoader(classLoader);
        }
        if (applicationClass != null) {
            this.applicationContext.register(applicationClass);
            this.applicationContext.scan(applicationClass.getPackageName());
        }
        return this;
    }

    @Override
    public void registerBeanRegister(BeanRegister beanRegister) {
    }

    @Override
    public void registerClass(Class<?> beanClass) {
        this.applicationContext.registerBean(beanClass);
    }

    @Override
    public <Bean> void register(Class<Bean> clazz) {
        this.applicationContext.registerBean(clazz);
    }

    @Override
    public ApplicationFactory config() {
        return this;
    }

    @Override
    public <T> ApplicationFactory config(T application) {
        this.applicationContext.registerBean(application.getClass(), (Supplier<T>) () -> application);
        return this;
    }

    @Override
    public ApplicationFactory config(BeanScanConfiguration configuration) {
        this.applicationContext.scan(configuration.getScanBasePackage());
        Set<Class<?>> classes = configuration.getScannedClasses();
        for (Class<?> clazz : classes) {
            this.applicationContext.registerBean(clazz);
        }
        return this;
    }

    @Override
    public ApplicationFactory create() {
        return this;
    }

    @Override
    public boolean registerBeanInfo(BeanInfo<?> beanInfo) {
        this.applicationContext.registerBean(beanInfo.getServiceName(), beanInfo.getBeanClass());
        return true;
    }

    @Override
    public ApplicationFactory register(BeanInfo<?> beanInfo) {
        this.applicationContext.registerBean(beanInfo.getServiceName(), beanInfo.getBeanClass());
        return this;
    }

    @Override
    public ApplicationFactory register(BeanFactory<?> beanFactory) {
        this.applicationContext.registerBean(beanFactory.getServiceName(), beanFactory.getBeanClass(), beanFactory.factoryBean(this));
        return this;
    }

    @Override
    public ApplicationFactory register(Collection<BeanInfo<?>> beanInfos) {
        for (BeanInfo<?> beanInfo : beanInfos) {
            this.applicationContext.registerBean(beanInfo.getServiceName(), beanInfo.getBeanClass());
        }
        return this;
    }

    @Override
    public ApplicationFactory register(BeanLifecycle beanLifecycle) {
        beanLifecycles.add(beanLifecycle);
        return this;
    }

    @Override
    public ApplicationFactory register(BeanRegister beanRegister) {
        return this;
    }

    @Override
    public ApplicationFactory postCreate() {
        return this;
    }

    @Override
    public ApplicationFactory build() {
        this.applicationContext.refresh();
        return this;
    }

    @Override
    public <T> T factoryWithoutProxy(Class<T> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public <T> T factory(String serviceName, Class<T> type, boolean required) {
        return applicationContext.getBean(serviceName, type);
    }

    @Override
    public <T> T factoryIfPresent(Class<T> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public <T> T factoryIfPresentOrElse(Class<T> type, Supplier<T> otherFactory) {
        Map<Resource, T> cache = applicationContext.getResourceCache(type);
        if (cache.isEmpty() && otherFactory != null) {
            return otherFactory.get();
        }
        return applicationContext.getBean(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> factoryIfPresent(String beanName) {
        if (applicationContext.containsBean(beanName)) {
            return Optional.of((T) applicationContext.getBean(beanName));
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> supply(String beanName) {
        return () -> (T) applicationContext.getBean(beanName);
    }

    @Override
    public <T> Supplier<T> supply(Class<T> type) {
        return () -> applicationContext.getBean(type);
    }

    @Override
    public <T> void factoryByRawBean(T rawBean) {
        applicationContext.registerBean(rawBean.getClass(), (Supplier<T>) () -> rawBean);
    }

    @Override
    public <T> T factoryByNoBean(Class<T> noBeanType) {
        applicationContext.registerBean(noBeanType);
        return applicationContext.getBean(noBeanType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Bean> Set<Bean> getBeanList(Class<Bean> superType) {
        String[] names = applicationContext.getBeanNamesForType(superType);
        Set<Bean> set = new HashSet<>();
        for (String name : names) {
            Bean bean = (Bean) applicationContext.getBean(name);
            set.add(bean);
        }
        return set;
    }

    @Override
    public <T> boolean containsBean(Class<T> beanType) {
        String[] names = applicationContext.getBeanNamesForType(beanType);
        return names.length > 0;
    }

    @Override
    public boolean containsBean(String beanName) {
        return applicationContext.containsBean(beanName);
    }

    @Override
    public void printGraalvmConfig(ApplicationContext context) {
        List<BeanInfo<? extends BeanScanConfiguration>> list = this.getBeanInfoList(BeanScanConfiguration.class, true);
        Set<String> packages = new HashSet<>();
        for (BeanInfo<? extends BeanScanConfiguration> info : list) {
            Set<String> names = info.getBeanNames();
            for (String name : names) {
                if (info instanceof BeanFactory<?> beanFactory) {
                    BeanScanConfiguration bean = (BeanScanConfiguration) beanFactory.factoryNamedBean(name, context);
                    packages.addAll(bean.getScanBasePackages());
                }
            }
        }
        if (packages.isEmpty() && applicationClass != null) {
            packages.add(applicationClass.getPackageName());
        }
        packages.add("org.springframework");
        StringBuilder scanClasses = new StringBuilder("debbie.core.scan.classes=");
        StringBuilder reflectConfig = new StringBuilder("[\n");
        StringBuilder proxyConfig = new StringBuilder("[\n  [],\n");
        Set<BeanInfo> set = this.getAllBeanInfo();
        for (BeanInfo info : set) {
            Class beanClass = info.getBeanClass();
            String name = beanClass.getName();
            if (beanClass.isInterface()) {
                proxyConfig.append("  [\"").append(name).append("\"],\n");
            }
            for (String s : packages) {
                if (name.startsWith(s + ".")) {
                    scanClasses.append(name).append(",");
                }
                reflectConfig.append("  {\n    \"name\": \"")
                        .append(name)
                        .append("\",\n    \"methods\": [\n");
                Constructor[] constructors = beanClass.getConstructors();
                if (constructors.length > 0) {
                    for (Constructor constructor : constructors) {
                        reflectConfig.append("      {\"name\":\"<init>\", \"parameterTypes\": [");
                        Class<?>[] types = constructor.getParameterTypes();
                        for (Class<?> type : types) {
                            reflectConfig.append("\"").append(type.getName()).append("\", ");
                        }
                        reflectConfig.append("] },\n");
                    }
                }
                if (info instanceof DebbieReflectionBeanFactory beanFactory) {
                    Map<Method, Set<Annotation>> map = beanFactory.getMethodWithAnnotations();
                    Set<Method> methods = map.keySet();
                    for (Method method : methods) {
                        reflectConfig.append("      {\"name\":\"").append(method.getName()).append("\", \"parameterTypes\": [");
                        Class<?>[] types = method.getParameterTypes();
                        for (Class<?> type : types) {
                            reflectConfig.append("\"").append(type.getName()).append("\", ");
                        }
                        reflectConfig.append("] },\n");
                    }
                }
                if (reflectConfig.lastIndexOf(",\n") == reflectConfig.length() - 2) {
                    reflectConfig.deleteCharAt(reflectConfig.length() - 1);
                    reflectConfig.deleteCharAt(reflectConfig.length() - 1);
                }
                reflectConfig.append("\n    ],\n")
                        .append("    \"allDeclaredConstructors\": true,\n")
                        .append("    \"allPublicConstructors\": true,\n")
                        .append("    \"allDeclaredMethods\": true,\n")
                        .append("    \"allPublicMethods\": true,\n")
                        .append("    \"allDeclaredFields\": true,\n")
                        .append("    \"allPublicFields\": true\n")
                        .append("  },\n");
            }
        }
        if (reflectConfig.lastIndexOf(",\n") == reflectConfig.length() - 2) {
            reflectConfig.deleteCharAt(reflectConfig.length() - 1);
            reflectConfig.deleteCharAt(reflectConfig.length() - 1);
        }
        reflectConfig.append("\n]");

        if (proxyConfig.lastIndexOf(",\n") == proxyConfig.length() - 2) {
            proxyConfig.deleteCharAt(proxyConfig.length() - 1);
            proxyConfig.deleteCharAt(proxyConfig.length() - 1);
        }
        proxyConfig.append("\n]");
        Console.info("scanned classes: \n" + scanClasses);
        Console.info("graalvm reflect config: \n" + reflectConfig);
        Console.info("graalvm proxy config: \n" + proxyConfig);
    }

    @Override
    public void destroy(BeanInfo<?> beanInfo) {
        beanFactory.clearMetadataCache();
        applicationContext.clearResourceCaches();
    }

    @Override
    public void reset(ApplicationContext applicationContext) {
        beanFactory.clearMetadataCache();
        this.applicationContext.clearResourceCaches();
    }

    @Override
    public ApplicationArgs getApplicationArgs() {
        return applicationArgs;
    }

    @Override
    public ClassLoader getClassLoader() {
        return applicationContext.getClassLoader();
    }

    @Override
    public EnvironmentContent getEnvContent() {
        return new SpringEnvContent(applicationContext.getEnvironment());
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    @Override
    public BeanInfoManager getBeanInfoManager() {
        return this;
    }

    @Override
    public GlobalBeanFactory getGlobalBeanFactory() {
        return this;
    }

    @Override
    public Set<BeanLifecycle> getBeanLifecycle() {
        return beanLifecycles;
    }

    @Override
    public <T extends I, I> void registerSingleBean(Class<I> beanClass, T bean, String... names) {
        applicationContext.registerBean(beanClass, () -> bean);
    }

    @Override
    public void registerBeanLifecycle(BeanLifecycle beanLifecycle) {
        beanLifecycles.add(beanLifecycle);
    }

    @Override
    public Set<BeanLifecycle> getBeanLifecycles() {
        return beanLifecycles;
    }

    @Override
    public <Bean> void refresh(BeanInfo<Bean> beanInfo) {
        applicationContext.refresh();
    }

    @Override
    public void autoCreateSingletonBeans(ApplicationContext applicationContext) {
        this.applicationContext.refresh();
    }

    @Override
    public Set<BeanInfo> getLazyCreateBean() {
        return new HashSet<>();
    }

    @Override
    public <A extends Annotation> void registerInjectType(Class<A> injectType) {

    }

    @Override
    public void registerInjectType(Set<Class<? extends Annotation>> injectTypes) {

    }

    @Override
    public <A extends Annotation> void registerBeanAnnotation(Class<A> annotationType, BeanComponentParser parser) {

    }

    @Override
    public Set<Class<? extends Annotation>> getBeanAnnotations() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getClassAnnotation() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getMethodAnnotation() {
        return null;
    }

    @Override
    public boolean support(Class<?> beanClass) {
        return true;
    }

    @Override
    public Set<Class<? extends Annotation>> getInjectTypes() {
        return null;
    }

    @Override
    public Class<? extends Annotation> getInjectType() {
        return null;
    }

    @Override
    public boolean hasInjectType(AnnotatedElement annotatedElement, boolean another) {
        return false;
    }

    @Override
    public boolean injectedRequired(Annotation annotation, boolean another) {
        return false;
    }

    @Override
    public Boolean injectBeanRequiredIfPresent(AnnotatedElement annotatedElement, boolean another) {
        return null;
    }

    @Override
    public boolean containInjectType(Class<? extends Annotation> annotation) {
        return false;
    }

    @Override
    public void addIgnoreInterface(Class<?> ignoreInterface) {
        beanFactory.ignoreDependencyInterface(ignoreInterface);
    }

    @Override
    public Set<Class<?>> getIgnoreInterface() {
        return null;
    }

    @Override
    public void addIgnoreAnnotation(Class<? extends Annotation> annotation) {
    }

    @Override
    public Set<Class<? extends Annotation>> getIgnoredAnnotations() {
        return null;
    }

    @Override
    public <T extends Annotation> Set<BeanInfo<?>> getAnnotatedClass(Class<T> annotationClass) {
        return null;
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Set<BeanInfo> getAllBeanInfo() {
        String[] names = applicationContext.getBeanDefinitionNames();
        Set<BeanInfo> set = new HashSet<>();
        for (String name : names) {
            SpringBeanFactory beanFactory = new SpringBeanFactory(applicationContext, name);
            set.add(beanFactory);
        }
        return set;
    }

    @Override
    public <Bean> List<BeanInfo<? extends Bean>> getBeanInfoList(Class<Bean> type, boolean require) {
        DefaultListableBeanFactory factory = applicationContext.getDefaultListableBeanFactory();
        Map<String, Bean> map = factory.getBeansOfType(type);
        List<BeanInfo<? extends Bean>> list = new ArrayList<>();
        map.forEach((name, bean) -> {
            if (applicationContext.isSingleton(name)) {
                SimpleBeanFactory<Bean, Bean> beanFactory = new SimpleBeanFactory<>(bean, type, name);
                list.add(beanFactory);
            } else {
                SimpleBeanFactory<Bean, Bean> beanFactory = new SimpleBeanFactory<>(bean, type, BeanType.NO_LIMIT, BeanProxyType.JDK, name);
                list.add(beanFactory);
            }
        });
        return list;
    }

    @Override
    public <Bean> BeanInfo<Bean> getBeanInfo(String serviceName, Class<Bean> type, boolean require) {
        return null;
    }

    @Override
    public <Bean> BeanFactory<Bean> getBeanFactory(String serviceName, Class<Bean> type, boolean require) {
        return null;
    }

    @Override
    public <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException) {
        return null;
    }

    @Override
    public <T> BeanFactory<T> getBeanFactory(String serviceName, Class<T> type, boolean require, boolean throwException) {
        return null;
    }

    @Override
    public Set<BeanInfo<?>> getAnnotatedBeans() {
        return null;
    }

    @Override
    public Set<BeanInfo<?>> getAnnotatedMethodsBean(Class<? extends Annotation> methodAnnotation) {
        return null;
    }

    @Override
    public <T> Set<BeanInfo> getBeansByInterface(Class interfaceType) {
        return null;
    }

    @Override
    public Set<BeanInfo<?>> getBeanByAbstractSuper(Class<?> abstractType) {
        return null;
    }

    @Override
    public Collection<BeanInfo> getRegisteredBeans() {
        return null;
    }

    @Override
    public Set<Method> getBeanMethods(Class<?> beanClass) {
        return null;
    }

    @Override
    public boolean isBeanRegistered(Class<?> beanClass) {
        return true;
    }

    @Override
    public <O, T> T transform(O origin, Class<T> target) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T factory(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    @Override
    public <T> T factory(Class<T> beanType) {
        return applicationContext.getBean(beanType);
    }

    @Override
    public <E extends AbstractDebbieEvent> void publishEvent(E event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this;
    }

    @Override
    public DebbieApplication factory() {
        return new SpringDebbieApplication(this);
    }

    @Override
    public void release() {
        applicationContext.clearResourceCaches();
    }

    @Override
    public void release(String... args) {
        applicationContext.clearResourceCaches();
        applicationContext.close();
    }

    @Override
    public boolean isExiting() {
        return false;
    }
}
