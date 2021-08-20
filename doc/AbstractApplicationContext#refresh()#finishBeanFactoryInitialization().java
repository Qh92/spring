//实例化和初始化
//AbstractApplicationContext#refresh
finishBeanFactoryInitialization(beanFactory);

//ConfigurableApplicationContext
String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

//AbstractApplicationContext#finishBeanFactoryInitialization
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
	// Initialize conversion service for this context.
	if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
			beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
		beanFactory.setConversionService(
				beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
	}

	// Register a default embedded value resolver if no bean post-processor
	// (such as a PropertyPlaceholderConfigurer bean) registered any before:
	// at this point, primarily for resolution in annotation attribute values.
	if (!beanFactory.hasEmbeddedValueResolver()) {
		beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
	}

	// Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
	String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
	for (String weaverAwareName : weaverAwareNames) {
		getBean(weaverAwareName);
	}

	// Stop using the temporary ClassLoader for type matching.
	beanFactory.setTempClassLoader(null);

	// Allow for caching all bean definition metadata, not expecting further changes.
	beanFactory.freezeConfiguration();

	// Instantiate all remaining (non-lazy-init) singletons.
	//实例化和初始化非懒加载的单例bean
	beanFactory.preInstantiateSingletons();
}

//DefaultListableBeanFactory#preInstantiateSingletons
public void preInstantiateSingletons() throws BeansException {
	if (logger.isTraceEnabled()) {
		logger.trace("Pre-instantiating singletons in " + this);
	}

	// Iterate over a copy to allow for init methods which in turn register new bean definitions.
	// While this may not be part of the regular factory bootstrap, it does otherwise work fine.
	//beanDefinitionNames在开始创建beanFactory时已经初始化了，将bean的id存入beanDefinitionNames
	List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

	// Trigger initialization of all non-lazy singleton beans...
	for (String beanName : beanNames) {
		//获取到beanDefinition对象，并将bean和beanDefinition添加进mergedBeanDefinitions
		RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
		//判断不是抽象类，不是懒加载，并且是单例bean
		if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
			//判断是否是FactoryBean
			if (isFactoryBean(beanName)) {
				Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
				if (bean instanceof FactoryBean) {
					final FactoryBean<?> factory = (FactoryBean<?>) bean;
					boolean isEagerInit;
					if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
						isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
										((SmartFactoryBean<?>) factory)::isEagerInit,
								getAccessControlContext());
					}
					else {
						isEagerInit = (factory instanceof SmartFactoryBean &&
								((SmartFactoryBean<?>) factory).isEagerInit());
					}
					if (isEagerInit) {
						getBean(beanName);
					}
				}
			}
			else {
				getBean(beanName);
			}
		}
	}

	// Trigger post-initialization callback for all applicable beans...
	for (String beanName : beanNames) {
		Object singletonInstance = getSingleton(beanName);
		if (singletonInstance instanceof SmartInitializingSingleton) {
			final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
					smartSingleton.afterSingletonsInstantiated();
					return null;
				}, getAccessControlContext());
			}
			else {
				smartSingleton.afterSingletonsInstantiated();
			}
		}
	}
}

//AbstractBeanFactory#getBean
public Object getBean(String name) throws BeansException {
	return doGetBean(name, null, null, false);
}

