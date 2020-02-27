package com.xzq;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

import reactor.core.publisher.Flux;

public class FluxTest {

	public static void main(String[] args) throws InterruptedException {
		Flux.just("hello", "world").subscribe(System.out::println);
		Flux.fromArray(new Integer[] {1, 2, 3}).subscribe(System.out::println);
		Flux.empty().subscribe(System.out::println);
		Flux.range(1, 10).subscribe(System.out::println);
//		Flux.interval(Duration.of(2, ChronoUnit.SECONDS)).subscribe(System.out::println);
		//通过同步和逐一的方式来产生 Flux 序列。序列的产生是通过调用所提供的 SynchronousSink 对象的 next()，complete()和 error(Throwable)方法来完成的。
		//next()方法只能最多被调用一次
		Flux.generate(sink -> {
			sink.next(5);
			sink.complete();
		}).subscribe(System.out::println);
		Random random = new Random();
		Flux.generate(ArrayList::new, (list, sink) -> {
			int value = random.nextInt(100);
		    list.add(value);
		    sink.next(value);
		    if (list.size() == 10) {
		        sink.complete();
		    }
		    return list;
		}).subscribe(System.out::println);
		//可以在一次调用中产生多个元素，即多次调用next
		Flux.create(sink -> {
			sink.next(5);
			sink.next(6);
			sink.complete();
		}).subscribe(System.out::println);
		//热序列，订阅者只能获取订阅后的数据
		Flux<Long> source = Flux.interval(Duration.of(1, ChronoUnit.SECONDS)).take(10).publish().autoConnect();
		source.subscribe();
		Thread.sleep(5000);
		source.toStream().forEach(System.out::println);
	} 
	
}
