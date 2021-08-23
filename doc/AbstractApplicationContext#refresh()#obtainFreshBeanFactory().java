//AbstractApplicationContext#refresh
//完成beanFactory的创建和将beanDefinition注册进beanFactory工厂
ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();


//AbstractApplicationContext#obtainFreshBeanFactory
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
	// 初始化BeanFactory，并进行XML文件读取，并将得到的BeanFactory记录在当前实体的属性中
	refreshBeanFactory();
	// 返回当前实体的beanFactory属性
	return getBeanFactory();
}

//AbstractRefreshableApplicationContext#refreshBeanFactory
protected final void refreshBeanFactory() throws BeansException {
	//如果存在beanFactory，则销毁beanFactory
	if (hasBeanFactory()) {
		destroyBeans();
		closeBeanFactory();
	}
	try {
		//创建DefaultListableBeanFactory beanFactory工厂
		DefaultListableBeanFactory beanFactory = createBeanFactory();
		//为了序列化指定id，可以从id反序列化到beanFactory对象
		beanFactory.setSerializationId(getId());
		//定制beanFactory，设置相关属性，包括是否允许覆盖同名称的不同定义的对象以及循环依赖
		customizeBeanFactory(beanFactory);
		//初始化documentReader，并进行XML文件读取及解析，默认命名空间的解析，自定义标签的解析
		loadBeanDefinitions(beanFactory);
		this.beanFactory = beanFactory;
	}
	catch (IOException ex) {
		throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
	}
}

//AbstractRefreshableApplicationContext#createBeanFactory
protected DefaultListableBeanFactory createBeanFactory() {
	return new DefaultListableBeanFactory(getInternalParentBeanFactory());
}

//AbstractXmlApplicationContext#loadBeanDefinitions
protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
	// Create a new XmlBeanDefinitionReader for the given BeanFactory.
	//注册了DefaultListableBeanFactory到AbstractBeanDefinitionReader
	// 适配器模式
	XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
	//public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
	//	super(registry);
	//}

	// Configure the bean definition reader with this context's
	// resource loading environment.
	beanDefinitionReader.setEnvironment(this.getEnvironment());
	//this == ClassPathXmlApplicationContext
	beanDefinitionReader.setResourceLoader(this);
	/*
	xsd,dtd文件是来定义标签的定义信息的
	ResourceEntityResolver是来读取标签库
	 */
	beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

	// Allow a subclass to provide custom initialization of the reader,
	// then proceed with actually loading the bean definitions.
	// 加载beanDefinitionReader
	initBeanDefinitionReader(beanDefinitionReader);
	//创建beanDefinition
	loadBeanDefinitions(beanDefinitionReader);
}

//beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
public ResourceEntityResolver(ResourceLoader resourceLoader) {
	super(resourceLoader.getClassLoader());
	this.resourceLoader = resourceLoader;
}
//super(resourceLoader.getClassLoader());
public DelegatingEntityResolver(@Nullable ClassLoader classLoader) {
	this.dtdResolver = new BeansDtdResolver();
	/*
	当完成这行代码的调用之后，大家神奇的发现一件事情，schemaResolver对象的schemaMappingsLocation属性被完成了赋值操作，但是你遍历完成所有代码后依然没有看到显式调用
	其实此时的原理是非常简单的，我们在进行debug的时候，因为在程序运行期间需要显示当前类的所有信息，所以idea会帮助我们调用toString()方法，只不过此过程我们识别不到而已
	 */
	this.schemaResolver = new PluggableSchemaResolver(classLoader);
}
//this.schemaResolver = new PluggableSchemaResolver(classLoader);
public PluggableSchemaResolver(@Nullable ClassLoader classLoader) {
	this.classLoader = classLoader;
	this.schemaMappingsLocation = DEFAULT_SCHEMA_MAPPINGS_LOCATION;
}

//AbstractXmlApplicationContext#loadBeanDefinitions
protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
	Resource[] configResources = getConfigResources();
	if (configResources != null) {
		reader.loadBeanDefinitions(configResources);
	}
	String[] configLocations = getConfigLocations();
	if (configLocations != null) {
		reader.loadBeanDefinitions(configLocations);
	}
}

