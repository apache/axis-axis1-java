package samples.userguide.example4;

import java.io.*;
import java.util.Date;
import org.apache.axis.*;
import org.apache.axis.handlers.BasicHandler;

public class LogHandler extends BasicHandler {
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        /** Log an access each time we get invoked.
         */
        try {
            Handler serviceHandler = msgContext.getServiceHandler();
            String filename = (String)serviceHandler.getOption("filename");
            FileOutputStream fos = new FileOutputStream(filename, true);
            
            PrintWriter writer = new PrintWriter(fos);
            
            Integer numAccesses =
                             (Integer)serviceHandler.getOption("accesses");
            numAccesses = new Integer(numAccesses.intValue() + 1);
            
            Date date = new Date();
            String result = date + ": service " +
                            msgContext.getTargetService() +
                            " accessed " + numAccesses + " time(s).";
            serviceHandler.addOption("accesses", numAccesses);
            
            writer.println(result);
            
            writer.close();
        } catch (Exception e) {
            throw new AxisFault(e);
        }
    }
    
    public void undo(MessageContext msgContext)
    {
    }
}