package org.apache.axis.message;

import java.io.*;

public interface MessageWriter {
	public void readWith(MessageReader reader);
}