//AbstractBeanDefinitionReader#loadBeanDefinitions
public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
	Assert.notNull(locations, "Location array must not be null");
	int count = 0;
	for (String location : locations) {
		count += loadBeanDefinitions(location);
	}
	return count;
}
//AbstractBeanDefinitionReader#loadBeanDefinitions
public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
	return loadBeanDefinitions(location, null);
}

//AbstractBeanDefinitionReader#loadBeanDefinitions
public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
	//ClassPathXmlApplicationContext
	ResourceLoader resourceLoader = getResourceLoader();
	if (resourceLoader == null) {
		throw new BeanDefinitionStoreException(
				"Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
	}

	if (resourceLoader instanceof ResourcePatternResolver) {
		// Resource pattern matching available.
		try {
			Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
			int count = loadBeanDefinitions(resources);
			if (actualResources != null) {
				Collections.addAll(actualResources, resources);
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
			}
			return count;
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"Could not resolve bean definition resource pattern [" + location + "]", ex);
		}
	}
	else {
		// Can only load single resources by absolute URL.
		Resource resource = resourceLoader.getResource(location);
		int count = loadBeanDefinitions(resource);
		if (actualResources != null) {
			actualResources.add(resource);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
		}
		return count;
	}
}

//XmlBeanDefinitionReader#loadBeanDefinitions
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
	//使用默认的编码和字符集
	return loadBeanDefinitions(new EncodedResource(resource));
}

//XmlBeanDefinitionReader#loadBeanDefinitions
public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
	Assert.notNull(encodedResource, "EncodedResource must not be null");
	if (logger.isTraceEnabled()) {
		logger.trace("Loading XML bean definitions from " + encodedResource);
	}

	Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();

	if (!currentResources.add(encodedResource)) {
		throw new BeanDefinitionStoreException(
				"Detected cyclic loading of " + encodedResource + " - check your import definitions!");
	}

	try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
		InputSource inputSource = new InputSource(inputStream);
		if (encodedResource.getEncoding() != null) {
			inputSource.setEncoding(encodedResource.getEncoding());
		}
		return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
	}
	catch (IOException ex) {
		throw new BeanDefinitionStoreException(
				"IOException parsing XML document from " + encodedResource.getResource(), ex);
	}
	finally {
		currentResources.remove(encodedResource);
		if (currentResources.isEmpty()) {
			this.resourcesCurrentlyBeingLoaded.remove();
		}
	}
}

//XmlBeanDefinitionReader#doLoadBeanDefinitions
protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
			throws BeanDefinitionStoreException {

	try {
		/*
		此处获取xml文件的document对象，这个解析过程是由documentLoader完成的
		从String[] -> String -> Resource[] -> Resource,最终开始将Resource读取成一个document文档对象
		根据文档的节点信息封装成一个个的BeanDefinition对象
		 */
		Document doc = doLoadDocument(inputSource, resource);
		//核心方法
		int count = registerBeanDefinitions(doc, resource);
		if (logger.isDebugEnabled()) {
			logger.debug("Loaded " + count + " bean definitions from " + resource);
		}
		return count;
	}
	catch (BeanDefinitionStoreException ex) {
		throw ex;
	}
	catch (SAXParseException ex) {
		throw new XmlBeanDefinitionStoreException(resource.getDescription(),
				"Line " + ex.getLineNumber() + " in XML document from " + resource + " is invalid", ex);
	}
	catch (SAXException ex) {
		throw new XmlBeanDefinitionStoreException(resource.getDescription(),
				"XML document from " + resource + " is invalid", ex);
	}
	catch (ParserConfigurationException ex) {
		throw new BeanDefinitionStoreException(resource.getDescription(),
				"Parser configuration exception parsing XML from " + resource, ex);
	}
	catch (IOException ex) {
		throw new BeanDefinitionStoreException(resource.getDescription(),
				"IOException parsing XML document from " + resource, ex);
	}
	catch (Throwable ex) {
		throw new BeanDefinitionStoreException(resource.getDescription(),
				"Unexpected exception parsing XML document from " + resource, ex);
	}
}

//XmlBeanDefinitionReader#registerBeanDefinitions
public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
	//DefaultBeanDefinitionDocumentReader对象
	// 对xml的beanDefinition进行解析
	BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
	int countBefore = getRegistry().getBeanDefinitionCount();
	//createReaderContext() --> XmlReaderContext
	//完成具体的解析过程
	documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
	return getRegistry().getBeanDefinitionCount() - countBefore;
}

