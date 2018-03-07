package com.fincons.proton.rabbitmq.producer;

import com.ibm.hrl.proton.adapters.configuration.IInputAdapterConfiguration;
import com.ibm.hrl.proton.adapters.formatters.ITextFormatter.TextFormatterType;

public class SubscriberConfiguration implements IInputAdapterConfiguration {
	private long delay; // in milisecs
	private long pollingDelay;

	public SubscriberConfiguration(long delay, long pollingDelay) {
		super();
		this.delay = delay;
		this.pollingDelay = pollingDelay;

	}

	public TextFormatterType getFileFormatterType() {
		return TextFormatterType.JSON;
	}

	@Override
	public InputAdapterPullModeEnum getPollMode() {
		return InputAdapterPullModeEnum.BATCH;
	}

	@Override
	public long getPollingDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getSendingDelay() {
		// TODO Auto-generated method stub
		return 0;
	}
}