//AbstractBeanFactory#doGetBean
/** Map from bean name to merged RootBeanDefinition. */
private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);
/** Names of beans that have already been created at least once. */
private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
		@Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {

	final String beanName = transformedBeanName(name);
	Object bean;

	// Eagerly check singleton cache for manually registered singletons.
	Object sharedInstance = getSingleton(beanName);
	if (sharedInstance != null && args == null) {
		if (logger.isTraceEnabled()) {
			if (isSingletonCurrentlyInCreation(beanName)) {
				logger.trace("Returning eagerly cached instance of singleton bean '" + beanName +
						"' that is not fully initialized yet - a consequence of a circular reference");
			}
			else {
				logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
			}
		}
		bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
	}

	else {
		// Fail if we're already creating this bean instance:
		// We're assumably within a circular reference.
		//如果是多例模式，并且存在循环依赖，则报错BeanCurrentlyInCreationException
		if (isPrototypeCurrentlyInCreation(beanName)) {
			throw new BeanCurrentlyInCreationException(beanName);
		}

		// Check if bean definition exists in this factory.
		BeanFactory parentBeanFactory = getParentBeanFactory();
		if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
			// Not found -> check parent.
			String nameToLookup = originalBeanName(name);
			if (parentBeanFactory instanceof AbstractBeanFactory) {
				return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
						nameToLookup, requiredType, args, typeCheckOnly);
			}
			else if (args != null) {
				// Delegation to parent with explicit args.
				return (T) parentBeanFactory.getBean(nameToLookup, args);
			}
			else if (requiredType != null) {
				// No args -> delegate to standard getBean method.
				return parentBeanFactory.getBean(nameToLookup, requiredType);
			}
			else {
				return (T) parentBeanFactory.getBean(nameToLookup);
			}
		}

		if (!typeCheckOnly) {
			//判断alreadyCreated中是否存在当前bean，不存在则添加进alreadyCreated
			markBeanAsCreated(beanName);
		}

		try {
			//之前已经将bean和beanDefinition添加进mergedBeanDefinitions
			//现在直接从中获取到值
			final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
			//检查是否为抽象bean
			checkMergedBeanDefinition(mbd, beanName, args);

			// Guarantee initialization of beans that the current bean depends on.
			//检查是否有依赖
			String[] dependsOn = mbd.getDependsOn();
			if (dependsOn != null) {
				for (String dep : dependsOn) {
					if (isDependent(beanName, dep)) {
						throw new BeanCreationException(mbd.getResourceDescription(), beanName,
								"Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
					}
					registerDependentBean(dep, beanName);
					try {
						getBean(dep);
					}
					catch (NoSuchBeanDefinitionException ex) {
						throw new BeanCreationException(mbd.getResourceDescription(), beanName,
								"'" + beanName + "' depends on missing bean '" + dep + "'", ex);
					}
				}
			}

			// Create bean instance.
			if (mbd.isSingleton()) {
				sharedInstance = getSingleton(beanName, () -> {
					try {
						//args == null，通过反射获取到bean对象
						return createBean(beanName, mbd, args);
					}
					catch (BeansException ex) {
						// Explicitly remove instance from singleton cache: It might have been put there
						// eagerly by the creation process, to allow for circular reference resolution.
						// Also remove any beans that received a temporary reference to the bean.
						destroySingleton(beanName);
						throw ex;
					}
				});
				//返回bean实例对象（已经初始化）
				bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
			}

			else if (mbd.isPrototype()) {
				// It's a prototype -> create a new instance.
				Object prototypeInstance = null;
				try {
					beforePrototypeCreation(beanName);
					prototypeInstance = createBean(beanName, mbd, args);
				}
				finally {
					afterPrototypeCreation(beanName);
				}
				bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
			}

			else {
				String scopeName = mbd.getScope();
				final Scope scope = this.scopes.get(scopeName);
				if (scope == null) {
					throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
				}
				try {
					Object scopedInstance = scope.get(beanName, () -> {
						beforePrototypeCreation(beanName);
						try {
							return createBean(beanName, mbd, args);
						}
						finally {
							afterPrototypeCreation(beanName);
						}
					});
					bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
				}
				catch (IllegalStateException ex) {
					throw new BeanCreationException(beanName,
							"Scope '" + scopeName + "' is not active for the current thread; consider " +
							"defining a scoped proxy for this bean if you intend to refer to it from a singleton",
							ex);
				}
			}
		}
		catch (BeansException ex) {
			cleanupAfterBeanCreationFailure(beanName);
			throw ex;
		}
	}

	// Check if required type matches the type of the actual bean instance.
	if (requiredType != null && !requiredType.isInstance(bean)) {
		try {
			T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
			if (convertedBean == null) {
				throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
			}
			return convertedBean;
		}
		catch (TypeMismatchException ex) {
			if (logger.isTraceEnabled()) {
				logger.trace("Failed to convert bean '" + name + "' to required type '" +
						ClassUtils.getQualifiedName(requiredType) + "'", ex);
			}
			throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
		}
	}
	return (T) bean;
}

//DefaultSingletonBeanRegistry#getSingleton
public Object getSingleton(String beanName) {
	return getSingleton(beanName, true);
}

//DefaultSingletonBeanRegistry#getSingleton
//为什么不能只用一级缓存?因为只用一级缓存，当前缓存里会有完成对象和半成品对象，如果此时获取对象，则有可能获取到半成品对象
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
	//先从一级缓存中取数据
	Object singletonObject = this.singletonObjects.get(beanName);
	//一级缓存中的值为空，并判断bean是否正在创建
	if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
		//这里加锁的目的：防止bean还未创建成功，就开始获取bean。
		//如果当前能拿到这把锁，并且bean还未开始创建，则获取的对象为null
		//因为在创建bean对象时，也是singletonObjects同一把锁
		synchronized (this.singletonObjects) {
			//再从二级缓存中取
			singletonObject = this.earlySingletonObjects.get(beanName);
			if (singletonObject == null && allowEarlyReference) {
				ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
				if (singletonFactory != null) {
					//三级缓存中获取匿名内部类,此时会调用getEarlyBeanReference(beanName, mbd, bean)方法
					//如果此时有代理对象，会返回代理对象。每次调用一下该方法就会返回一个新的代理对象。
					//所以需要将三级缓存的半成品对象添加进二级缓存，避免每次都返回一个新的匿名内部类。
					singletonObject = singletonFactory.getObject();
					//再将半成品对象添加进二级缓存中
					this.earlySingletonObjects.put(beanName, singletonObject);
					//将半成品对象从三级缓存中移除
					this.singletonFactories.remove(beanName);
				}
			}
		}
	}
	return singletonObject;
}

