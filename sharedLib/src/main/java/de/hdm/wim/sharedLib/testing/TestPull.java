package de.hdm.wim.sharedLib.testing;

import com.google.cloud.pubsub.spi.v1.MessageReceiver;
import com.google.pubsub.v1.Subscription;
import de.hdm.wim.sharedLib.Constants.PubSub.Config;
import de.hdm.wim.sharedLib.Constants.PubSub.Topic.TOPIC_1;
import de.hdm.wim.sharedLib.pubsub.helper.SubscriptionHelper;
import de.hdm.wim.sharedLib.pubsub.receiver.ExampleReceiver;

/**
 * TestPull
 * this class is only for testing purposes!
 *
 * @author Benedikt Benz
 * @createdOn 11.06.2017
 * @deprecated
 */
public class TestPull {

	public static void main(String[] args) throws Exception {

		MessageReceiver receiver = new ExampleReceiver();

		// init a SubscriptionHelper to use for prod environment, without REST and for the given project
		SubscriptionHelper sh = new SubscriptionHelper(false, Config.APP_ID);

		/**
		 * this will create a subscription with id: "subscription-pull-topic-1-test1"
		 * if the subscription already exists, we will use it
		 *
		 * make sure to create the subscription(s) BEFORE publishing
		 * this example will work because the subscriptions already exist
		 */
		Subscription subscription = sh.CreatePullSubscription(TOPIC_1.TOPIC_ID, "test1");

		// subscribe using the existing subscription
		sh.Subscribe(subscription, receiver);
	}
}
