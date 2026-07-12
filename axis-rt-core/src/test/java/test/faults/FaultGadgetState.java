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
package test.faults;

/**
 * Holder for the {@link FaultGadgetProbe} instrumentation flags. Kept in a
 * separate class so that a test can inspect the flags without itself
 * triggering initialization of {@link FaultGadgetProbe} (which would run the
 * probe's static initializer and defeat the point of the check).
 */
public class FaultGadgetState {
    public static volatile boolean staticInitRan = false;
    public static volatile boolean constructorRan = false;

    public static void reset() {
        staticInitRan = false;
        constructorRan = false;
    }
}
