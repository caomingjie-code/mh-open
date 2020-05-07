package com.mh.base.mq.msgconverter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import com.mh.base.mq.msgdata.MQMsgImpl;
import com.mh.base.utils.protostuff.ProtostuffUtils;

/**
 * 信息类型转换
 * @author cmj
 *
 */
public class MHMsgConverter implements MessageConverter{

	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
		byte[] encode = ProtostuffUtils.encode(object);
		Message message = new Message(encode, messageProperties);
		return message;
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		byte[] body = message.getBody();
		MQMsgImpl decode = ProtostuffUtils.decode(body, MQMsgImpl.class);
		return decode;
	}

}
