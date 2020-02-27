package com.xzq;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

public class StepVerifierTest {

	public static void main(String[] args) {
		//启用调试，所有的操作符在执行时都会保存额外的与执行链相关的信息
		Hooks.onOperatorDebug();
		//启用检查点进行调试
//		Flux.just(1, 0).map(x -> 1 / x).checkpoint("test").subscribe(System.out::println);
		Flux.range(1, 2).log("Range").subscribe(System.out::println);
		StepVerifier.create(Flux.just(1, 2)).expectNext(1).expectNext(2).verifyComplete();
		//成功-流结束，失败-流不结束
		StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(4), Duration.ofSeconds(2)).take(2))
				.expectSubscription().expectNoEvent(Duration.ofSeconds(4)).expectNext(0L).thenAwait(Duration.ofSeconds(2))
				.expectNext(1L).verifyComplete();
		TestPublisher<Integer> test = TestPublisher.create();
		test.next(1);
		test.next(2);
		test.complete();
		StepVerifier.create(test).expectNext(1).expectNext(2).expectComplete();
	}

}