//DefaultBeanDefinitionDocumentReader#registerBeanDefinitions
public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
	this.readerContext = readerContext;
	doRegisterBeanDefinitions(doc.getDocumentElement());
}
//DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions
protected void doRegisterBeanDefinitions(Element root) {
	// Any nested <beans> elements will cause recursion in this method. In
	// order to propagate and preserve <beans> default-* attributes correctly,
	// keep track of the current (parent) delegate, which may be null. Create
	// the new (child) delegate with a reference to the parent for fallback purposes,
	// then ultimately reset this.delegate back to its original (parent) reference.
	// this behavior emulates a stack of delegates without actually necessitating one.
	BeanDefinitionParserDelegate parent = this.delegate;
	this.delegate = createDelegate(getReaderContext(), root, parent);

	if (this.delegate.isDefaultNamespace(root)) {
		String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
		if (StringUtils.hasText(profileSpec)) {
			String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
					profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
			// We cannot use Profiles.of(...) since profile expressions are not supported
			// in XML config. See SPR-12458 for details.
			if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
							"] not matching: " + getReaderContext().getResource());
				}
				return;
			}
		}
	}
	//可以自定义解析xml之前的工作
	preProcessXml(root);
	//解析beanDefinition
	parseBeanDefinitions(root, this.delegate);
	//自定义解析xml之后的工作
	postProcessXml(root);

	this.delegate = parent;
}
//DefaultBeanDefinitionDocumentReader#parseBeanDefinitions
//从beans开始解析
protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
	if (delegate.isDefaultNamespace(root)) {
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				Element ele = (Element) node;
				//默认的名称空间
				if (delegate.isDefaultNamespace(ele)) {
					parseDefaultElement(ele, delegate);
				}
				//自定义的名称空间
				else {
					delegate.parseCustomElement(ele);
				}
			}
		}
	}
	//自定义的名称空间
	else {
		delegate.parseCustomElement(root);
	}
}

//DefaultBeanDefinitionDocumentReader#parseDefaultElement
//默认名称空间
private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
	if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
		importBeanDefinitionResource(ele);
	}
	else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
		processAliasRegistration(ele);
	}
	else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
		processBeanDefinition(ele, delegate);
	}
	else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
		// recurse
		doRegisterBeanDefinitions(ele);
	}
}

//DefaultBeanDefinitionDocumentReader#processBeanDefinition
//解析bean标签，并将bean标签的属性映射到beanDefinition对象中
protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
	//解析并封装了GenericBeanDefinition 
	//new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray)
	/*
		beanDefinitionHolder是beanDefinition对象的封装类，封装了BeanDefinition,bean的名字和别名，用它来完成向IOC容器的注册
		得到这个beanDefinitionHolder就意味着beanDefinition是通过BeanDefinitionParserDelegate对xml元素的信息按照spring的bean规则进行解析得到的
	 */
	BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
	if (bdHolder != null) {
		bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
		try {
			// Register the final decorated instance.
			BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
		}
		catch (BeanDefinitionStoreException ex) {
			getReaderContext().error("Failed to register bean definition with name '" +
					bdHolder.getBeanName() + "'", ele, ex);
		}
		// Send registration event.
		getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
	}
}

//BeanDefinitionParserDelegate#parseBeanDefinitionElement
//解析bean
public BeanDefinitionHolder parseBeanDefinitionElement(Element ele) {
	return parseBeanDefinitionElement(ele, null);
}

