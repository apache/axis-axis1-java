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

package org.apache.axis.components.threadpool;

import org.apache.axis.AxisProperties;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public abstract class TaskManagerFactory {

    protected static Log log = 
        LogFactory.getLog(TaskManagerFactory.class.getName());

    static {
        AxisProperties.setClassOverrideProperty(
                TaskManager.class, 
                "axis.TaskManager");
        AxisProperties.setClassDefault(
                TaskManager.class, 
                "org.apache.axis.components.threadpool.SimpleTaskManager");
    }

    public static void setDefault(String taskManagerClass) {
        AxisProperties.setClassDefault(TaskManager.class, taskManagerClass);
    }

    /**
     * Returns an instance of TaskManager.
     */
    public static TaskManager getTaskManager() {
        TaskManager taskManager= (TaskManager) AxisProperties.newInstance(TaskManager.class);
        log.debug("axis.TaksManager: " + taskManager);
        return taskManager;
    }
}