//DefaultSingletonBeanRegistry#getSingleton
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
	Assert.notNull(beanName, "Bean name must not be null");
	synchronized (this.singletonObjects) {
		Object singletonObject = this.singletonObjects.get(beanName);
		if (singletonObject == null) {
			if (this.singletonsCurrentlyInDestruction) {
				throw new BeanCreationNotAllowedException(beanName,
						"Singleton bean creation not allowed while singletons of this factory are in destruction " +
						"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
			}
			beforeSingletonCreation(beanName);
			boolean newSingleton = false;
			boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
			if (recordSuppressedExceptions) {
				this.suppressedExceptions = new LinkedHashSet<>();
			}
			try {
				//执行createBean方法
				singletonObject = singletonFactory.getObject();
				newSingleton = true;
			}
			catch (IllegalStateException ex) {
				// Has the singleton object implicitly appeared in the meantime ->
				// if yes, proceed with it since the exception indicates that state.
				singletonObject = this.singletonObjects.get(beanName);
				if (singletonObject == null) {
					throw ex;
				}
			}
			catch (BeanCreationException ex) {
				if (recordSuppressedExceptions) {
					for (Exception suppressedException : this.suppressedExceptions) {
						ex.addRelatedCause(suppressedException);
					}
				}
				throw ex;
			}
			finally {
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = null;
				}
				afterSingletonCreation(beanName);
			}
			if (newSingleton) {
				//将完全体对象添加进一级缓存，并同时移除二级、三级缓存
				addSingleton(beanName, singletonObject);
			}
		}
		return singletonObject;
	}
}

//DefaultSingletonBeanRegistry#addSingleton
//将完全体对象添加进一级缓存，并同时移除二级、三级缓存
protected void addSingleton(String beanName, Object singletonObject) {
	synchronized (this.singletonObjects) {
		this.singletonObjects.put(beanName, singletonObject);
		this.singletonFactories.remove(beanName);
		this.earlySingletonObjects.remove(beanName);
		this.registeredSingletons.add(beanName);
	}
}


//AbstractAutowireCapableBeanFactory#createBean
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
		throws BeanCreationException {

	if (logger.isTraceEnabled()) {
		logger.trace("Creating instance of bean '" + beanName + "'");
	}
	RootBeanDefinition mbdToUse = mbd;

	// Make sure bean class is actually resolved at this point, and
	// clone the bean definition in case of a dynamically resolved Class
	// which cannot be stored in the shared merged bean definition.
	//bean 对应的 class
	Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
	if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
		mbdToUse = new RootBeanDefinition(mbd);
		mbdToUse.setBeanClass(resolvedClass);
	}

	// Prepare method overrides.
	try {
		mbdToUse.prepareMethodOverrides();
	}
	catch (BeanDefinitionValidationException ex) {
		throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
				beanName, "Validation of method overrides failed", ex);
	}

	try {
		// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
		Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
		if (bean != null) {
			return bean;
		}
	}
	catch (Throwable ex) {
		throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
				"BeanPostProcessor before instantiation of bean failed", ex);
	}

	try {
		Object beanInstance = doCreateBean(beanName, mbdToUse, args);
		if (logger.isTraceEnabled()) {
			logger.trace("Finished creating instance of bean '" + beanName + "'");
		}
		return beanInstance;
	}
	catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
		// A previously detected exception with proper bean creation context already,
		// or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
		throw ex;
	}
	catch (Throwable ex) {
		throw new BeanCreationException(
				mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
	}
}

