/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.ear.controller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.jboss.as.quickstarts.ear.ejb.MessageConsumer3;
import org.jboss.as.quickstarts.mdb.MDBStats;


/**
 * @author bmaxwell
 *
 * @Named defaults to the name of the class with the first letter lower case so this bean can be referred to as greeterBean in a JSF page
 * It is RequestScoped so nothing is maintained beyond the request/response.
 */
@Named(value="statsBean")
@RequestScoped
public class StatsBean {
    
    /**
     * 
     */
    public StatsBean() {
    }
    
    public MDBStats getStats() {
    	return MessageConsumer3.mdbStats;
    }
}
