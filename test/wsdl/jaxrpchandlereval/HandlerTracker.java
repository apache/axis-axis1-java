package test.wsdl.jaxrpchandlereval;

import java.util.*;

public class HandlerTracker {
	private static List clientHandlers;
	private static List serverHandlers;

	public static void init() {
		clientHandlers = new ArrayList();
		serverHandlers = new ArrayList();
	}

	public static void addClientHandler(String s) {
		clientHandlers.add(s);
	}

	public static void addServerHandler(String s) {
		serverHandlers.add(s);
	}

	public static void assertClientHandlerOrder(String[] arr) throws Exception {
		assertHandlerOrder(clientHandlers, arr);
	}

	public static void assertServerHandlerOrder(String[] arr) throws Exception {
		assertHandlerOrder(serverHandlers, arr);
	}

	public static void assertHandlerOrder(List handlers, String[] expected) throws Exception {
		String[] actual = new String[handlers.size()];
		handlers.toArray(actual);

		System.out.print("excepted order:");
		for (int i = 0; i < expected.length; i++) {
			System.out.print(expected[i] + " ");
		}
		System.out.println("\n");

		System.out.print("actual order:");
		for (int i = 0; i < actual.length; i++) {
			System.out.print(actual[i] + " ");
		}
		System.out.println("\n");
		
		if (expected.length != actual.length) {
			throw new Exception("Handler length not match");
		}

		System.out.println("\n");
		for (int i = 0; i < expected.length; i++) {
			if (!expected[i].equals(actual[i])) {
				throw new Exception("Handler order not match : expected = " + expected[i] + ", actual = " + actual[i]);
			}
		}
		return;
	}

}
