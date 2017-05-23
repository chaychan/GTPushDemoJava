package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.gexin.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style6;

public class PushUtils {

	private static String appId = "0SkbWZcgrV8Rze7NkAbKJ7";
	private static String appKey = "JrtaNbM2yB8b37vwPOVtD5";
	private static String masterSecret = "fg22MrfdhBAkLbl50llcr8";
	private static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	public static IGtPush sPush = new IGtPush(host, appKey, masterSecret);;

	/**
	 * 根据别名推送通知
	 * 
	 * @param alias
	 *            别名 使用用户名
	 * @param title
	 *            标题
	 * @param text
	 *            内容
	 */
	private static void pushNotificationByAlias(String alias, String title,
			String text) {
		NotificationTemplate template = notificationTemplateDemo(title, text);
		SingleMessage message = new SingleMessage();
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(72 * 3600 * 1000);
		message.setData(template);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		Target target = new Target();
		target.setAppId(appId);
		target.setAlias(alias);
		IPushResult ret = null;
		try {
			ret = sPush.pushMessageToSingle(message, target);
		} catch (RequestException e) {
			e.printStackTrace();
			ret = sPush.pushMessageToSingle(message, target, e.getRequestId());
		}
		if (ret != null) {
			System.out.println(ret.getResponse().toString());
		} else {
			System.out.println("个推服务器响应异常");
		}
	}

	private static NotificationTemplate notificationTemplateDemo(String title,
			String text) {
		NotificationTemplate template = new NotificationTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(1);
		// template.setTransmissionContent("请输入您要透传的内容");
		// 设置定时展示时间
		// template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
		Style6 style = new Style6();
		// 设置通知栏标题与内容
		style.setTitle(title);
		style.setText(text);
		style.setBigStyle2(text);

		// 配置通知栏图标
		style.setLogo("");
		// 配置通知栏网络图标
		style.setLogoUrl("");
		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		template.setStyle(style);
		return template;
	}

	/**
	 * 推送通知栏类型的透传消息
	 * 
	 * @param alia
	 *            别名
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param orderId
	 *            订单号，没有传null
	 * @param pageNum
	 *            打开页面的编号，PushConstants中有对应页面的编号
	 */
	public static void pushNotificationMessage(String alia, String title,
			String content, String orderId, int pageNum) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put(PushConstants.TITLE, title);
		jsonObject.put(PushConstants.CONTENT, content);
		jsonObject.put(PushConstants.CONTENT_ID, orderId);
		jsonObject.put(PushConstants.PAGE_NUMBER, pageNum);
		jsonObject.put(PushConstants.IS_NOTIFICATION, 1);

		List<String> alias = new ArrayList<String>();
		alias.add(alia);
		pushMessage(alias, jsonObject);
	}

	/**
	 * 推送信息提醒
	 * 
	 * @param alias
	 *            别名
	 * @param unReadCount
	 *            未读数
	 * @param notifyType
	 *            未读消息的类型,PushConstants中有对应提醒类型
	 */
	public static void pushNotifyMessage(List<String> alias, int notifyType) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(PushConstants.NOTIFY_TYPE, notifyType);

		pushMessage(alias, jsonObject);
	}

	/**
	 * 推送透传消息
	 * 
	 * @param alias
	 *            别名
	 * @param template
	 *            透传消息的模板
	 */
	private static void pushMessage(List<String> alias, JSONObject json) {
		if (alias == null || alias.size() == 0) {
			return;
		}

		TransmissionTemplate template = getTemplate(json);

		ListMessage message = new ListMessage();
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(72 * 3600 * 1000);
		message.setData(template);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);

		List<Target> targets = new ArrayList<Target>();
		// 遍历别名
		for (String alia : alias) {
			Target target = new Target();
			target.setAppId(appId);
			target.setAlias(alia);
			targets.add(target);
		}

		String taskId = sPush.getContentId(message);
		IPushResult ret = sPush.pushMessageToList(taskId, targets);

		if (ret != null) {
			System.out.println(ret.getResponse().toString());
		} else {
			System.out.println("个推服务器响应异常");
		}
	}

	/**
	 * 透传消息的模板
	 * 
	 * @param message
	 * @return
	 */
	public static TransmissionTemplate getTemplate(JSONObject json) {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(2);
		template.setTransmissionContent(json.toString());
		// 设置定时展示时间
		// template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");

		APNPayload payload = new APNPayload();

		String title = json.getString(PushConstants.TITLE);
		String body = json.getString(PushConstants.CONTENT);
		if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(body)) {
			payload.setAlertMsg(getDictionaryAlertMsg(title, body));
		}

		// 在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
		payload.setAutoBadge("+1");
		payload.setContentAvailable(1);
		payload.setSound("default");
		payload.setCategory("$由客户端定义");

		Set<Entry<String, Object>> entrySet = json.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			payload.addCustomMsg(entry.getKey(), entry.getValue());
		}

		template.setAPNInfo(payload);

		return template;
	}

	private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(
			String title, String body) {
		APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
		alertMsg.setBody(body);
		alertMsg.setActionLocKey("ActionLockey");
		alertMsg.setLocKey("LocKey");
		alertMsg.addLocArg("loc-args");
		alertMsg.setLaunchImage("launch-image");
		// iOS8.2以上版本支持
		alertMsg.setTitle(title);
		alertMsg.setTitleLocKey("TitleLocKey");
		alertMsg.addTitleLocArg("TitleLocArg");
		return alertMsg;
	}

	public static void main(String[] args) throws InterruptedException {
		Thread.sleep(5000);
		/*PushUtils.pushNotificationMessage("chaychan", "支付成功",
				"您在XXX店铺消费了XXX.00元，多谢回顾，点击查看订单详情", "521",
				PushConstants.PAGE_ORDER_DETAIL);*/
		
		PushUtils.pushNotificationMessage("chaychan", "生日快乐",
				"感谢您对我们的支持，今天是您的生日，特向您发来祝福", "521",
				PushConstants.PAGE_MESSAGE_CENTER);
		/*
		 * List<String> alias = new ArrayList<String>(); alias.add("chaychan");
		 * PushUtils
		 * .pushNotifyMessage(alias,PushConstants.MESSAGE_CENTER_NOTIFY);
		 */
	}
	
	
}