//AbstractAutowireCapableBeanFactory#doCreateBean
//实例和初始化bean
/** Cache of unfinished FactoryBean instances: FactoryBean name to BeanWrapper. */
private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
		throws BeanCreationException {

	// Instantiate the bean.
	// 这个beanWrapper是用来持有创建出来的bean对象
	BeanWrapper instanceWrapper = null;
	if (mbd.isSingleton()) {
		//如果是单例对象，从factoryBean实例缓存中移除当前bean定义信息
		instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
	}
	if (instanceWrapper == null) {
		//创建bean实例，反射实现
		//根据执行bean使用对应的策略创建新的实例，如：工厂方法，构造函数主动注入，简单初始化
		instanceWrapper = createBeanInstance(beanName, mbd, args);
	}
	//获取到bean实例
	final Object bean = instanceWrapper.getWrappedInstance();
	Class<?> beanType = instanceWrapper.getWrappedClass();
	if (beanType != NullBean.class) {
		mbd.resolvedTargetType = beanType;
	}

	// Allow post-processors to modify the merged bean definition.
	synchronized (mbd.postProcessingLock) {
		if (!mbd.postProcessed) {
			try {
				applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
			}
			catch (Throwable ex) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Post-processing of merged bean definition failed", ex);
			}
			mbd.postProcessed = true;
		}
	}

	// Eagerly cache singletons to be able to resolve circular references
	// even when triggered by lifecycle interfaces like BeanFactoryAware.
	//判断当前bean是否正在创建，是否存在循环依赖。如果存在循环依赖，earlySingletonExposure == true
	boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
			isSingletonCurrentlyInCreation(beanName));
	if (earlySingletonExposure) {
		if (logger.isTraceEnabled()) {
			logger.trace("Eagerly caching bean '" + beanName +
					"' to allow for resolving potential circular references");
		}
		//添加三级缓存，移除二级缓存
		addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
	}

	// Initialize the bean instance.
	Object exposedObject = bean;
	try {
		//初始化bean属性
		populateBean(beanName, mbd, instanceWrapper);
		//执行后置处理器和初始化方法
		exposedObject = initializeBean(beanName, exposedObject, mbd);
	}
	catch (Throwable ex) {
		if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
			throw (BeanCreationException) ex;
		}
		else {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
		}
	}

	if (earlySingletonExposure) {
		//此时获取到三级缓存中的半成品对象，对象为匿名内部类
		Object earlySingletonReference = getSingleton(beanName, false);
		if (earlySingletonReference != null) {
			if (exposedObject == bean) {
				exposedObject = earlySingletonReference;
			}
			else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
				String[] dependentBeans = getDependentBeans(beanName);
				Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
				for (String dependentBean : dependentBeans) {
					if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
						actualDependentBeans.add(dependentBean);
					}
				}
				if (!actualDependentBeans.isEmpty()) {
					throw new BeanCurrentlyInCreationException(beanName,
							"Bean with name '" + beanName + "' has been injected into other beans [" +
							StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
							"] in its raw version as part of a circular reference, but has eventually been " +
							"wrapped. This means that said other beans do not use the final version of the " +
							"bean. This is often the result of over-eager type matching - consider using " +
							"'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
				}
			}
		}
	}

	// Register bean as disposable.
	try {
		registerDisposableBeanIfNecessary(beanName, bean, mbd);
	}
	catch (BeanDefinitionValidationException ex) {
		throw new BeanCreationException(
				mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
	}

	return exposedObject;
}

//AbstractAutowireCapableBeanFactory#getEarlyBeanReference
protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
	Object exposedObject = bean;
	//如果有AOP代理，则返回代理对象
	if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
				SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
				exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
			}
		}
	}
	return exposedObject;
}

//DefaultSingletonBeanRegistry#addSingletonFactory
//将半成品对象添加进三级缓存，同时移除二级缓存
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
	Assert.notNull(singletonFactory, "Singleton factory must not be null");
	synchronized (this.singletonObjects) {
		if (!this.singletonObjects.containsKey(beanName)) {
			this.singletonFactories.put(beanName, singletonFactory);
			this.earlySingletonObjects.remove(beanName);
			this.registeredSingletons.add(beanName);
		}
	}
}

//AbstractAutowireCapableBeanFactory#createBeanInstance
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
	// Make sure bean class is actually resolved at this point.
	Class<?> beanClass = resolveBeanClass(mbd, beanName);

	if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
		throw new BeanCreationException(mbd.getResourceDescription(), beanName,
				"Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
	}

	Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
	if (instanceSupplier != null) {
		return obtainFromSupplier(instanceSupplier, beanName);
	}

	if (mbd.getFactoryMethodName() != null) {
		return instantiateUsingFactoryMethod(beanName, mbd, args);
	}

	// Shortcut when re-creating the same bean...
	boolean resolved = false;
	boolean autowireNecessary = false;
	if (args == null) {
		synchronized (mbd.constructorArgumentLock) {
			if (mbd.resolvedConstructorOrFactoryMethod != null) {
				resolved = true;
				autowireNecessary = mbd.constructorArgumentsResolved;
			}
		}
	}
	if (resolved) {
		if (autowireNecessary) {
			return autowireConstructor(beanName, mbd, null, null);
		}
		else {
			return instantiateBean(beanName, mbd);
		}
	}

	// Candidate constructors for autowiring?
	Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
	if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
			mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
		return autowireConstructor(beanName, mbd, ctors, args);
	}

	// Preferred constructors for default construction?
	ctors = mbd.getPreferredConstructors();
	if (ctors != null) {
		return autowireConstructor(beanName, mbd, ctors, null);
	}

	// No special handling: simply use no-arg constructor.
	return instantiateBean(beanName, mbd);
}

//AbstractAutowireCapableBeanFactory#populateBean
//初始化bean属性，通过反射注入属性值，如果属性为对象，则先去实例化和初始化属性对象
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
	if (bw == null) {
		if (mbd.hasPropertyValues()) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
		}
		else {
			// Skip property population phase for null instance.
			return;
		}
	}

	// Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
	// state of the bean before properties are set. This can be used, for example,
	// to support styles of field injection.
	if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
					return;
				}
			}
		}
	}

	PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

	int resolvedAutowireMode = mbd.getResolvedAutowireMode();
	if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
		MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
		// Add property values based on autowire by name if applicable.
		if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
			autowireByName(beanName, mbd, bw, newPvs);
		}
		// Add property values based on autowire by type if applicable.
		if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
			autowireByType(beanName, mbd, bw, newPvs);
		}
		pvs = newPvs;
	}

	boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
	boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

	PropertyDescriptor[] filteredPds = null;
	if (hasInstAwareBpps) {
		if (pvs == null) {
			pvs = mbd.getPropertyValues();
		}
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
				if (pvsToUse == null) {
					if (filteredPds == null) {
						filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
					}
					pvsToUse = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
					if (pvsToUse == null) {
						return;
					}
				}
				pvs = pvsToUse;
			}
		}
	}
	if (needsDepCheck) {
		if (filteredPds == null) {
			filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
		}
		checkDependencies(beanName, mbd, filteredPds, pvs);
	}

	if (pvs != null) {
		//通过反射setXX将属性值注入到对象中
		//开始填充属性值
		applyPropertyValues(beanName, mbd, bw, pvs);
	}
}

