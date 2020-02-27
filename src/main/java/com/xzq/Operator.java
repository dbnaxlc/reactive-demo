package com.xzq;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Operator {

	public static void main(String[] args) {
//		buffer();
//		Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);
//		window();
//		zipwith();
//		reduce();
//		merge();
//		Flux.just(5, 10)
//        .flatMap(x -> Flux.interval(Duration.ofMillis(x * 10), Duration.ofMillis(100)).take(x))
//        .toStream()
//        .forEach(System.out::println);
//		Flux.just(5, 10)
//        .concatMap(x -> Flux.interval(Duration.ofMillis(x * 10), Duration.ofMillis(100)).take(x))
//        .toStream()
//        .forEach(System.out::println);
//		Flux.combineLatest(
//		        Arrays::toString,
//		        Flux.interval(Duration.ofMillis(100)).take(5),
//		        Flux.interval(Duration.ofMillis(50), Duration.ofMillis(100)).take(5)
//		).toStream().forEach(System.out::println);
//		onError();
		schedule();
	}

	public static void buffer() {
		Flux.range(1, 100).buffer(20).subscribe(System.out::println);
		Flux.interval(Duration.ofMillis(100)).buffer(Duration.ofMillis(1000)).take(2).toStream().forEach(i -> {
			System.out.println(i + " : " + System.currentTimeMillis());
		});
		// 收集所有元素
		Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
		// 当 Predicate 返回 true 时才会收集
		Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
	}

	public static void window() {
		Flux.range(1, 100).window(20).subscribe(System.out::println);
		Flux.interval(Duration.ofMillis(100)).window(Duration.ofMillis(1000)).take(2).toStream()
				.forEach(System.out::println);
	}

	public static void zipwith() {
		Flux.just("a", "b").zipWith(Flux.just("c", "d", "e")).subscribe(System.out::println);
		Flux.just("a", "b").zipWith(Flux.just("c", "d", "e"), (s1, s2) -> s1 + "-" + s2).subscribe(System.out::println);
	}

	public static void reduce() {
		Flux.range(1, 100).reduce((s1, s2) -> s1 + s2).subscribe(System.out::println);
		Flux.range(1, 100).reduceWith(() -> 1999, (s1, s2) -> s1 + s2).subscribe(System.out::println);
	}

	public static void merge() {
		// merge按照所有流中元素的实际产生顺序来合并
		Flux.merge(Flux.interval(Duration.ofMillis(100)).take(5), Flux.interval(Duration.ofMillis(50)).take(5))
				.toStream().forEach(System.out::println);
		// mergeSequential按照所有流被订阅的顺序，以流为单位进行合并
		Flux.mergeSequential(Flux.interval(Duration.ofMillis(100)).take(5),
				Flux.interval(Duration.ofMillis(50)).take(5)).toStream().forEach(System.out::println);
	}

	public static void onError() {
		Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).onErrorReturn(0)
				.subscribe(System.out::println);
		Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).onErrorResume(e -> {
			if (e instanceof IllegalStateException) {
				return Mono.just(5);
			} else if (e instanceof IllegalArgumentException) {
				return Mono.just(-1);
			}
			return Mono.empty();
		}).subscribe(System.out::println);
		// 重试
		Flux.just(1, 2).concatWith(Mono.error(new IllegalStateException())).retry(1).subscribe(System.out::println);
	}

	public static void schedule() {
		Flux.create(sink -> {
			sink.next(Thread.currentThread().getName() + "t");
			sink.complete();
		}).publishOn(Schedulers.single()).map(x -> Thread.currentThread().getName() + "-" + x)
				.publishOn(Schedulers.elastic()).map(x -> Thread.currentThread().getName() + "-" + x)
				.subscribeOn(Schedulers.parallel()).toStream().forEach(System.out::println);
	}
}
