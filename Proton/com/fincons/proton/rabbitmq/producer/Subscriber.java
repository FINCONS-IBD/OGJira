package com.fincons.proton.rabbitmq.producer;

import com.rabbitmq.client.*;

import jdk.nashorn.internal.parser.JSONParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.fincons.proton.util.PropertiesHelper;
import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.connector.IInputConnector;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter;
import com.ibm.hrl.proton.adapters.formatters.JSONFormatter;
import com.ibm.hrl.proton.adapters.interfaces.AbstractInputAdapter;
import com.ibm.hrl.proton.adapters.interfaces.AdapterException;
import com.ibm.hrl.proton.expression.facade.EepFacade;
import com.ibm.hrl.proton.metadata.inout.ProducerMetadata;
import com.ibm.hrl.proton.metadata.parser.MetadataParser;
import com.ibm.hrl.proton.runtime.event.interfaces.IEventInstance;
import com.ibm.hrl.proton.runtime.metadata.EventMetadataFacade;

public class Subscriber extends AbstractInputAdapter {
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private boolean arrived = false;
	private LinkedList<String> messageList = new LinkedList<String>();

	public String msg_received = "";
	private ITextFormatter textFormatter;

	public Subscriber(ProducerMetadata producerMetadata, IInputConnector serverConnector,
			EventMetadataFacade eventMetadata, EepFacade eep) throws AdapterException {
		super(producerMetadata, serverConnector, eventMetadata);
		textFormatter = new JSONFormatter(producerMetadata.getProducerProperties(), eventMetadata, eep);
	}

	public String getMsg_received() {
		return msg_received;
	}

	public void setMsg_received(String msg) {
		msg_received = msg;
	}

	public void connect()  {
		factory = new ConnectionFactory();
		factory.setUsername(PropertiesHelper.getConnectionConfig().getProperty("rabbitMqUser"));//("EPOSubscriber");
		factory.setPassword(PropertiesHelper.getConnectionConfig().getProperty("rabbitMqPsw"));//("OgzillaEPO17");
		factory.setVirtualHost(PropertiesHelper.getConnectionConfig().getProperty("rabbitMqvHost"));//("Excel_Proton_Ogzilla");
		factory.setHost(PropertiesHelper.getConnectionConfig().getProperty("rabbitMQServer"));//("89.207.106.74");
		factory.setPort(Integer.parseInt(PropertiesHelper.getConnectionConfig().getProperty("rabbitMQPort")));//(5672);// 

		try {
			connection = factory.newConnection();

			channel = connection.createChannel();

			String queueName = PropertiesHelper.getConnectionConfig().getProperty("rabbitMqQueue");// "Excel_Bugs_queue";

			System.out.println(" [*] Waiting for messages. To exit press any key\n");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println(" [x] Received '" + message + "'" + "\n");
					setMsg_received(message);
					messageList.add(message);
					arrived = true;
					//channel.basicNack(envelope.getDeliveryTag(), true, true);//(envelope.getDeliveryTag(), false);
				}
			};
			channel.basicConsume(queueName, true, consumer);
		} catch (IOException e) {
//			e.printStackTrace();
			logger.log(Level.WARNING, e.getMessage(), e);
		} catch (TimeoutException e) {
//			e.printStackTrace();
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	@Override
	public List<IEventInstance> readBatchedData() {
		List<IEventInstance> eventInstances = new ArrayList<IEventInstance>();
		while (messageList.iterator().hasNext()) {
			String allEvents = messageList.iterator().next();
			messageList.remove();
			List<String> eventsList;
			if (allEvents != null && isJSONValid(allEvents)) {
				eventsList = getEventsList(allEvents);
				for (String eventInstanceText : eventsList) {
					eventInstanceText = eventInstanceText.substring(0, eventInstanceText.length() - 1) + ",\"Name\":"
							+ "\"" + "Request" + "\"} ";
					IEventInstance eventInstance;
					try {
						eventInstance = textFormatter.parseText(eventInstanceText);
						eventInstance.setDetectionTime(Calendar.getInstance().getTimeInMillis());
						eventInstances.add(eventInstance);
					} catch (ClassCastException | AdapterException e) {
						eventsList.remove(eventInstanceText);
						logger.warning("Invalid event : " + e.getMessage());
					}
				}
				setMsg_received("");
			}
		}

		return eventInstances;
	}

	@Override
	public IEventInstance readData() throws AdapterException {
		throw new UnsupportedOperationException("File input adapter doesn't support the 'readData' operation");
	}

	@Override
	public void initializeAdapter() throws AdapterException {
		super.initialize();
		try {
			connect();
		} catch (Exception e) {
			logger.warning(e.getMessage());

		}

	}

	@Override
	public void shutdownAdapter() throws AdapterException {
		super.shutdown();
		try {
			connection.close();
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IInputAdapterConfiguration createConfiguration(ProducerMetadata producerMetadata) {
		long delay = 1000;
		long pollingDelay = 1000;
		return new SubscriberConfiguration(delay, pollingDelay);
	}

	protected static List<String> getEventsList(String s) {
		List<String> resultEvents = new ArrayList<String>();
		JSONObject req = null;
		try {
			req = new JSONObject(s);
			JSONArray bugs = req.getJSONArray("bugs");

			for (int i = 0; i < bugs.length(); i++) {
				JSONObject bug = new JSONObject(bugs.optString(i));
				resultEvents.add(bug.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultEvents;
	}

	private boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}
}