//BeanDefinitionParserDelegate#parseBeanDefinitionElement
public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, @Nullable BeanDefinition containingBean) {
	//解析id属性
	String id = ele.getAttribute(ID_ATTRIBUTE);
	//解析name属性
	String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);
	//如果bean有别名的话，那么将别名分割解析
	List<String> aliases = new ArrayList<>();
	if (StringUtils.hasLength(nameAttr)) {
		String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
		aliases.addAll(Arrays.asList(nameArr));
	}

	String beanName = id;
	if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
		beanName = aliases.remove(0);
		if (logger.isTraceEnabled()) {
			logger.trace("No XML 'id' specified - using '" + beanName +
					"' as bean name and " + aliases + " as aliases");
		}
	}

	if (containingBean == null) {
		checkNameUniqueness(beanName, aliases, ele);
	}

	//解析并封装为BeanDefinition
	//对bean元素的详细解析
	AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean);
	if (beanDefinition != null) {
		if (!StringUtils.hasText(beanName)) {
			try {
				if (containingBean != null) {
					beanName = BeanDefinitionReaderUtils.generateBeanName(
							beanDefinition, this.readerContext.getRegistry(), true);
				}
				else {
					beanName = this.readerContext.generateBeanName(beanDefinition);
					// Register an alias for the plain bean class name, if still possible,
					// if the generator returned the class name plus a suffix.
					// This is expected for Spring 1.2/2.0 backwards compatibility.
					String beanClassName = beanDefinition.getBeanClassName();
					if (beanClassName != null &&
							beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() &&
							!this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
						aliases.add(beanClassName);
					}
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Neither XML 'id' nor 'name' specified - " +
							"using generated bean name [" + beanName + "]");
				}
			}
			catch (Exception ex) {
				error(ex.getMessage(), ele);
				return null;
			}
		}
		String[] aliasesArray = StringUtils.toStringArray(aliases);		
		return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
	}

	return null;
}

//BeanDefinitionParserDelegate#parseBeanDefinitionElement
public AbstractBeanDefinition parseBeanDefinitionElement(
		Element ele, String beanName, @Nullable BeanDefinition containingBean) {

	this.parseState.push(new BeanEntry(beanName));
	//解析class属性
	String className = null;
	if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
		className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
	}
	//解析parent属性
	String parent = null;
	if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
		parent = ele.getAttribute(PARENT_ATTRIBUTE);
	}

	try {
		//创建封装在bean信息的AbstractBeanDefinition对象，实际的实现是GenericBeanDefiniton
		AbstractBeanDefinition bd = createBeanDefinition(className, parent);

		//解析bean标签的各种其它属性
		parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
		//设置description信息
		bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));

		//解析元数据
		parseMetaElements(ele, bd);
		//解析lookup-method属性
		parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
		//解析replaced-method属性
		parseReplacedMethodSubElements(ele, bd.getMethodOverrides());

		//解析构造函数
		parseConstructorArgElements(ele, bd);
		//解析property子元素
		parsePropertyElements(ele, bd);
		//解析qualifier子元素
		parseQualifierElements(ele, bd);

		bd.setResource(this.readerContext.getResource());
		bd.setSource(extractSource(ele));

		return bd;
	}
	catch (ClassNotFoundException ex) {
		error("Bean class [" + className + "] not found", ele, ex);
	}
	catch (NoClassDefFoundError err) {
		error("Class that bean class [" + className + "] depends on not found", ele, err);
	}
	catch (Throwable ex) {
		error("Unexpected failure during bean definition parsing", ele, ex);
	}
	finally {
		this.parseState.pop();
	}

	return null;
}

//BeanDefinitionParserDelegate#createBeanDefinition
protected AbstractBeanDefinition createBeanDefinition(@Nullable String className, @Nullable String parentName)
		throws ClassNotFoundException {

	return BeanDefinitionReaderUtils.createBeanDefinition(
			parentName, className, this.readerContext.getBeanClassLoader());
}

