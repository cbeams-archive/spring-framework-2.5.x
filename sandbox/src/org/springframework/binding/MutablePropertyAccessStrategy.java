/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.binding;

import java.beans.PropertyEditor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.binding.value.BoundValueModel;
import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.binding.value.ValueModel;

/**
 * <p>
 * An extension of the base property access strategy interface that allows for
 * mutable operations. Specifically, this interface allows:
 * </p>
 * <ul>
 * <li>registering custom property editors for performing type conversions
 * <li>returning a domain object holder allowing the underying domain object to
 * be changed and subscribed to for modification, and
 * <li>adding listeners for changes on particular properties.
 * </ul>
 * EXPERIMENTAL - not yet fit for general use
 * @author Keith Donald
 */
public interface MutablePropertyAccessStrategy extends PropertyAccessStrategy,
        PropertyAccessor, PropertyChangePublisher {
    public void registerCustomEditor(Class propertyType,
            PropertyEditor propertyEditor);

    public void registerCustomEditor(String propertyName,
            PropertyEditor propertyEditor);

    public PropertyEditor findCustomEditor(String propertyName);

    public BoundValueModel getDomainObjectHolder();

    public BoundValueModel getPropertyValueModel(String propertyPath)
            throws BeansException;

    public MutablePropertyAccessStrategy getPropertyAccessStrategyForPath(
            String propertyPath) throws BeansException;

    public MutablePropertyAccessStrategy newPropertyAccessStrategy(
            ValueModel domainObjectHolder);

}