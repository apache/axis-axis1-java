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
package samples.integrationGuide.example2;

import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.wsdl.WSDL2Java;
import org.apache.axis.wsdl.gen.Parser;

public class WSDL2Useless extends WSDL2Java {

    protected static final int SONG_OPT = 'g';

    protected static final CLOptionDescriptor[] options = new CLOptionDescriptor[]{
        new CLOptionDescriptor("song",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SONG_OPT,
                "Choose a song for deploy.useless:  work or rum")
    };

    public WSDL2Useless() {
        addOptions(options);
    } // ctor

    protected Parser createParser() {
        return new MyEmitter();
    } // createParser

    protected void parseOption(CLOption option) {
        if (option.getId() == SONG_OPT) {
            String arg = option.getArgument();
            if (arg.equals("rum")) {
                ((MyEmitter) parser).setSong(MyEmitter.RUM);
            }
            else if (arg.equals("work")) {
                ((MyEmitter) parser).setSong(MyEmitter.WORK);
            }
        }
        else {
            super.parseOption(option);
        }
    } // parseOption

    /**
     * Main
     */
    public static void main(String args[]) {
        WSDL2Useless useless = new WSDL2Useless();

        useless.run(args);
    } // main
} // class WSDL2Useless