//AbstractAutowireCapableBeanFactory#applyPropertyValues
protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
	if (pvs.isEmpty()) {
		return;
	}

	if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
		((BeanWrapperImpl) bw).setSecurityContext(getAccessControlContext());
	}

	MutablePropertyValues mpvs = null;
	List<PropertyValue> original;

	if (pvs instanceof MutablePropertyValues) {
		mpvs = (MutablePropertyValues) pvs;
		if (mpvs.isConverted()) {
			// Shortcut: use the pre-converted values as-is.
			try {
				bw.setPropertyValues(mpvs);
				return;
			}
			catch (BeansException ex) {
				throw new BeanCreationException(
						mbd.getResourceDescription(), beanName, "Error setting property values", ex);
			}
		}
		original = mpvs.getPropertyValueList();
	}
	else {
		original = Arrays.asList(pvs.getPropertyValues());
	}

	TypeConverter converter = getCustomTypeConverter();
	if (converter == null) {
		converter = bw;
	}
	BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);

	// Create a deep copy, resolving any references for values.
	List<PropertyValue> deepCopy = new ArrayList<>(original.size());
	boolean resolveNecessary = false;
	for (PropertyValue pv : original) {
		if (pv.isConverted()) {
			deepCopy.add(pv);
		}
		else {
			String propertyName = pv.getName();
			Object originalValue = pv.getValue();
			if (originalValue == AutowiredPropertyMarker.INSTANCE) {
				Method writeMethod = bw.getPropertyDescriptor(propertyName).getWriteMethod();
				if (writeMethod == null) {
					throw new IllegalArgumentException("Autowire marker for property without write method: " + pv);
				}
				originalValue = new DependencyDescriptor(new MethodParameter(writeMethod, 0), true);
			}
			Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
			Object convertedValue = resolvedValue;
			boolean convertible = bw.isWritableProperty(propertyName) &&
					!PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
			if (convertible) {
				convertedValue = convertForProperty(resolvedValue, propertyName, bw, converter);
			}
			// Possibly store converted value in merged bean definition,
			// in order to avoid re-conversion for every created bean instance.
			if (resolvedValue == originalValue) {
				if (convertible) {
					pv.setConvertedValue(convertedValue);
				}
				deepCopy.add(pv);
			}
			else if (convertible && originalValue instanceof TypedStringValue &&
					!((TypedStringValue) originalValue).isDynamic() &&
					!(convertedValue instanceof Collection || ObjectUtils.isArray(convertedValue))) {
				pv.setConvertedValue(convertedValue);
				deepCopy.add(pv);
			}
			else {
				resolveNecessary = true;
				deepCopy.add(new PropertyValue(pv, convertedValue));
			}
		}
	}
	if (mpvs != null && !resolveNecessary) {
		mpvs.setConverted();
	}

	// Set our (possibly massaged) deep copy.
	try {
		bw.setPropertyValues(new MutablePropertyValues(deepCopy));
	}
	catch (BeansException ex) {
		throw new BeanCreationException(
				mbd.getResourceDescription(), beanName, "Error setting property values", ex);
	}
}

