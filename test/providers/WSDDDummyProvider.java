/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

package test.providers;

import org.apache.axis.*;
import org.apache.axis.deployment.wsdd.*;
import org.apache.axis.deployment.wsdd.WSDDProvider;

/**
 * Factory for WSDDProvider
 */
public class WSDDDummyProvider extends WSDDProvider {

    public static final String NAME = "DUMMY";

  public Handler newProviderInstance(
    WSDDService arg0,
    EngineConfiguration arg1)
    throws Exception {
    return new DummyProvider();
  }

  public String getName() {
    return NAME;
  }
}
