
//ClassPathXmlApplicationContext#ClassPathXmlApplicationContext
public ClassPathXmlApplicationContext(
		String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
		throws BeansException {
	//调用父类构造方法，进行相关的对象创建等操作，包含属性的赋值操作
	super(parent);
	setConfigLocations(configLocations);
	if (refresh) {
		//最重要的方法
		refresh();
	}
}

//AbstractApplicationContext#refresh
public void refresh() throws BeansException, IllegalStateException {
	synchronized (this.startupShutdownMonitor) {
		// Prepare this context for refreshing.
		/*
		做容器刷新前的准备工作
		1.设置容器的启动时间
		2.设置活跃状态为true
		3.设置关闭状态为false
		4.获取Environment对象，并加载当前系统的属性值到Environment对象中
		5.准备监听器和事件的集合对象，默认为空集合
		*/
		prepareRefresh();

		// Tell the subclass to refresh the internal bean factory.
		// 创建beanFactory工厂和创建BeanDefinition（读取xml中的bean信息）
		//将beanName和beanDefinition放入 DefaultListableBeanFactory中的beanDefinitionMap和beanDefinitionNames
		//创建容器对象，DefaultListableBeanFactory
		//加载xml配置文件的属性值到当前工厂中，最重要的就是BeanDefinition
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

		// Prepare the bean factory for use in this context.
		// 设置beanFactory的属性值
		//beanFactory的准备工作，对各种属性进行填充
		prepareBeanFactory(beanFactory);

		try {
			// Allows post-processing of the bean factory in context subclasses.
			// 留给子类做扩展实现的
			//子类覆盖方法做额外的处理，此时我们一般不做任何扩展工作，但是可以查看web中的代码，是有具体实现的
			postProcessBeanFactory(beanFactory);

			// Invoke factory processors registered as beans in the context.
			// 调用beanFactory的各种处理器
			invokeBeanFactoryPostProcessors(beanFactory);

			// Register bean processors that intercept bean creation.
			//注册bean处理器，这里只是注册功能，真正调用的是getBean方法
			registerBeanPostProcessors(beanFactory);

			// Initialize message source for this context.
			//国际化i18n（跟SpringMVC相关联）
			//为上下文初始化message源，即不同语言的消息体，国际化处理
			initMessageSource();

			// Initialize event multicaster for this context.
			// 初始化事件监听多路广播器
			initApplicationEventMulticaster();

			// Initialize other special beans in specific context subclasses.
			//留给子类来初始化其它bean
			onRefresh();

			// Check for listener beans and register them.
			//在所有注册的bean中查找listener bean，注册到消息广播器中
			registerListeners();

			// Instantiate all remaining (non-lazy-init) singletons.
			//完成bean的实例化和初始化和执行后置处理器方法和初始化方法
			finishBeanFactoryInitialization(beanFactory);

			// Last step: publish corresponding event.
			finishRefresh();
		}

		catch (BeansException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception encountered during context initialization - " +
						"cancelling refresh attempt: " + ex);
			}

			// Destroy already created singletons to avoid dangling resources.
			destroyBeans();

			// Reset 'active' flag.
			cancelRefresh(ex);

			// Propagate exception to caller.
			throw ex;
		}

		finally {
			// Reset common introspection caches in Spring's core, since we
			// might not ever need metadata for singleton beans anymore...
			resetCommonCaches();
		}
	}
}


protected void prepareRefresh() {
	// Switch to active.
	// 设置容器启动的时间
	this.startupDate = System.currentTimeMillis();
	// 容器的关闭标志位
	this.closed.set(false);
	// 容器的激活标志位
	this.active.set(true);

	if (logger.isDebugEnabled()) {
		if (logger.isTraceEnabled()) {
			logger.trace("Refreshing " + this);
		}
		else {
			logger.debug("Refreshing " + getDisplayName());
		}
	}

	// Initialize any placeholder property sources in the context environment.
	// 留给子类覆盖，初始化属性资源
	initPropertySources();

	// Validate that all properties marked as required are resolvable:
	// see ConfigurablePropertyResolver#setRequiredProperties
	// 创建并获取环境对象，验证需要的属性文件是否都已经放入环境中
	getEnvironment().validateRequiredProperties();

	// Store pre-refresh ApplicationListeners...
	// 判断刷新前的应用程序监听器集合是否为空，如果为空，则将监听器添加进此集合中
	if (this.earlyApplicationListeners == null) {
		this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
	}
	else {
		// Reset local application listeners to pre-refresh state.
		// 如果不等于空，则清空集合元素对象
		this.applicationListeners.clear();
		this.applicationListeners.addAll(this.earlyApplicationListeners);
	}

	// Allow for the collection of early ApplicationEvents,
	// to be published once the multicaster is available...
	// 创建刷新前的监听事件集合
	this.earlyApplicationEvents = new LinkedHashSet<>();
}


//DefaultSingletonBeanRegistry
/** Cache of singleton objects: bean name to bean instance. */
//一级缓存
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

/** Cache of singleton factories: bean name to ObjectFactory. */
//三级缓存
//ObjectFactory是一个函数式接口，可以传入lambda表达式，可以是匿名内部类，通过调用getObject()方法来执行具体的逻辑
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

/** Cache of early singleton objects: bean name to bean instance. */
//二级缓存
private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);



public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {
	//设置xml文件的验证标志，默认为true
	private boolean validating = true;