//BeanDefinitionValueResolver#resolveValueIfNecessary
public Object resolveValueIfNecessary(Object argName, @Nullable Object value) {
	// We must check each value to see whether it requires a runtime reference
	// to another bean to be resolved.
	if (value instanceof RuntimeBeanReference) {
		RuntimeBeanReference ref = (RuntimeBeanReference) value;
		return resolveReference(argName, ref);
	}
	else if (value instanceof RuntimeBeanNameReference) {
		String refName = ((RuntimeBeanNameReference) value).getBeanName();
		refName = String.valueOf(doEvaluate(refName));
		if (!this.beanFactory.containsBean(refName)) {
			throw new BeanDefinitionStoreException(
					"Invalid bean name '" + refName + "' in bean reference for " + argName);
		}
		return refName;
	}
	else if (value instanceof BeanDefinitionHolder) {
		// Resolve BeanDefinitionHolder: contains BeanDefinition with name and aliases.
		BeanDefinitionHolder bdHolder = (BeanDefinitionHolder) value;
		return resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
	}
	else if (value instanceof BeanDefinition) {
		// Resolve plain BeanDefinition, without contained name: use dummy name.
		BeanDefinition bd = (BeanDefinition) value;
		String innerBeanName = "(inner bean)" + BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR +
				ObjectUtils.getIdentityHexString(bd);
		return resolveInnerBean(argName, innerBeanName, bd);
	}
	else if (value instanceof DependencyDescriptor) {
		Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
		Object result = this.beanFactory.resolveDependency(
				(DependencyDescriptor) value, this.beanName, autowiredBeanNames, this.typeConverter);
		for (String autowiredBeanName : autowiredBeanNames) {
			if (this.beanFactory.containsBean(autowiredBeanName)) {
				this.beanFactory.registerDependentBean(autowiredBeanName, this.beanName);
			}
		}
		return result;
	}
	else if (value instanceof ManagedArray) {
		// May need to resolve contained runtime references.
		ManagedArray array = (ManagedArray) value;
		Class<?> elementType = array.resolvedElementType;
		if (elementType == null) {
			String elementTypeName = array.getElementTypeName();
			if (StringUtils.hasText(elementTypeName)) {
				try {
					elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
					array.resolvedElementType = elementType;
				}
				catch (Throwable ex) {
					// Improve the message by showing the context.
					throw new BeanCreationException(
							this.beanDefinition.getResourceDescription(), this.beanName,
							"Error resolving array type for " + argName, ex);
				}
			}
			else {
				elementType = Object.class;
			}
		}
		return resolveManagedArray(argName, (List<?>) value, elementType);
	}
	else if (value instanceof ManagedList) {
		// May need to resolve contained runtime references.
		return resolveManagedList(argName, (List<?>) value);
	}
	else if (value instanceof ManagedSet) {
		// May need to resolve contained runtime references.
		return resolveManagedSet(argName, (Set<?>) value);
	}
	else if (value instanceof ManagedMap) {
		// May need to resolve contained runtime references.
		return resolveManagedMap(argName, (Map<?, ?>) value);
	}
	else if (value instanceof ManagedProperties) {
		Properties original = (Properties) value;
		Properties copy = new Properties();
		original.forEach((propKey, propValue) -> {
			if (propKey instanceof TypedStringValue) {
				propKey = evaluate((TypedStringValue) propKey);
			}
			if (propValue instanceof TypedStringValue) {
				propValue = evaluate((TypedStringValue) propValue);
			}
			if (propKey == null || propValue == null) {
				throw new BeanCreationException(
						this.beanDefinition.getResourceDescription(), this.beanName,
						"Error converting Properties key/value pair for " + argName + ": resolved to null");
			}
			copy.put(propKey, propValue);
		});
		return copy;
	}
	else if (value instanceof TypedStringValue) {
		// Convert value to target type here.
		TypedStringValue typedStringValue = (TypedStringValue) value;
		Object valueObject = evaluate(typedStringValue);
		try {
			Class<?> resolvedTargetType = resolveTargetType(typedStringValue);
			if (resolvedTargetType != null) {
				return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
			}
			else {
				return valueObject;
			}
		}
		catch (Throwable ex) {
			// Improve the message by showing the context.
			throw new BeanCreationException(
					this.beanDefinition.getResourceDescription(), this.beanName,
					"Error converting typed String value for " + argName, ex);
		}
	}
	else if (value instanceof NullBean) {
		return null;
	}
	else {
		return evaluate(value);
	}
}

//BeanDefinitionValueResolver#resolveInnerBean
private Object resolveInnerBean(Object argName, String innerBeanName, BeanDefinition innerBd) {
	RootBeanDefinition mbd = null;
	try {
		mbd = this.beanFactory.getMergedBeanDefinition(innerBeanName, innerBd, this.beanDefinition);
		// Check given bean name whether it is unique. If not already unique,
		// add counter - increasing the counter until the name is unique.
		String actualInnerBeanName = innerBeanName;
		if (mbd.isSingleton()) {
			actualInnerBeanName = adaptInnerBeanName(innerBeanName);
		}
		this.beanFactory.registerContainedBean(actualInnerBeanName, this.beanName);
		// Guarantee initialization of beans that the inner bean depends on.
		String[] dependsOn = mbd.getDependsOn();
		if (dependsOn != null) {
			for (String dependsOnBean : dependsOn) {
				this.beanFactory.registerDependentBean(dependsOnBean, actualInnerBeanName);
				this.beanFactory.getBean(dependsOnBean);
			}
		}
		// Actually create the inner bean instance now...
		//接着又去实例化和初始化bean
		Object innerBean = this.beanFactory.createBean(actualInnerBeanName, mbd, null);
		if (innerBean instanceof FactoryBean) {
			boolean synthetic = mbd.isSynthetic();
			innerBean = this.beanFactory.getObjectFromFactoryBean(
					(FactoryBean<?>) innerBean, actualInnerBeanName, !synthetic);
		}
		if (innerBean instanceof NullBean) {
			innerBean = null;
		}
		return innerBean;
	}
	catch (BeansException ex) {
		throw new BeanCreationException(
				this.beanDefinition.getResourceDescription(), this.beanName,
				"Cannot create inner bean '" + innerBeanName + "' " +
				(mbd != null && mbd.getBeanClassName() != null ? "of type [" + mbd.getBeanClassName() + "] " : "") +
				"while setting " + argName, ex);
	}
}