//BeanDefinitionReaderUtils#createBeanDefinition
//最终创建了AbstractBeanDefinition的一个子类对象GenericBeanDefinition
public static AbstractBeanDefinition createBeanDefinition(
		@Nullable String parentName, @Nullable String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException {

	GenericBeanDefinition bd = new GenericBeanDefinition();
	bd.setParentName(parentName);
	if (className != null) {
		if (classLoader != null) {
			bd.setBeanClass(ClassUtils.forName(className, classLoader));
		}
		else {
			bd.setBeanClassName(className);
		}
	}
	return bd;
}


//BeanDefinitionReaderUtils#registerBeanDefinition
public static void registerBeanDefinition(
		BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
		throws BeanDefinitionStoreException {

	// Register bean definition under primary name.
	String beanName = definitionHolder.getBeanName();
	//DefaultListableBeanFactory
	registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

	// Register aliases for bean name, if any.
	String[] aliases = definitionHolder.getAliases();
	if (aliases != null) {
		for (String alias : aliases) {
			registry.registerAlias(beanName, alias);
		}
	}
}



//DefaultListableBeanFactory#registerBeanDefinition
private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
/** List of names of manually registered singletons, in registration order. */
private volatile Set<String> manualSingletonNames = new LinkedHashSet<>(16);
/** Names of beans that have already been created at least once. */
private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

//DefaultListableBeanFactory#registerBeanDefinition
public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
		throws BeanDefinitionStoreException {

	Assert.hasText(beanName, "Bean name must not be empty");
	Assert.notNull(beanDefinition, "BeanDefinition must not be null");

	if (beanDefinition instanceof AbstractBeanDefinition) {
		try {
			// 注册前的最后一个校验，这里的校验不同于之前的xml文件校验，主要是对应abstractBeanDefinition属性的methodOverrides校验
			// 校验methodOverrides是否与工厂方法并存或者methodOverrides对应的方法根本不存在
			((AbstractBeanDefinition) beanDefinition).validate();
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
					"Validation of bean definition failed", ex);
		}
	}

	BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
	// 处理注册已经注册的beanName情况
	if (existingDefinition != null) {
		//如果获取到bean之前已经存在，且没有设置可以覆盖属性，则报错
		if (!isAllowBeanDefinitionOverriding()) {
			throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
		}
		else if (existingDefinition.getRole() < beanDefinition.getRole()) {
			// e.g. was ROLE_APPLICATION, now overriding with ROLE_SUPPORT or ROLE_INFRASTRUCTURE
			if (logger.isInfoEnabled()) {
				logger.info("Overriding user-defined bean definition for bean '" + beanName +
						"' with a framework-generated bean definition: replacing [" +
						existingDefinition + "] with [" + beanDefinition + "]");
			}
		}
		else if (!beanDefinition.equals(existingDefinition)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Overriding bean definition for bean '" + beanName +
						"' with a different definition: replacing [" + existingDefinition +
						"] with [" + beanDefinition + "]");
			}
		}
		else {
			if (logger.isTraceEnabled()) {
				logger.trace("Overriding bean definition for bean '" + beanName +
						"' with an equivalent definition: replacing [" + existingDefinition +
						"] with [" + beanDefinition + "]");
			}
		}
		//设置了覆盖属性，则将最新的bean添加进beanDefinitionMap中
		this.beanDefinitionMap.put(beanName, beanDefinition);
	}
	else {
		//如果当前bean已经创建
		if (hasBeanCreationStarted()) {
			// Cannot modify startup-time collection elements anymore (for stable iteration)
			synchronized (this.beanDefinitionMap) {
				this.beanDefinitionMap.put(beanName, beanDefinition);
				List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
				updatedDefinitions.addAll(this.beanDefinitionNames);
				updatedDefinitions.add(beanName);
				this.beanDefinitionNames = updatedDefinitions;
				//移除手动创建的bean
				removeManualSingletonName(beanName);
			}
		}
		else {
			// Still in startup registration phase
			// 注册beanDefinition
			this.beanDefinitionMap.put(beanName, beanDefinition);
			// 记录beanName
			this.beanDefinitionNames.add(beanName);
			removeManualSingletonName(beanName);
		}
		this.frozenBeanDefinitionNames = null;
	}

	if (existingDefinition != null || containsSingleton(beanName)) {
		resetBeanDefinition(beanName);
	}
	else if (isConfigurationFrozen()) {
		clearByTypeCache();
	}
}

//DefaultListableBeanFactory#removeManualSingletonName
private void removeManualSingletonName(String beanName) {
	updateManualSingletonNames(set -> set.remove(beanName), set -> set.contains(beanName));
}

//DefaultListableBeanFactory#updateManualSingletonNames
private void updateManualSingletonNames(Consumer<Set<String>> action, Predicate<Set<String>> condition) {
	if (hasBeanCreationStarted()) {
		// Cannot modify startup-time collection elements anymore (for stable iteration)
		synchronized (this.beanDefinitionMap) {
			if (condition.test(this.manualSingletonNames)) {
				Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
				action.accept(updatedSingletons);
				this.manualSingletonNames = updatedSingletons;
			}
		}
	}
	else {
		// Still in startup registration phase
		if (condition.test(this.manualSingletonNames)) {
			action.accept(this.manualSingletonNames);
		}
	}
}