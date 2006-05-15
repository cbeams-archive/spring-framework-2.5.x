/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractComponentDefinition;
import org.springframework.util.Assert;

/**
 * @author Rob Harrop
 * @since 2.0
 */
public class AdvisorComponentDefinition extends AbstractComponentDefinition {

	private final BeanDefinition pointcutDefinition;

	private final BeanDefinition advisorDefinition;

	private final String advisorBeanName;

	private String description;

	private RuntimeBeanReference[] beanReferences;

	private BeanDefinition[] beanDefinitions;

	private String expression;

	public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition) {
		 this(advisorBeanName, advisorDefinition, null);
	}

	public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition, BeanDefinition pointcutDefinition) {
		Assert.notNull(advisorDefinition, "'advisorDefinition' cannot be null.");
		Assert.notNull(advisorBeanName, "'advisorBeanName' cannot be null.");
		this.advisorBeanName = advisorBeanName;
		this.advisorDefinition = advisorDefinition;
		this.pointcutDefinition = pointcutDefinition;
		unwrapDefinitions(advisorDefinition, pointcutDefinition);
	}

	private void unwrapDefinitions(BeanDefinition advisorDefinition, BeanDefinition pointcutDefinition) {
		MutablePropertyValues propertyValues = advisorDefinition.getPropertyValues();

		// grab the advice reference
		RuntimeBeanReference adviceReference = (RuntimeBeanReference) propertyValues.getPropertyValue("advice").getValue();

		if (pointcutDefinition == null) {
			RuntimeBeanReference pointcutReference = (RuntimeBeanReference) propertyValues.getPropertyValue("pointcut").getValue();
			this.beanReferences = new RuntimeBeanReference[]{adviceReference, pointcutReference};
			this.beanDefinitions = new BeanDefinition[]{this.advisorDefinition};
			this.description = createDescription(adviceReference, pointcutReference);
		}
		else {
			this.beanReferences = new RuntimeBeanReference[]{adviceReference};
			this.beanDefinitions = new BeanDefinition[]{this.advisorDefinition, pointcutDefinition};
			this.description = createDescription(adviceReference, pointcutDefinition);
		}
	}

	private String createDescription(RuntimeBeanReference adviceReference, BeanDefinition pointcutDefinition) {
		return new StringBuilder().append("Advisor <advice(ref)='")
						.append(adviceReference.getBeanName())
						.append("', pointcut(expression)='")
						.append(pointcutDefinition.getPropertyValues().getPropertyValue("expression").getValue())
						.append("'>").toString();
	}

	private String createDescription(RuntimeBeanReference adviceReference, RuntimeBeanReference pointcutReference) {
		return new StringBuilder().append("Advisor <advice(ref)='")
						.append(adviceReference.getBeanName())
						.append("', pointcut(ref)='")
						.append(pointcutReference.getBeanName())
						.append("'>").toString();
	}

	public String getName() {
		return this.advisorBeanName;
	}

	public String getDescription() {
		return this.description;
	}

	public BeanDefinition[] getBeanDefinitions() {
		return this.beanDefinitions;
	}

	public RuntimeBeanReference[] getBeanReferences() {
		return this.beanReferences;
	}

	public Object getSource() {
		return this.advisorDefinition.getSource();
	}
}