//AbstractPropertyAccessor#setPropertyValues
//注入属性值
public void setPropertyValues(PropertyValues pvs) throws BeansException {
	setPropertyValues(pvs, false, false);
}
//AbstractPropertyAccessor#setPropertyValues
public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
		throws BeansException {

	List<PropertyAccessException> propertyAccessExceptions = null;
	List<PropertyValue> propertyValues = (pvs instanceof MutablePropertyValues ?
			((MutablePropertyValues) pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues()));
	for (PropertyValue pv : propertyValues) {
		try {
			// This method may throw any BeansException, which won't be caught
			// here, if there is a critical failure such as no matching field.
			// We can attempt to deal only with less serious exceptions.
			setPropertyValue(pv);
		}
		catch (NotWritablePropertyException ex) {
			if (!ignoreUnknown) {
				throw ex;
			}
			// Otherwise, just ignore it and continue...
		}
		catch (NullValueInNestedPathException ex) {
			if (!ignoreInvalid) {
				throw ex;
			}
			// Otherwise, just ignore it and continue...
		}
		catch (PropertyAccessException ex) {
			if (propertyAccessExceptions == null) {
				propertyAccessExceptions = new ArrayList<>();
			}
			propertyAccessExceptions.add(ex);
		}
	}

	// If we encountered individual exceptions, throw the composite exception.
	if (propertyAccessExceptions != null) {
		PropertyAccessException[] paeArray = propertyAccessExceptions.toArray(new PropertyAccessException[0]);
		throw new PropertyBatchUpdateException(paeArray);
	}
}
//AbstractPropertyAccessor#setPropertyValue
public void setPropertyValue(PropertyValue pv) throws BeansException {
	setPropertyValue(pv.getName(), pv.getValue());
}

//AbstractNestablePropertyAccessor#setPropertyValue
public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException {
	AbstractNestablePropertyAccessor nestedPa;
	try {
		nestedPa = getPropertyAccessorForPropertyPath(propertyName);
	}
	catch (NotReadablePropertyException ex) {
		throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName,
				"Nested property in path '" + propertyName + "' does not exist", ex);
	}
	PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedPa, propertyName));
	nestedPa.setPropertyValue(tokens, new PropertyValue(propertyName, value));
}
//AbstractNestablePropertyAccessor#setPropertyValue
protected void setPropertyValue(PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
	if (tokens.keys != null) {
		processKeyedProperty(tokens, pv);
	}
	else {
		processLocalProperty(tokens, pv);
	}
}

//AbstractNestablePropertyAccessor#processLocalProperty
private void processLocalProperty(PropertyTokenHolder tokens, PropertyValue pv) {
	PropertyHandler ph = getLocalPropertyHandler(tokens.actualName);
	if (ph == null || !ph.isWritable()) {
		if (pv.isOptional()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Ignoring optional value for property '" + tokens.actualName +
						"' - property not found on bean class [" + getRootClass().getName() + "]");
			}
			return;
		}
		else {
			throw createNotWritablePropertyException(tokens.canonicalName);
		}
	}

	Object oldValue = null;
	try {
		Object originalValue = pv.getValue();
		Object valueToApply = originalValue;
		if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
			if (pv.isConverted()) {
				valueToApply = pv.getConvertedValue();
			}
			else {
				if (isExtractOldValueForEditor() && ph.isReadable()) {
					try {
						oldValue = ph.getValue();
					}
					catch (Exception ex) {
						if (ex instanceof PrivilegedActionException) {
							ex = ((PrivilegedActionException) ex).getException();
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Could not read previous value of property '" +
									this.nestedPath + tokens.canonicalName + "'", ex);
						}
					}
				}
				valueToApply = convertForProperty(
						tokens.canonicalName, oldValue, originalValue, ph.toTypeDescriptor());
			}
			pv.getOriginalPropertyValue().conversionNecessary = (valueToApply != originalValue);
		}
		ph.setValue(valueToApply);
	}
	catch (TypeMismatchException ex) {
		throw ex;
	}
	catch (InvocationTargetException ex) {
		PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(
				getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
		if (ex.getTargetException() instanceof ClassCastException) {
			throw new TypeMismatchException(propertyChangeEvent, ph.getPropertyType(), ex.getTargetException());
		}
		else {
			Throwable cause = ex.getTargetException();
			if (cause instanceof UndeclaredThrowableException) {
				// May happen e.g. with Groovy-generated methods
				cause = cause.getCause();
			}
			throw new MethodInvocationException(propertyChangeEvent, cause);
		}
	}
	catch (Exception ex) {
		PropertyChangeEvent pce = new PropertyChangeEvent(
				getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
		throw new MethodInvocationException(pce, ex);
	}
}

//BeanWrapperImpl#setValue
//通过反射注入属性值
public void setValue(final @Nullable Object value) throws Exception {
	final Method writeMethod = (this.pd instanceof GenericTypeAwarePropertyDescriptor ?
			((GenericTypeAwarePropertyDescriptor) this.pd).getWriteMethodForActualAccess() :
			this.pd.getWriteMethod());
	if (System.getSecurityManager() != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			ReflectionUtils.makeAccessible(writeMethod);
			return null;
		});
		try {
			AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () ->
					writeMethod.invoke(getWrappedInstance(), value), acc);
		}
		catch (PrivilegedActionException ex) {
			throw ex.getException();
		}
	}
	else {
		ReflectionUtils.makeAccessible(writeMethod);
		//通过反射注入属性值 setXX
		writeMethod.invoke(getWrappedInstance(), value);
	}
}

