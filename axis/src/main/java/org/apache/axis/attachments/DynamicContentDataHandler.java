/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.axis.attachments;

import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 * To be used with writing out DIME Attachments.
 * 
 * AXIS will use DIME record chunking.
 * 
 * @author Marc Dumontier (mrdumont@blueprint.org)
 * 
 */
public class DynamicContentDataHandler extends DataHandler {

	int chunkSize = 1*1024*1024;
	
	/**
	 * @param arg0
	 */
	public DynamicContentDataHandler(DataSource arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DynamicContentDataHandler(Object arg0, String arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public DynamicContentDataHandler(URL arg0) {
		super(arg0);
	}

	/**
	 * Get the DIME record chunk size
	 * @return The value
	 */
	public int getChunkSize() {
		return chunkSize;
	}
	
	/**
	 * Set the DIME record chunk size
	 * @param chunkSize The value.
	 */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
}
