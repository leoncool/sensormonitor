import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

public class SerialTest implements SerialPortEventListener {
	SerialPort serialPort;
	/** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
																				// OS
																				// X
			"/dev/ttyUSB0", // Linux
			"COM7", // Windows
	};
	/** Buffered input stream from the port */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
					.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}
//
//	Hashtable<Date, String> tempList = new Hashtable<Date, String>();
//	Hashtable<Date, String> lightList = new Hashtable<Date, String>();
	LinkedList<DataWrap> tempList = new LinkedList<DataWrap>();
	LinkedList<DataWrap> lightList = new LinkedList<DataWrap>();
	LinkedList<DataWrap> humidityList = new LinkedList<DataWrap>();
	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				if (input.available() > 20) {
					int available = input.available();
					byte chunk[] = new byte[available];
					input.read(chunk, 0, available);

					// Displayed results are codepage dependent
					String data = new String(chunk);

					// System.out.println(data);
				
					
					for (int i = 0; i < data.length(); i++) {
						if (data.charAt(i) == '<' && (i + 1) < data.length()
								&& data.charAt(i + 1) == 'T') {
							String tempStr = "";
							int j = (i + 2);
							boolean valid = false;
							for (; j < data.length(); j++) {
								if (data.charAt(j) != '>'
										&& data.charAt(j) != 'T'
										&& data.charAt(j) != 'L'
										&& data.charAt(j) != 'H'
										&& data.charAt(j) != '<') {
									tempStr = tempStr + data.charAt(j);
	
								} else if (data.charAt(j) == '>') {
									valid = true;
									break;
								} else {
									break;
								}
							}
							if (valid) {
								System.out.println("templerate:" + tempStr);
								DataWrap data_wrap=new DataWrap(new Date(), tempStr);
								tempList.push(data_wrap);
							}
						} else if (data.charAt(i) == '<'
								&& (i + 1) < data.length()
								&& data.charAt(i + 1) == 'L') {
							String lightStr = "";
							boolean valid = false;
							int j = (i + 2);
							for (; j < data.length(); j++) {
								if (data.charAt(j) != '>'
										&& data.charAt(j) != 'T'
										&& data.charAt(j) != 'L'
										&& data.charAt(j) != 'H'
										&& data.charAt(j) != '<') {
									lightStr = lightStr + data.charAt(j);

								} else if (data.charAt(j) == '>') {
									valid = true;
									break;
								} else {
									continue;
								}
							}
							if (valid) {
								System.out.println("light:" + lightStr);

								DataWrap data_wrap=new DataWrap(new Date(), lightStr);
								lightList.push(data_wrap);
							}
						} else if (data.charAt(i) == '<'
								&& (i + 1) < data.length()
								&& data.charAt(i + 1) == 'H') {
							String humidityStr = "";
							boolean valid = false;
							int j = (i + 2);
							for (; j < data.length(); j++) {
								if (data.charAt(j) != '>'
										&& data.charAt(j) != 'T'
										&& data.charAt(j) != 'L'
										&& data.charAt(j) != 'H'
										&& data.charAt(j) != '<') {
									humidityStr = humidityStr + data.charAt(j);

								} else if (data.charAt(j) == '>') {
									valid = true;
									break;
								} else {
									continue;
								}
							}
							if (valid) {
								System.out.println("Humidity:" + humidityStr);

								DataWrap data_wrap=new DataWrap(new Date(), humidityStr);
								humidityList.push(data_wrap);
							}
						}
					}

				}

				if (tempList.size() > 10) {
					TestSendDatapoints testsend=new TestSendDatapoints();
					testsend.PostData("feb6f678-a518-4d57-b0c3-f8ae34e22354", tempList);
				}
				if (humidityList.size() > 10) {
					TestSendDatapoints testsend=new TestSendDatapoints();
					testsend.PostData("2dec377f-7555-49aa-90fc-a631a7e60af5", humidityList);
				}
				if (lightList.size() > 10) {
					TestSendDatapoints testsend=new TestSendDatapoints();
					testsend.PostData("ea5d843d-9fe8-409b-ad85-90c05066742b", lightList);
				}
				
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
	}

	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		System.out.println("Started");
	}
}