//AbstractAutowireCapableBeanFactory#initializeBean
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
	if (System.getSecurityManager() != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			invokeAwareMethods(beanName, bean);
			return null;
		}, getAccessControlContext());
	}
	else {
		invokeAwareMethods(beanName, bean);
	}

	Object wrappedBean = bean;
	if (mbd == null || !mbd.isSynthetic()) {
		//执行后置处理器的前置方法
		wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
	}

	try {
		//执行初始化方法
		invokeInitMethods(beanName, wrappedBean, mbd);
	}
	catch (Throwable ex) {
		throw new BeanCreationException(
				(mbd != null ? mbd.getResourceDescription() : null),
				beanName, "Invocation of init method failed", ex);
	}
	if (mbd == null || !mbd.isSynthetic()) {
		//执行后置处理器的后置方法
		wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
	}

	return wrappedBean;
}

//AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
		throws BeansException {

	Object result = existingBean;
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		Object current = processor.postProcessBeforeInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}

//AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
		throws BeansException {

	Object result = existingBean;
	for (BeanPostProcessor processor : getBeanPostProcessors()) {
		Object current = processor.postProcessAfterInitialization(result, beanName);
		if (current == null) {
			return result;
		}
		result = current;
	}
	return result;
}

//AbstractAutowireCapableBeanFactory#invokeInitMethods
//执行初始化方法，如果bean实现了InitializingBean会先执行afterPropertiesSet()方法，再执行指定的初始化方法 
protected void invokeInitMethods(String beanName, final Object bean, @Nullable RootBeanDefinition mbd)
		throws Throwable {

	boolean isInitializingBean = (bean instanceof InitializingBean);
	if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
		if (logger.isTraceEnabled()) {
			logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
		}
		if (System.getSecurityManager() != null) {
			try {
				AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
					((InitializingBean) bean).afterPropertiesSet();
					return null;
				}, getAccessControlContext());
			}
			catch (PrivilegedActionException pae) {
				throw pae.getException();
			}
		}
		else {
			((InitializingBean) bean).afterPropertiesSet();
		}
	}

	if (mbd != null && bean.getClass() != NullBean.class) {
		String initMethodName = mbd.getInitMethodName();
		//如果设置了初始化方法，执行初始化方法
		if (StringUtils.hasLength(initMethodName) &&
				!(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
				!mbd.isExternallyManagedInitMethod(initMethodName)) {
			invokeCustomInitMethod(beanName, bean, mbd);
		}
	}
}

//AbstractAutowireCapableBeanFactory#invokeCustomInitMethod
protected void invokeCustomInitMethod(String beanName, final Object bean, RootBeanDefinition mbd)
		throws Throwable {

	String initMethodName = mbd.getInitMethodName();
	Assert.state(initMethodName != null, "No init method set");
	Method initMethod = (mbd.isNonPublicAccessAllowed() ?
			BeanUtils.findMethod(bean.getClass(), initMethodName) :
			ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName));

	if (initMethod == null) {
		if (mbd.isEnforceInitMethod()) {
			throw new BeanDefinitionValidationException("Could not find an init method named '" +
					initMethodName + "' on bean with name '" + beanName + "'");
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("No default init method named '" + initMethodName +
						"' found on bean with name '" + beanName + "'");
			}
			// Ignore non-existent default lifecycle methods.
			return;
		}
	}

	if (logger.isTraceEnabled()) {
		logger.trace("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'");
	}
	Method methodToInvoke = ClassUtils.getInterfaceMethodIfPossible(initMethod);

	if (System.getSecurityManager() != null) {
		AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
			ReflectionUtils.makeAccessible(methodToInvoke);
			return null;
		});
		try {
			AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () ->
					methodToInvoke.invoke(bean), getAccessControlContext());
		}
		catch (PrivilegedActionException pae) {
			InvocationTargetException ex = (InvocationTargetException) pae.getException();
			throw ex.getTargetException();
		}
	}
	else {
		try {
			ReflectionUtils.makeAccessible(methodToInvoke);
			methodToInvoke.invoke(bean);
		}
		catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
	}
}
