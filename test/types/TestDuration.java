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
package test.types;

import junit.framework.TestCase;
import org.apache.axis.types.Duration;

public class TestDuration extends TestCase
  {

  public TestDuration( String name )
    {
    super( name );
    }

    
  public void testDurations()
    throws Exception
    {
    // invoke the web service as if it was a local java object
    String[] durationStrings = new String[ 11 ];
    durationStrings[ 0 ] = "P2Y3M8DT8H1M3.3S";
    durationStrings[ 1 ] = "P2Y3M8DT8H1M3S";
    durationStrings[ 2 ] = "PT8H1M3.3S";
    durationStrings[ 3 ] = "P2Y3M8D";
    durationStrings[ 4 ] = "P2YT8H";
    durationStrings[ 5 ] = "P8DT3.3S";
    durationStrings[ 6 ] = "P3MT1M";
    durationStrings[ 7 ] = "PT0.3S";
    durationStrings[ 8 ] = "P1M";
    durationStrings[ 9 ] = "-P1M";
    durationStrings[ 10 ] = "-P2Y3M8DT8H1M3.3S";

    for( int i = 0; i < durationStrings.length; i++ )
      {
      String durationString = durationStrings[ i ];
      Duration duration = 
              new Duration( durationString );

      assertTrue( "Duration string \"" + durationString + 
                  "\" not equal to returned: " + duration.toString(), 
                  durationString.equals( duration.toString() ) );
      }
    }
  }
