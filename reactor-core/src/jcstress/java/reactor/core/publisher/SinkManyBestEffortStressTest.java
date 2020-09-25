/*
 * Copyright (c) 2020-Present Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;
import org.reactivestreams.Subscription;

import static org.openjdk.jcstress.annotations.Expect.*;

public abstract class SinkManyBestEffortStressTest {

	@JCStressTest
	@Outcome(id = {"6"}, expect = ACCEPTABLE, desc = "Six parallel subscribes")
	@State
	public static class ParallelSubscribersStressTest {

		final SinkManyBestEffort<Integer> sink = SinkManyBestEffort.createBestEffort();

		@Actor
		public void one() {
			sink.subscribe(new StressSubscriber<>());
		}

		@Actor
		public void two() {
			sink.subscribe(new StressSubscriber<>());
		}

		@Actor
		public void three() {
			sink.subscribe(new StressSubscriber<>());
		}

		@Actor
		public void four() {
			sink.subscribe(new StressSubscriber<>());
		}

		@Actor
		public void five() {
			sink.subscribe(new StressSubscriber<>());
		}

		@Actor
		public void six() {
			sink.subscribe(new StressSubscriber<>());
		}

		@Arbiter
		public void arbiter(I_Result r) {
			r.r1 = sink.currentSubscriberCount();
		}
	}

	@JCStressTest
	@Outcome(id = {"0"}, expect = ACCEPTABLE, desc = "No subscriber counted")
	@State
	public static class CancelOnSubscribeOneSubscribersStressTest {

		final SinkManyBestEffort<Integer> sink = SinkManyBestEffort.createBestEffort();

		@Actor
		public void one() {
			sink.subscribe(new BaseSubscriber<Integer>() {
				@Override
				protected void hookOnSubscribe(Subscription subscription) {
					subscription.cancel();
				}
			});
		}

		@Arbiter
		public void arbiter(I_Result r) {
			r.r1 = sink.currentSubscriberCount();
		}
	}

	@JCStressTest
	@Outcome(id = {"1"}, expect = ACCEPTABLE, desc = "One subscriber counted")
	@State
	public static class CancelOnSubscribeTwoSubscribersStressTest {

		final SinkManyBestEffort<Integer> sink = SinkManyBestEffort.createBestEffort();

		@Actor
		public void one() {
			sink.subscribe(new StressSubscriber<>(0));
		}

		@Actor
		public void two() {
			sink.subscribe(new BaseSubscriber<Integer>() {
				@Override
				protected void hookOnSubscribe(Subscription subscription) {
					subscription.cancel();
				}
			});
		}

		@Arbiter
		public void arbiter(I_Result r) {
			r.r1 = sink.currentSubscriberCount();
		}
	}

	@JCStressTest
	@Outcome(id = {"0"}, expect = ACCEPTABLE, desc = "No subscriber counted")
	@State
	public static class CancelledBeforeSubscribeOneSubscriberStressTest {

		final SinkManyBestEffort<Integer> sink = SinkManyBestEffort.createBestEffort();
		final StressSubscriber<Integer> sub = new StressSubscriber<>(0);

		@Actor
		public void cancellingSub() {
			sub.cancel();
		}

		@Actor
		public void subscribingSub() {
			sink.subscribe(sub);
		}

		@Arbiter
		public void arbiter(I_Result r) {
			r.r1 = sink.currentSubscriberCount();
		}
	}

	@JCStressTest
	@Outcome(id = {"1"}, expect = ACCEPTABLE, desc = "One subscriber counted")
	@State
	public static class CancelledBeforeSubscribeTwoSubscribersStressTest {

		final SinkManyBestEffort<Integer> sink = SinkManyBestEffort.createBestEffort();
		final StressSubscriber<Integer> sub2 = new StressSubscriber<>(0);

		@Actor
		public void subscribingSub1() {
			sink.subscribe(new StressSubscriber<>(0));
		}

		@Actor
		public void subscribingSub2() {
			sink.subscribe(sub2);
		}

		@Actor
		public void cancellingSub2() {
			sub2.cancel();
		}

		@Arbiter
		public void arbiter(I_Result r) {
			r.r1 = sink.currentSubscriberCount();
		}
	}

}